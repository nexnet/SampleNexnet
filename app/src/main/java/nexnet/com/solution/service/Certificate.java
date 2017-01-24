package nexnet.com.solution.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
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

import nexnet.com.solution.R;
import nexnet.com.solution.signup.SignUpActivity;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Ching on 1/4/2017.
 */

public class Certificate extends MultiDexApplication implements IM800Management.M800ManagementConnectionListener{
    private static final String DEBUG_TAG=Certificate.class.getSimpleName();
    private static final String PREF_M800_CERT="com.demo.m800.cert";
    public static final String ACTION_M800_SDK_INITIALIZED = "com.demo.m800.action.sdk_initialized";
    private static Certificate _M800ClientPhoneApp;
    private File mCertFile;
    private boolean isConnectedToM800;
    private volatile M800SDK mM800SDK;
    private static Context context;

    public static Certificate getInstance(){
        assert _M800ClientPhoneApp != null;
        return _M800ClientPhoneApp;
    }
    @Override
    public void onCreate(){
        Log.d(DEBUG_TAG, "Run Certificate");
        super.onCreate();
        _M800ClientPhoneApp=this;
        M800SDKConfiguration configuration = M800SDK.newConfiguration();
        Utilities.fillInConfigurationWithPreference(configuration, this);
        configuration.setCertificateFileForCall(copyCertificateFromAsset());
        configuration.setCertificateFileForIM(copyCertificateFromAsset());

        M800SDK.setConfiguration(configuration);
        // Broadcast sdk initialized event
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_M800_SDK_INITIALIZED));
        // Broadcast sdk initialized event

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

    @Override
    public void onConnectedToM800() {
        Log.d(DEBUG_TAG, "Connected to M800");
        isConnectedToM800=true;
    }

    @Override
    public void onDisconnectedFromM800(M800Error m800Error) {
        Log.d(DEBUG_TAG, "Not Connected to M800 Error:" + m800Error.getMessage());
        isConnectedToM800=false;
    }
}
