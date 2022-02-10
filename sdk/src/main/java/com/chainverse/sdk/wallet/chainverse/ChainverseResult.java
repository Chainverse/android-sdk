package com.chainverse.sdk.wallet.chainverse;

import android.content.Intent;

public class ChainverseResult {
    public static String handleConnect(Intent intent) {
        return intent.getData().getQueryParameter("data");
    }

    public static String getUserAddress(Intent intent) {
        return intent.getData().getQueryParameter("accounts");
    }

    public static String getTime(Intent intent) {
        return intent.getData().getQueryParameter("time");
    }

    public static String getNonce(Intent intent) {
        return intent.getData().getQueryParameter("nonce");
    }

    public static String getUserSignature(Intent intent) {
        String signature = intent.getData().getQueryParameter("signature");
        if (signature.substring(0, 2).equals("0x")) {
            return intent.getData().getQueryParameter("signature");
        } else {
            return "0x" + intent.getData().getQueryParameter("signature");
        }
    }

    public static String getAction(Intent intent) {
        return intent.getData().getQueryParameter("action");
    }
}
