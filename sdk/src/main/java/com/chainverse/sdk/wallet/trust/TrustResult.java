package com.chainverse.sdk.wallet.trust;

import android.content.Intent;

public class TrustResult {
    public static String handleConnect(Intent intent){
        return intent.getData().getQueryParameter("data");
    }
}
