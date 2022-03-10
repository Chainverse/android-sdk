package com.chainverse.sdk.common;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.chainverse.sdk.model.MessageNonce;
import com.chainverse.sdk.model.service.ChainverseService;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;

import wallet.core.jni.StoredKey;


public class EncryptPreferenceUtils {
    public static final String NAME = "chainverse_secret_shared_prefs";
    public static final String SERVICE = "SERVICE";
    public static final String PATH_STORED_KEY = "PATH_STORED_KEY";
    public static final String RPC = "RPC";
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

    public synchronized void setXUserMessageNonce(MessageNonce message) {
        Gson gson = new Gson();
        String value = gson.toJson(message);
        editor.putString(KEY_5, value);
        editor.commit();
    }

    public synchronized MessageNonce getXUserMessageNonce() {
        Gson gson = new Gson();
        String value = preferences.getString(KEY_5, "");
        MessageNonce messageNonce = gson.fromJson(value, MessageNonce.class);
        return messageNonce;
    }

    public synchronized void clearXUserMessageNonce() {
        preferences.edit().remove(KEY_5).commit();
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

    public synchronized void setService(ChainverseService chainverseService) {
        Gson gson = new Gson();
        String value = gson.toJson(chainverseService);
        editor.putString(SERVICE, value);
        editor.commit();
    }

    public synchronized ChainverseService getService() {
        Gson gson = new Gson();
        String value = preferences.getString(SERVICE, "");
        ChainverseService chainverseService = gson.fromJson(value, ChainverseService.class);
        return chainverseService;
    }

    public synchronized void clearService() {
        preferences.edit().remove(SERVICE).commit();
    }

    public synchronized void setPathStoredKey(String path) {
        editor.putString(PATH_STORED_KEY, path);
        editor.commit();
    }

    public synchronized String getPathStoredKey() {
        String value = preferences.getString(PATH_STORED_KEY, "");
        return value;
    }

    public synchronized void clearPathStoredKey() {
        preferences.edit().remove(PATH_STORED_KEY).commit();
    }

    public synchronized void setRPC(String path) {
        editor.putString(PATH_STORED_KEY, path);
        editor.commit();
    }

    public synchronized String getRPC() {
        String value = preferences.getString(PATH_STORED_KEY, "");
        return value;
    }

    public synchronized void clearRPC() {
        preferences.edit().remove(PATH_STORED_KEY).commit();
    }

}
