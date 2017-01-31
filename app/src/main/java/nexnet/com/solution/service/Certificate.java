package nexnet.com.solution.service;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.m800.sdk.IM800Management;
import com.m800.sdk.M800Error;
import com.m800.sdk.M800SDK;
import com.m800.sdk.M800SDKConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import nexnet.com.solution.database.AppDB;

/**
 * Created by Ching on 1/4/2017.
 */

public class Certificate extends MultiDexApplication implements IM800Management.M800ManagementConnectionListener {
    private static final String DEBUG_TAG=Certificate.class.getSimpleName();
    private static final String PREF_M800_CERT="com.demo.m800.cert";
    private static final String PREF_M800_CALL_HOLD_TONE = "com.demo.m800.call.hold_tone";
    private static final String PREF_M800_CALL_RING_BACK_TONE = "com.demo.m800.call.ring_back_tone";
    public static final String ACTION_M800_CONNECTION_CHANGED = "connection_changed";
    private static Certificate _M800ClientPhoneApp;
    private File mHoldToneFile;
    private File mRingBackToneFile;
    private boolean isConnectedToM800;
    private volatile M800SDK mM800SDK;

    public static Certificate getInstance(){
        return _M800ClientPhoneApp;
    }
    @Override
    public void onCreate(){
        Log.d(DEBUG_TAG, "Run Certificate");
        super.onCreate();
        _M800ClientPhoneApp=this;

        AppDB.initiate(this);

        mHoldToneFile = copyHoldToneFromAsset();
        mRingBackToneFile = copyRingBackToneFromAsset();

        M800SDKConfiguration configuration = M800SDK.newConfiguration();
        Utilities.fillInConfigurationWithPreference(configuration, this);
        configuration.setCertificateFileForCall(copyCertificateFromAsset());
        configuration.setCertificateFileForIM(copyCertificateFromAsset());

        M800SDK.setConfiguration(configuration);

        // Initialize M800SDK
        M800SDK.initialize();
        mM800SDK = M800SDK.getInstance();

        // Start M800 connection
        if (mM800SDK.hasUserSignedUp()) {
            Log.d(DEBUG_TAG, "Is it initialized: "+ mM800SDK.getUserJID());
            mM800SDK.getManagement().connect();
        }
        // Add M800 listener
        mM800SDK.getManagement().addConnectionListener(this);

        mM800SDK.getManagement().addNotificationListener(new IM800Management.M800NotificationListener() {
            @Override
            public void onMessage(Context context, Bundle notification) {
                Log.d(DEBUG_TAG, "Received system notification: " + notification.toString());
            }
        });
    }



    private File copyCertificateFromAsset() {
        String assetFileName = "cacert.crt";
        File certFile = copyFileFromAsset(assetFileName);
        if (certFile != null) {
            Log.d(DEBUG_TAG, "M800 cert file: " + certFile.getAbsolutePath());
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(PREF_M800_CERT, certFile.getAbsolutePath());
            editor.apply();
        }
        return certFile;
    }

    private File copyHoldToneFromAsset() {
        String assetFileName = "hold_tone.raw";
        File holdToneFile = copyFileFromAsset(assetFileName);
        if (holdToneFile != null) {
            Log.d(DEBUG_TAG, "M800 call hold tone file: " + holdToneFile.getAbsolutePath());
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(PREF_M800_CALL_HOLD_TONE, holdToneFile.getAbsolutePath());
            editor.apply();
        }
        return holdToneFile;
    }

    private File copyRingBackToneFromAsset() {
        String assetFileName = "bell_ringback.raw";
        File ringBackToneFile = copyFileFromAsset(assetFileName);
        if (ringBackToneFile != null) {
            Log.d(DEBUG_TAG, "M800 call ring back tone file: " + ringBackToneFile.getAbsolutePath());
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(PREF_M800_CALL_RING_BACK_TONE, ringBackToneFile.getAbsolutePath());
            editor.apply();
        }
        return ringBackToneFile;
    }

    private File copyFileFromAsset(String filename) {
        AssetManager am = getAssets();
        InputStream in;
        OutputStream out;
        try {
            in = am.open(filename);
            String cacheDir = getFilesDir().getAbsolutePath();
            File outFile = new File(cacheDir, filename);
            out = new FileOutputStream(outFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            in.close();
            out.flush();
            out.close();

            return outFile;
        } catch (IOException e) {
            Log.e(DEBUG_TAG, "Failed to copy file from asset!", e);
        }
        return null;
    }

    public File getHoldToneFile() {
        return mHoldToneFile;
    }

    public File getRingBackToneFile() {
        return mRingBackToneFile;
    }

    public boolean isConnectedToM800() {
        return isConnectedToM800;
    }

    @Override
    public void onConnectedToM800() {
        Log.d(DEBUG_TAG, "Connected to M800");
        isConnectedToM800=true;
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_M800_CONNECTION_CHANGED));
    }

    @Override
    public void onDisconnectedFromM800(M800Error m800Error) {
        Log.d(DEBUG_TAG, "Not Connected to M800 Error:" + m800Error.getMessage());
        isConnectedToM800=false;
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_M800_CONNECTION_CHANGED));
    }
}
