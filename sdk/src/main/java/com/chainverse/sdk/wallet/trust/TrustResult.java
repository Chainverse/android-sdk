package com.chainverse.sdk.wallet.trust;

import android.content.Intent;

public class TrustResult {
    public static String handleConnect(Intent intent){
        return intent.getData().getQueryParameter("data");
    }

    public static String getUserAddress(Intent intent){
        String data = intent.getData().getQueryParameter("data");
        String[] tmp = data.split(",");
        String userAddress = tmp[0];
        return userAddress;
    }

    public static String getAction(Intent intent){
        return intent.getData().getQueryParameter("action");
    }
}
