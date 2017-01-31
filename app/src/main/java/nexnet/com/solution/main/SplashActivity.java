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
import nexnet.com.solution.service.AppM800Class;
import nexnet.com.solution.service.Utilities;
import nexnet.com.solution.signup.SignUpActivity;

import static android.R.attr.name;
import static android.R.attr.permission;


public class SplashActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = SplashActivity.class.getSimpleName();
    private static final String PREF_M800_CERT = "com.demo.m800.cert";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT >= 23) {
            requestForSpecificPermission();
        }
        if (M800SDK.getInstance().hasUserSignedUp()) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            // If user hasn't sign up yet, go to SignUpActivity
            Intent intent = new Intent(SplashActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
        startService(new Intent(this, AppM800Class.class));
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
}
