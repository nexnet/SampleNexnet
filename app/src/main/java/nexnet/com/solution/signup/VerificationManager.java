package nexnet.com.solution.signup;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.m800.phoneverification.api.M800CountryCode;
import com.m800.phoneverification.api.M800FlowMode;
import com.m800.phoneverification.api.M800VerificationError;
import com.m800.phoneverification.api.M800VerificationManager;
import com.m800.phoneverification.api.M800VerificationProgressCallback;
import com.m800.phoneverification.api.M800VerificationRecord;
import com.m800.sdk.IM800Management;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.List;

import nexnet.com.solution.R;
import nexnet.com.solution.service.Utilities;

/**
 * Created by Ching on 1/2/2017.
 */

class VerificationManager {

    private static final String DEBUG_TAG = VerificationManager.class.getSimpleName();

    private static final String PREF_LAST_VERIFICATION_RECORD = "com.m800.demo.last_verification_record";

    private static VerificationManager sInstance;

    private M800VerificationManager verificationManager;
    private boolean isInitialized;
    private SharedPreferences sharedPreferences;
    private VerificationRecord activeVerificationRecord;
    // Just to keep the reference
    private VerificationCallbackWrapper activeCallbackWrapper;
    private VerificationRecord lastVerificationRecord;

    public static VerificationManager getInstance() {
        if (sInstance == null) {
            sInstance = new VerificationManager();
        }
        return sInstance;
    }

    synchronized boolean isInitialized() {
        return isInitialized;
    }

