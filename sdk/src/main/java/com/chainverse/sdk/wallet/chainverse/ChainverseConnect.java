package com.chainverse.sdk.wallet.chainverse;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.wallet.trust.TrustConnect;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class ChainverseConnect {
    public ChainverseConnect(){
        super();
    }

    public void connect(Context context){
        Utils.openURI(context,Uri.parse(buildUri()));
    }

    private String buildUri(){
        return String.format("chainverse://%s?action=%s&coins.0=%s&coin=%s&data=%s&app=%s&callback=%s&id=%s",
                "sdk_account_sign_message",
                "account_sign_message",
                "20000714",
                "20000714",
                "chainverse",
                ChainverseSDK.scheme,
                "sdk_account_sign_result",
                "2");
    }

    public static class Builder{
        public Builder(){}
        public ChainverseConnect build(){
            return new ChainverseConnect();
        }
    }
}
