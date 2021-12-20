package com.chainverse.sdk.common;

import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BroadcastUtil {
    public static void send(Context context, String action){
        Intent intent = new Intent();
        intent.setAction(action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
