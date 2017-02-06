package nexnet.com.solution.call;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import nexnet.com.solution.service.AutoAnswerCallUtil;
import nexnet.com.solution.service.CallLogManager;
import nexnet.com.solution.R;
import com.m800.msme.api.M800Call;
import com.m800.msme.api.M800CallDelegate;
import com.m800.msme.api.M800IncomingCall;
import com.m800.msme.jni.EMsmeMediaType;
import com.m800.sdk.M800SDK;

import java.util.Map;

import javax.annotation.Nonnull;


/*
 * Activity for user to reject or answer the call
 */
public class CallAnswerScreenActivity extends Activity implements M800CallDelegate  {

    private final static String TAG = CallAnswerScreenActivity.class.getSimpleName();

    public final static String EXTRA_KEY_CALL_ID = "callID";

    private PowerManager.WakeLock fullWakeLock;
    private M800IncomingCall mCall;

    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_answer_screen);

        createWakeLocks();
        wakeDevice();

        // Get call object
        String callID = getIntent().getStringExtra(EXTRA_KEY_CALL_ID);
        try {
            mCall = (M800IncomingCall) M800SDK.getInstance().getRealtimeClient().getCall(callID);
        } catch(Exception e) {
            // Failed to get call
        }

        if (mCall == null){
            Log.e(TAG, "Cannot get call session with callId:" + callID);
            finish();
            return;
        }

        // Add self as delegate to receive call events.
        mCall.addCallDelegate(this);

        //Put the name
        String name = getIntent().getStringExtra("name");
        TextView textView = (TextView) findViewById(R.id.textViewJID);
        textView.setText(name);

        findViewById(R.id.buttonAnswerCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Answer a call
                mCall.answer();
                startCallScreen();
            }
        });

        findViewById(R.id.buttonEndCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reject the call
                mCall.reject("Rejected by User");
                finish();
            }
        });

        if (AutoAnswerCallUtil.getInstance().isStarted()){
            AutoAnswerCallUtil.getInstance().trackCallAnswering(mCall);
            mCall.answer();
            startCallScreen();
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (fullWakeLock.isHeld()) {
            fullWakeLock.release();
        }
        if (mCall != null) {
            mCall.removeCallDelegate(this);
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Forbid the back key
        return keyCode != KeyEvent.KEYCODE_BACK && super.onKeyDown(keyCode, event);
    }

    // Called from onCreate
    private void createWakeLocks() {
        if (fullWakeLock == null) {
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            fullWakeLock = powerManager.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                            | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP),
                    "Loneworker - FULL WAKE LOCK");
        }
    }

    private void wakeDevice() {
        Log.d(TAG, "wakeDevice");
        fullWakeLock.acquire();
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
        keyguardLock.disableKeyguard();
    }

    private void startCallScreen() {
        Log.d(TAG, "startCallScreen");
        Intent intent = new Intent();
        intent.setClass(this, CallScreenActivity.class);
        intent.putExtra(CallScreenActivity.EXTRA_KEY_CALL_ID, mCall.callID());
        intent.putExtra("counter", false);
        startActivity(intent);
        finish();
    }

    @Override
    public void callTerminated(M800Call call, int status, Map<String, String> userInfo) {
        Log.d(TAG, "<callTerminated> status:" + status + " userInfo:" + userInfo);
        CallLogManager.getInstance().saveCallLog(call);
        finish();
    }

    @Override public void callHoldByLocal(M800Call call) {}
    @Override public void callUnholdByLocal(M800Call call) {}
    @Override public void callHoldByRemote(M800Call call) {}
    @Override public void callUnHoldByRemote(M800Call call) {}
    @Override public void callStartPlayingFilePlayback(M800Call call) {}
    @Override public void callRestartPlayingFilePlayback(M800Call call) {}
    @Override public void callFailedToPlayFilePlayback(M800Call call) {}
    @Override public void callDial(M800Call call) {}
    @Override public void callBeginTalking(M800Call call) {}
    @Override public void callProgress(M800Call call, int code, @Nonnull Map<String, String> userInfo) {}
    @Override public void callAnswering(M800Call call) {}
    @Override public void callEstablishing(M800Call call) {}
    @Override public void callWillStartMedia(M800Call call) {}
    @Override public void callWillDestroy(M800Call call) {}
    @Override public void networkQualityReport(M800Call call, long qualityLevel) {}
    @Override public void callNewMediaOffer(M800Call call, EMsmeMediaType media, boolean isRemoved) {}
    @Override public void callEvLocalSurfaceViewCreated(M800Call call, SurfaceView View){}
    @Override public void callEvRemoteSurfaceViewCreated(M800Call call, SurfaceView View) {}
    @Override public void callMediaRouteChanged(M800Call call, EMsmeMediaType media) {}
    @Override public void callReconnecting(M800Call call, int attempts, boolean isPeerReconnecting) {}
    @Override public void callFailedToReconnect(M800Call call, int attempts)  {}

}
