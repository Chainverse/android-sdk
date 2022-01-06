package com.chainverse.sdk.common;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class EncryptPreferenceUtils {
    public static final String NAME = "chainverse_secret_shared_prefs";
    public static final String KEY_1 = "CHAINVERSE_SDK_KEY_1";
    public static final String KEY_2 = "CHAINVERSE_SDK_KEY_2";
    public static final String KEY_3 = "CHAINVERSE_SDK_KEY_3";
    public static final String KEY_4 = "CHAINVERSE_SDK_KEY_4";
    public static final String KEY_5 = "CHAINVERSE_SDK_KEY_5";
    public static final String KEY_6 = "CHAINVERSE_SDK_KEY_6";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private static EncryptPreferenceUtils instance;

    public static EncryptPreferenceUtils getInstance() {
        if (instance == null) {
            synchronized (EncryptPreferenceUtils.class) {
                if (instance == null) {
                    instance = new EncryptPreferenceUtils();
                }
            }
        }
        return instance;
    }

    public EncryptPreferenceUtils init(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            preferences = EncryptedSharedPreferences.create(
                    NAME,
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            editor = preferences.edit();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public synchronized void setXUserAddress(String value) {
        editor.putString(KEY_1, value);
        editor.commit();
    }

    public synchronized String getXUserAddress() {
        return preferences.getString(KEY_1, "");
    }

    public synchronized void clearXUserAddress() {
        preferences.edit().remove(KEY_1).commit();
    }

    public synchronized void setMnemonic(String value) {
        editor.putString(KEY_4, value);
        editor.commit();
    }

    public synchronized String getMnemonic() {
        return preferences.getString(KEY_4, "");
    }

    public synchronized void clearMnemonic() {
        preferences.edit().remove(KEY_4).commit();
    }

    public synchronized void setXUserSignature(String value) {
        editor.putString(KEY_2, value);
        editor.commit();
    }

    public synchronized String getXUserSignature() {
        return preferences.getString(KEY_2, "");
    }

    public synchronized void clearXUserSignature() {
        preferences.edit().remove(KEY_2).commit();
    }

    public synchronized void setXUserSignatureMarket(String value) {
        editor.putString(KEY_5, value);
        editor.commit();
    }

    public synchronized String getXUserSignatureMarket() {
        return preferences.getString(KEY_5, "");
    }

    public synchronized void clearXUserSignatureMarket() {
        preferences.edit().remove(KEY_5).commit();
    }

    public synchronized void setNonceSignature(String value) {
        editor.putString(KEY_6, value);
        editor.commit();
    }

    public synchronized String getNonceSignature() {
        return preferences.getString(KEY_6, "");
    }

    public synchronized void clearNonceSignature() {
        preferences.edit().remove(KEY_6).commit();
    }

    public synchronized void setConnectWallet(String value) {
        editor.putString(KEY_3, value);
        editor.commit();
    }

    public synchronized String getConnectWallet() {
        return preferences.getString(KEY_3, "");
    }

    public synchronized void clearConnectWallet() {
        preferences.edit().remove(KEY_3).commit();
    }


}