    synchronized void initializeVerificationManager(Context context) {
        if (context == null || isInitialized) {
            return;
        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        verificationManager = M800VerificationManager.getInstance(context);
        String applicationKey = Utilities.findValue(context, R.string.M800DefaultApplicationKey);
        String developerKey = Utilities.findValue(context, R.string.M800DefaultDeveloperKey);
        String appSecret = Utilities.findValue(context, R.string.M800DefaultApplicationSecret);
        String devSecret = Utilities.findValue(context, R.string.M800DefaultDeveloperSecret);

        // Get verification service hosts
        String[] verificationHosts = context.getResources()
                .getStringArray(R.array.M800DefaultVerificationHosts);

        // Set to your preferred language
        String language = IM800Management.M800Language.M800LanguageEnglish.getCode();

        verificationManager.init(applicationKey, developerKey, appSecret,
                devSecret, verificationHosts, language, null);
        verificationManager.setMOCallTimeout(20 * 1000);
        verificationManager.setMTCallTimeout(30 * 1000);
        verificationManager.setVerificationTimeout(60 * 1000);
        restoreLastVerificationRecord();
        isInitialized = true;
    }

    synchronized VerificationRecord getActiveVerificationRecord() {
        return activeVerificationRecord;
    }

    synchronized VerificationRecord getLastVerificationRecord() {
        return lastVerificationRecord;
    }

    synchronized void abortCurrentVerificationProcess() {
        if (verificationManager != null) {
            verificationManager.abortRequest();
        }
        activeVerificationRecord = null;
        activeCallbackWrapper = null;
    }

    M800CountryCode getDefaultCountryCode(Context context) {
        if (verificationManager == null) {
            return null;
        }
        return verificationManager.getDefaultCountryCode(context);
    }

    String getDefaultPhoneNumber(Context context) {
        if (verificationManager == null) {
            return null;
        }
        return verificationManager.getDefaultPhoneNumber(context);
    }

    List<M800CountryCode> getSupportedCountries(Context context) {
        if (verificationManager == null) {
            return null;
        }
        return verificationManager.getSupportedCountries(context);
    }

    /**
     * The method to start a verification
     * If permissions "android.permission.WRITE_CALL_LOG" and "android.permission.WRITE_CALL_LOG"
     * are provided in "AndroidManifest.xml", call logs of the verification would be cleared by default.
     *
     * @param application     {@link Application} of the client app.
     * @param m800CountryCode {@link M800CountryCode}
     */
    M800VerificationError startMTVerification(Application application,
                                              M800CountryCode m800CountryCode,
                                              String phoneNumber,
                                              VerificationProgressCallback callback) {
        Log.d(DEBUG_TAG, "<startMTVerification> phoneNumber = "
                + m800CountryCode.getCountyCode() + " " + phoneNumber);
        if (verificationManager == null) {
            return M800VerificationError.NotInitialized;
        }
        VerificationCallbackWrapper callbackWrapper = new VerificationCallbackWrapper(callback);
        M800VerificationError result = verificationManager.startMTVerification(application,
                m800CountryCode.getCountyCode(), String.valueOf(m800CountryCode.getCallCode()),
                phoneNumber, callbackWrapper);
        if (result == null) {
            // No error, update current verification record
            activeVerificationRecord = new VerificationRecord(m800CountryCode, phoneNumber,
                    M800FlowMode.MobileTerminated);
            activeCallbackWrapper = callbackWrapper;
        }
        return result;
    }

    /**
     * The method to start a verification
     *
     * @param application     {@link Application} of the client app.
     * @param m800CountryCode {@link M800CountryCode}
     */
    M800VerificationError startSMSVerification(Application application,
                                               M800CountryCode m800CountryCode,
                                               String phoneNumber,
                                               VerificationProgressCallback callback) {
        Log.d(DEBUG_TAG, "<startSMSVerification> phoneNumber = "
                + m800CountryCode.getCountyCode() + " " + phoneNumber);
        if (verificationManager == null) {
            return M800VerificationError.NotInitialized;
        }
        VerificationCallbackWrapper callbackWrapper = new VerificationCallbackWrapper(callback);
        M800VerificationError result = verificationManager.startSMSVerification(application,
                m800CountryCode.getCountyCode(), String.valueOf(m800CountryCode.getCallCode()),
                phoneNumber, callbackWrapper);
        if (result == null) {
            // No error, update current verification record
            activeVerificationRecord = new VerificationRecord(m800CountryCode, phoneNumber, M800FlowMode.SMS);
            activeCallbackWrapper = callbackWrapper;
        }
        return result;
    }

    /**
     * The method to start a verification
     *
     * @param application     {@link Application} of the client app.
     * @param m800CountryCode {@link M800CountryCode}
     */
    M800VerificationError startIVRVerification(Application application,
                                               M800CountryCode m800CountryCode,
                                               String phoneNumber,
                                               VerificationProgressCallback callback) {
        Log.d(DEBUG_TAG, "<startIVRVerification> phoneNumber = "
                + m800CountryCode.getCountyCode() + " " + phoneNumber);
        if (verificationManager == null) {
            return M800VerificationError.NotInitialized;
        }
        VerificationCallbackWrapper callbackWrapper = new VerificationCallbackWrapper(callback);
        M800VerificationError result = verificationManager.startIVRVerification(application,
                m800CountryCode.getCountyCode(), String.valueOf(m800CountryCode.getCallCode()),
                phoneNumber, callbackWrapper);
        if (result == null) {
            // No error, update current verification record
            activeVerificationRecord = new VerificationRecord(m800CountryCode, phoneNumber, M800FlowMode.IVR);
            activeCallbackWrapper = callbackWrapper;
        }
        return result;
    }

    /**
     *
     * @param requestId the requestId is provided by the callback {@link M800VerificationProgressCallback#startWaitingVerificationCode(M800FlowMode, String, int)}
     *                  during a {@link M800FlowMode#SMS} or {@link M800FlowMode#IVR} verification.
     * @param code the code received from SMS or IVR.
     * @return {@code null} if verification is proceeding with the provided code, else, a {@link M800VerificationError} object is returned
     */
    M800VerificationError verifyWithCode(String requestId, String code) {
        Log.d(DEBUG_TAG, "<verifyWithCode> requestId = " + requestId + " code = " + code);
        if (verificationManager == null) {
            return M800VerificationError.NotInitialized;
        }
        return verificationManager.verifyWithCode(requestId, code);
    }

    private void saveLastVerificationRecord() {
        if (lastVerificationRecord != null) {
            Gson gson = new Gson();
            String json = gson.toJson(lastVerificationRecord);
            sharedPreferences.edit().putString(PREF_LAST_VERIFICATION_RECORD, json).apply();
        } else {
            sharedPreferences.edit().putString(PREF_LAST_VERIFICATION_RECORD, null).apply();
        }
    }

    private void restoreLastVerificationRecord() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(PREF_LAST_VERIFICATION_RECORD, null);
        if (json != null) {
            lastVerificationRecord = gson.fromJson(json, VerificationRecord.class);
        }
    }

