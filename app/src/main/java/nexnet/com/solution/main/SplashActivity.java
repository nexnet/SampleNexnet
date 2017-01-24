package nexnet.com.solution.main;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.m800.sdk.M800SDK;
import com.m800.sdk.M800SDKConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nexnet.com.solution.R;
import nexnet.com.solution.service.Utilities;
import nexnet.com.solution.signup.SignUpActivity;

import static android.R.attr.name;
import static android.R.attr.permission;


public class SplashActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = SplashActivity.class.getSimpleName();
    private static final String PREF_M800_CERT = "com.demo.m800.cert";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= 23) {
            requestForSpecificPermission();
        }
        // Set level of SDK log.
       // M800SDK.setLogLevel(0x0702);
        M800SDKConfiguration configuration = M800SDK.newConfiguration();
        Utilities.fillInConfigurationWithPreference(configuration, this);
        configuration.setCertificateFileForCall(copyCertificateFromAsset());
        configuration.setCertificateFileForIM(copyCertificateFromAsset());

        M800SDK.setConfiguration(configuration);
        M800SDK.initialize();
        if (M800SDK.isInitialized()) {
            // M800SDK is initialized
            Log.d(DEBUG_TAG, "M800SDK is initialized" + M800SDK.getInstance().getUsername());

            if (M800SDK.getInstance().hasUserSignedUp()) {
                // If user has signed up, go to MainActivity
                M800SDK.getInstance().getManagement().connect();
                M800SDK.getInstance().getManagement().goOnline();

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                // If user hasn't sign up yet, go to SignUpActivity
                Intent intent = new Intent(SplashActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        } else {
            Log.d(DEBUG_TAG, "M800SDK is not Yet initialized" + M800SDK.getConfiguration());
        }


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    protected void onPause() {
        super.onPause();
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.WRITE_SETTINGS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.VIBRATE,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
        }, 101);
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Splash Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
