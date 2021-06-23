package com.chainverse.sdk.common;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefUtil {
    public static final String KEY_1 = "dont_edit_1";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;

    private static PrefUtil instance;

    public static PrefUtil getInstance(){
        if(instance == null){
            synchronized (PrefUtil.class){
                if(instance == null){
                    instance = new PrefUtil();
                }
            }
        }
        return instance;
    }

    public PrefUtil init(Context context){
        if(preferences == null){
            preferences = context.getSharedPreferences("chainverse", PRIVATE_MODE);
            editor = preferences.edit();
        }
        return instance;
    }

    public synchronized void chooseWallet(String value){
        editor.putString(KEY_1,value);
        editor.commit();
    }

    public synchronized String getChooseWallet(){
        return preferences.getString(KEY_1,"");
    }

}
