package com.chainverse.sdk.wl.trust;

import android.content.Intent;

public class TrustResult {
    public static String handleConnect(Intent intent){
        return intent.getData().getQueryParameter("data");
    }
}
