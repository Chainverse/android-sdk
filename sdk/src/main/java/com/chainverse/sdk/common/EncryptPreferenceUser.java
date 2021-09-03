package com.chainverse.sdk.common;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class EncryptPreferenceUser {
    public static final String NAME =  "CHAINVERSE_SDK";
    public static final String KEY_1 = "CHAINVERSE_SDK_KEY_1";
    public static final String KEY_2 = "CHAINVERSE_SDK_KEY_2";
    public static final String KEY_3 = "CHAINVERSE_SDK_KEY_3";
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private static EncryptPreferenceUser instance;

    public static EncryptPreferenceUser getInstance(){
        if(instance == null){
            synchronized (EncryptPreferenceUser.class){
                if(instance == null){
                    instance = new EncryptPreferenceUser();
                }
            }
        }
        return instance;
    }

    public EncryptPreferenceUser init(Context context){
        if(preferences == null){
            preferences = context.getSharedPreferences(NAME, 0);
        }
        if(editor == null){
            editor = preferences.edit();
        }
        return this;
    }

    public synchronized void setXUserAddress(String value){
        editor.putString(KEY_1,value);
        editor.commit();
    }

    public synchronized String getXUserAddress(){
        return preferences.getString(KEY_1,"");
    }

    public synchronized void clearXUserAddress(){
        preferences.edit().remove(KEY_1).commit();
    }

    public synchronized void setXUserSignature(String value){
        editor.putString(KEY_2,value);
        editor.commit();
    }

    public synchronized String getXUserSignature(){
        return preferences.getString(KEY_2,"");
    }

    public synchronized void clearXUserSignature(){
        preferences.edit().remove(KEY_2).commit();
    }

    public synchronized void setConnectWallet(String value){
        editor.putString(KEY_3,value);
        editor.commit();
    }

    public synchronized String getConnectWallet(){
        return preferences.getString(KEY_3,"");
    }

    public synchronized void clearConnectWallet(){
        preferences.edit().remove(KEY_3).commit();
    }

}
