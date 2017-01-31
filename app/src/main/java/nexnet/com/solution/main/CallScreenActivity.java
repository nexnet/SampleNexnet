package nexnet.com.solution.main;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.m800.msme.api.M800Audio;
import com.m800.msme.api.M800AudioRoutes;
import com.m800.msme.api.M800Call;
import com.m800.msme.api.M800CallDelegate;
import com.m800.msme.api.M800ClientConfiguration;
import com.m800.msme.jni.EMsmeMediaType;
import com.m800.sdk.M800SDK;

import java.io.File;
import java.util.Map;

import javax.annotation.Nonnull;

import nexnet.com.solution.R;
import nexnet.com.solution.service.CallLogManager;
import nexnet.com.solution.service.Certificate;

import static android.content.ContentValues.TAG;

public class CallScreenActivity extends Activity implements View.OnClickListener, M800CallDelegate {
    private final static String DEBUG_TAG=CallScreenActivity.class.getSimpleName();
    private M800Call mCall;
    public final static String EXTRA_KEY_CALL_ID = "callID";
    private TextView textViewPhoneNumber;
    private M800Audio mAudio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_screen);
        textViewPhoneNumber=(TextView) findViewById(R.id.textViewPhoneNumber);
        findViewById(R.id.buttonEndCall).setOnClickListener(this);
        findViewById(R.id.buttonSpeaker).setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String phonenumber = extras.getString("phonenumber");
            textViewPhoneNumber.setText(phonenumber);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        String callID = getIntent().getExtras().getString(EXTRA_KEY_CALL_ID);
        mCall = M800SDK.getInstance().getRealtimeClient().getCall(callID);
        mAudio = M800SDK.getInstance().getRealtimeClient().getAudioManager();



        if (mCall == null) {
            Toast.makeText(this, "Call Not Exists for callID, failed to create call. " + callID, Toast.LENGTH_LONG).show();
            Log.e(DEBUG_TAG, "Call Not Exists for callID " + callID);
            finish();
            return;
        }
        // Add self to the realtime delegate
        mCall.addCallDelegate(this);
        // Reset voice status
        mAudio.unmute();
        mAudio.setSpeaker(false);

        M800AudioRoutes route = mAudio.getRoute();

    }

    @Override
    public void callHoldByLocal(M800Call m800Call) {

    }

    @Override
    public void callUnholdByLocal(M800Call m800Call) {

    }

    @Override
    public void callHoldByRemote(M800Call m800Call) {

    }

    @Override
    public void callUnHoldByRemote(M800Call m800Call) {

    }

    @Override
    public void callStartPlayingFilePlayback(M800Call m800Call) {

    }

    @Override
    public void callRestartPlayingFilePlayback(M800Call m800Call) {

    }

    @Override
    public void callFailedToPlayFilePlayback(M800Call m800Call) {

    }

    @Override
    public void callDial(M800Call m800Call) {

    }

    @Override
    public void callBeginTalking(M800Call m800Call) {

    }

    @Override
    public void callTerminated(M800Call m800Call, int i, Map<String, String> map) {
        CallLogManager.getInstance().saveCallLog(m800Call);
    }

    @Override
    public void callProgress(M800Call m800Call, int i, @Nonnull Map<String, String> map) {

    }

    @Override
    public void callAnswering(M800Call m800Call) {

    }

    @Override
    public void callEstablishing(M800Call m800Call) {

    }

    @Override
    public void callWillStartMedia(M800Call m800Call) {

    }

    @Override
    public void callWillDestroy(M800Call m800Call) {

    }

    @Override
    public void networkQualityReport(M800Call m800Call, long l) {

    }

    @Override
    public void callNewMediaOffer(M800Call m800Call, EMsmeMediaType eMsmeMediaType, boolean b) {

    }

    @Override
    public void callEvLocalSurfaceViewCreated(M800Call m800Call, SurfaceView surfaceView) {

    }

    @Override
    public void callEvRemoteSurfaceViewCreated(M800Call m800Call, SurfaceView surfaceView) {

    }

    @Override
    public void callMediaRouteChanged(M800Call m800Call, EMsmeMediaType eMsmeMediaType) {

    }

    @Override
    public void callReconnecting(M800Call m800Call, int i, boolean b) {

    }

    @Override
    public void callFailedToReconnect(M800Call m800Call, int i) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonEndCall: // End the call
                mCall.hangup();
                finish();
                break;

            case R.id.buttonSpeaker: // End the call
                M800AudioRoutes route = mAudio.getRoute();
                if (route != M800AudioRoutes.SPEAKER) {
                    mAudio.setSpeaker(true);
                } else {
                    mAudio.setSpeaker(false);
                }
                break;
        }
    }
}
