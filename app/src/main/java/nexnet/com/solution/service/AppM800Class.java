package nexnet.com.solution.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.m800.msme.api.M800Client;
import com.m800.msme.api.M800ClientConfiguration;
import com.m800.msme.api.M800ClientDelegate;
import com.m800.msme.api.M800IncomingCall;
import com.m800.msme.jni.StringMap;
import com.m800.sdk.M800SDK;

import java.io.File;

/**
 * Created by Ching on 1/25/2017.
 */

public class AppM800Class extends Service implements M800ClientDelegate {
    private static final String DEBUG_TAG=AppM800Class.class.getSimpleName();
    private AppM800Binder mBinder;
    private M800SDK mM800SDK;
    public void onCreate() {
        Log.d(DEBUG_TAG, "onCreate");
        super.onCreate();
        mBinder = new AppM800Binder();
        mM800SDK = M800SDK.getInstance();
        // Add M800 Realtime client delegate
        M800Client client = mM800SDK.getRealtimeClient();
        if (client != null) {
            client.addClientDelegate(this);
            File file = Certificate.getInstance().getRingBackToneFile();
            M800ClientConfiguration config = client.getCurrentConfiguration();
            String filepath;
            if (file != null) {
                filepath = file.getAbsolutePath();
                Log.d(DEBUG_TAG, "The path of ringback tone:" + filepath);
                config.setSupportPlayRingbackToneInEngine(true);
                config.setRingbackTone(filepath);
            }
        }

    }

    public void onDestroy() {
        Log.d(DEBUG_TAG, "onDestroy");
        // Remove delegate and receivers
        M800Client client = mM800SDK.getRealtimeClient();
        if (client != null) {
            client.removeClientDelegate(this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class AppM800Binder extends Binder {
        public AppM800Class getService() {
            return AppM800Class.this;
        }
    }

    @Override
    public void onClientInitialized(M800Client m800Client, Bundle bundle) {
        try {
            M800ClientConfiguration config = m800Client.getCurrentConfiguration();
            if (config == null) {
                return;
            }

            String filepath;
            File file = Certificate.getInstance().getHoldToneFile();
            if (file != null) {
                filepath = file.getAbsolutePath();
                Log.d(DEBUG_TAG, "The path of hold tone:" + filepath);
                config.setHoldTone(filepath);
            }

            file =Certificate.getInstance().getRingBackToneFile();
            if (file != null) {
                filepath = file.getAbsolutePath();
                Log.d(DEBUG_TAG, "The path of ringback tone:" + filepath);
                config.setSupportPlayRingbackToneInEngine(true);
                config.setRingbackTone(filepath);
            }

            m800Client.start(config);
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Error in onClientInitialized", e);
        }
    }

    @Override
    public void onClientReady(M800Client m800Client, Bundle bundle) {

    }

    @Override
    public void onClientNotReady(M800Client m800Client, int i, Bundle bundle) {

    }

    @Override
    public void onClientRegistered(M800Client m800Client, Bundle bundle) {

    }

    @Override
    public void onIncomingCall(M800Client m800Client, M800IncomingCall m800IncomingCall, StringMap stringMap) {

    }

    @Override
    public void onOodResponse(M800Client m800Client, long l, long l1, short i, boolean b) {

    }
}