    private class VerificationCallbackWrapper extends M800VerificationProgressCallback {

        private WeakReference<VerificationProgressCallback> callbackRef;

        private VerificationCallbackWrapper(VerificationProgressCallback callback) {
            if (callback != null) {
                callbackRef = new WeakReference<>(callback);
            }
        }

        @Override
        public void onVerificationFinished(M800VerificationRecord m800VerificationRecord) {
            Log.d(DEBUG_TAG, "<onVerificationFinished> requestId = " + m800VerificationRecord.getRequestId()
                    + " isSuccess = " + m800VerificationRecord.isSuccess());
            activeVerificationRecord.setVerificationResult(m800VerificationRecord);
            lastVerificationRecord = activeVerificationRecord;
            activeVerificationRecord = null;
            saveLastVerificationRecord();

            VerificationProgressCallback callback = callbackRef == null ? null : callbackRef.get();
            if (callback != null) {
                callback.onVerificationFinished(lastVerificationRecord);
            }
            activeCallbackWrapper = null;
        }

        @Override
        public Context getContextToMakeMOCall() {
            // MO is disabled for now
            return null;
        }

        @Override
        public void onSMSReceived(String code) {
            Log.d(DEBUG_TAG, "<onSMSReceived> code = " + code);
            VerificationProgressCallback callback = callbackRef == null ? null : callbackRef.get();
            if (callback != null) {
                callback.onSMSReceived(code);
            }
        }

        @Override
        public void startWaitingVerificationCode(M800FlowMode m800FlowMode, String requestId, int codeLength) {
            Log.d(DEBUG_TAG, "<startWaitingVerificationCode> requestId = " + requestId);
            VerificationProgressCallback callback = callbackRef == null ? null : callbackRef.get();
            if (callback != null) {
                callback.onWaitingToReceiveCode(m800FlowMode, requestId, codeLength);
            }
        }

        @Override
        public void onCodeVerificationFailed(String requestId, int codeLength) {
            Log.d(DEBUG_TAG, "<onCodeVerificationFailed> requestId = " + requestId);
            VerificationProgressCallback callback = callbackRef == null ? null : callbackRef.get();
            if (callback != null) {
                callback.onCodeVerificationFailed(requestId, codeLength);
            }
        }
    }

    interface VerificationProgressCallback {

        void onWaitingToReceiveCode(M800FlowMode mode, String requestId, int codeLength);

        void onSMSReceived(String code);

        void onCodeVerificationFailed(String requestId, int codeLength);

        void onVerificationFinished(VerificationRecord record);
    }

    static class VerificationRecord implements Serializable {
        private final M800CountryCode countryCode;
        private final String phoneNumber;
        private final M800FlowMode m800FlowMode;

        private long startTime;
        private long endTime;
        private boolean isSuccess;
        private String failureReason;
        private M800VerificationError error;
        private String requestId;

        private VerificationRecord(M800CountryCode countryCode, String phoneNumber, M800FlowMode m800FlowMode) {
            this.countryCode = countryCode;
            this.phoneNumber = phoneNumber;
            this.m800FlowMode = m800FlowMode;
        }

        private void setVerificationResult(M800VerificationRecord result) {
            startTime = result.getStartTime();
            endTime = result.getEndTime();
            isSuccess = result.isSuccess();
            failureReason = result.getFailureReason();
            error = result.getError();
            requestId = result.getRequestId();
        }

        M800CountryCode getCountryCode() {
            return countryCode;
        }

        String getPhoneNumber() {
            return phoneNumber;
        }

        public M800FlowMode getM800FlowMode() {
            return m800FlowMode;
        }

        public long getStartTime() {
            return startTime;
        }

        long getEndTime() {
            return endTime;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        String getFailureReason() {
            return failureReason;
        }

        public M800VerificationError getError() {
            return error;
        }

        String getRequestId() {
            return requestId;
        }
    }


}

