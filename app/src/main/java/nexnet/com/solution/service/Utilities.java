package nexnet.com.solution.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import nexnet.com.solution.R;
import com.m800.sdk.IM800Utils;
import com.m800.sdk.M800SDK;
import com.m800.sdk.M800SDKConfiguration;


public class Utilities {

    public static M800SDKConfiguration fillInConfigurationWithPreference(M800SDKConfiguration configuration, Context context) {
        if (null == configuration) {
            configuration = M800SDK.newConfiguration();
        }
        SharedPreferences spm = PreferenceManager.getDefaultSharedPreferences(context);
        configuration.setAppliationContext(context.getApplicationContext());

        String key = context.getResources().getString(R.string.M800DefaultApplicationIdentifier);
        String defaultValue = context.getResources().getString(R.string.M800DefaultApplicationIdentifier);
        configuration.setApplicationIdentifier(spm.getString(key, defaultValue));

        key = context.getResources().getString(R.string.M800DefaultApplicationKey);
        defaultValue = context.getResources().getString(R.string.M800DefaultApplicationKey);
        configuration.setApplicationKey(spm.getString(key, defaultValue));

        key = context.getResources().getString(R.string.M800DefaultApplicationVersion);
        defaultValue = context.getResources().getString(R.string.M800DefaultApplicationVersion);
        configuration.setApplicationVersion(spm.getString(key, defaultValue));

        key = context.getResources().getString(R.string.M800DefaultDeveloperKey);
        defaultValue = context.getResources().getString(R.string.M800DefaultDeveloperKey);
        configuration.setDeveloperKey(spm.getString(key, defaultValue));

        key = context.getResources().getString(R.string.M800DefaultCarrier);
        defaultValue = context.getResources().getString(R.string.M800DefaultCarrier);
        configuration.setCarrier(spm.getString(key, defaultValue));

        key = context.getResources().getString(R.string.M800DefaultCapabilities);
        defaultValue = context.getResources().getString(R.string.M800DefaultCapabilities);
        configuration.setCapabilities(spm.getString(key, defaultValue));

        key = context.getResources().getString(R.string.M800DefaultExperation);
        defaultValue = context.getResources().getString(R.string.M800DefaultExperation);
        configuration.setExpiration(spm.getString(key, defaultValue));

        key = context.getResources().getString(R.string.M800DefaultApplicationSecret);
        defaultValue = context.getResources().getString(R.string.M800DefaultApplicationSecret);
        String applicationSecret = spm.getString(key, defaultValue);

        configuration.setCapabilitiesSignature(
                getCapabilitiesSignature(
                        applicationSecret,
                        configuration.getCapabilities(),
                        configuration.getExpiration()));
        return configuration;
    }

    public static String findValue(Context context, int keyId) {
        SharedPreferences spm = PreferenceManager.getDefaultSharedPreferences(context);
        int defaultValueId = -1;
        switch (keyId) {
            case R.string.M800DefaultApplicationIdentifier:
                defaultValueId = R.string.M800DefaultApplicationIdentifier;
                break;
            case R.string.M800DefaultApplicationKey:
                defaultValueId = R.string.M800DefaultApplicationKey;
                break;
            case R.string.M800DefaultApplicationVersion:
                defaultValueId = R.string.M800DefaultApplicationVersion;
                break;
            case R.string.M800DefaultDeveloperKey:
                defaultValueId = R.string.M800DefaultDeveloperKey;
                break;
            case R.string.M800DefaultCarrier:
                defaultValueId = R.string.M800DefaultCarrier;
                break;
            case R.string.M800DefaultCapabilities:
                defaultValueId = R.string.M800DefaultCapabilities;
                break;
            case R.string.M800DefaultExperation:
                defaultValueId = R.string.M800DefaultExperation;
                break;
            case R.string.M800DefaultApplicationSecret:
                defaultValueId = R.string.M800DefaultApplicationSecret;
                break;
            case R.string.M800DefaultDeveloperSecret:
                defaultValueId = R.string.M800DefaultDeveloperSecret;
                break;
        }
        String key = context.getResources().getString(keyId);
        String defaultValue = context.getResources().getString(defaultValueId);
        return spm.getString(key, defaultValue);
    }

    static String getCapabilitiesSignature(String applicationSecret, String capabilities, String expires) {
        String capSig = "";

        try {
            capSig = IM800Utils.Security.createSignature(capabilities, expires,
                    applicationSecret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return capSig;
    }

}
