package nexnet.com.solution.signup;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.m800.phoneverification.api.M800CountryCode;
import com.m800.phoneverification.api.M800FlowMode;
import com.m800.phoneverification.api.M800VerificationError;
import com.m800.phoneverification.api.M800VerificationManager;
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
import nexnet.com.solution.service.Utilities;

public class SignUpActivity extends AppCompatActivity implements VerificationManager.VerificationProgressCallback {
    private static final String DEBUG_TAG = SignUpActivity.class.getSimpleName();
    private static final String PREF_M800_CERT = "com.demo.m800.cert";
    private static String myrequestID = "";
    private static Context context;
    private static Application application;
    private TextView textViewSMSCode;
    private VerificationManager mVerificationManager;
    private M800CountryCode mCountryCode;

    private IM800Management mM800Management;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        M800SDKConfiguration configuration = M800SDK.newConfiguration();
        Utilities.fillInConfigurationWithPreference(configuration, this);
        configuration.setCertificateFileForCall(copyCertificateFromAsset());
        configuration.setCertificateFileForIM(copyCertificateFromAsset());

        M800SDK.setConfiguration(configuration);
        setContentView(R.layout.activity_main);

        SignUpActivity.context = getApplicationContext();
      //  textViewSMSCode = (TextView) findViewById(R.id.textViewHello);
        mM800Management = M800SDK.getInstance().getManagement();
        M800VerificationManager verificationManager = M800VerificationManager.getInstance();
        String applicationKey = Utilities.findValue(context, R.string.M800DefaultApplicationKey);
        String developerKey = Utilities.findValue(context, R.string.M800DefaultDeveloperKey);
        String appSecret = Utilities.findValue(context, R.string.M800DefaultApplicationSecret);
        String devSecret = Utilities.findValue(context, R.string.M800DefaultDeveloperSecret);

        // Get verification service hosts
        String[] verificationHosts = context.getResources().getStringArray(R.array.M800DefaultVerificationHosts);

        // Set to your preferred language
        String language = IM800Management.M800Language.M800LanguageEnglish.getCode();
        verificationManager.init(applicationKey, developerKey, appSecret, devSecret,verificationHosts, language, null);
        if(verificationManager.isInitialized()){
           // textViewSMSCode.setText("Verification Manager initialized");
            mVerificationManager = VerificationManager.getInstance();
            mInitVerificationManagerTask.execute();
            mCountryCode = verificationManager.getDefaultCountryCode(SignUpActivity.this);
        }else{
            //textViewSMSCode.setText("Verification Manager not initialized");
        }

        M800VerificationError result = mVerificationManager.startSMSVerification(getApplication(), mCountryCode, "9226375944", SignUpActivity.this);
        //Log.d(DEBUG_TAG, "RESULT = " + result.toString());

    }

    private AsyncTask<Void, Void, Void> mInitVerificationManagerTask = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... voids) {
            if (!mVerificationManager.isInitialized()) {
                mVerificationManager.initializeVerificationManager(SignUpActivity.this);
            }
            return null;
        }
    };

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
    public void onWaitingToReceiveCode(M800FlowMode mode, String requestId, int codeLength) {
        myrequestID=requestId;
    }

    @Override
    public void onSMSReceived(String code) {
        Log.d(DEBUG_TAG,"SMS Code: " + code);
        Log.d(DEBUG_TAG,"SMS Code: " + code + " RequestID" + myrequestID);
        mVerificationManager.verifyWithCode(myrequestID, code);
    }

    @Override
    public void onCodeVerificationFailed(String requestId, int codeLength) {

    }

    @Override
    public void onVerificationFinished(VerificationManager.VerificationRecord record) {
        signUpWhiteLabelWithVerificationRequestId(record.getRequestId());
    }

    private void signUpWhiteLabelWithVerificationRequestId(String requestId){
        mM800Management.signup(
                "Franz Oriola",
                "9226375944",
                mCountryCode.getCountyCode(),
                requestId,
                IM800Management.M800Language.M800LanguageEnglish,
                new IM800Management.M800ManagementCallback() {
                    @Override
                    public void complete(boolean isSuccess, M800Error error, Bundle userInfo) {
                        Log.d(DEBUG_TAG, "M800SDK sign up complete, isSuccess = " + isSuccess);
                        Log.d(DEBUG_TAG, "Error: "+ error.getMessage());
                        if (isSuccess) {
                           // textViewSMSCode.setText("Success!");
                        } else {
                           // textViewSMSCode.setText("Failed!");
                        }
                    }
                });
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:

                Log.d(DEBUG_TAG, "grantResults: "+ grantResults.length);
                boolean temp_bool = true;
                for (int i=5; i<grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        temp_bool = false;
                        i = grantResults.length;
                    }
                }

                if (temp_bool) {
                    //granted

                } else {
                    //not granted

                }

                break;
            default: {

            }
            break;
        }
    }
}