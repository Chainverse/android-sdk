package com.chainverse.sdk.wallet.chainverse;

import android.content.Intent;

import com.chainverse.sdk.common.LogUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    public static List<String> getMultiUserSignature(Intent intent) {
        List<String> signatures = new ArrayList();
        Set<String> params = intent.getData().getQueryParameterNames();
        for (String param : params) {
            if (param.indexOf("signature") >= 0) {
                signatures.add(intent.getData().getQueryParameter(param));
            }
        }
        return signatures;
    }

    public static String getAction(Intent intent) {
        return intent.getData().getQueryParameter("action");
    }
}
