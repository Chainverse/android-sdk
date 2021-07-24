package com.chainverse.sdk.common;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class EncryptPreferenceUser {
    public static final String NAME = "ChainverseEncryptPreferenceUser";
    public static final String KEY_1 = "chainverse_x_user_address";
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

        }
        return instance;
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

}
