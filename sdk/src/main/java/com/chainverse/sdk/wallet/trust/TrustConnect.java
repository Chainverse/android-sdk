package com.chainverse.sdk.wallet.trust;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.common.Utils;

public class TrustConnect {
    public TrustConnect(){
        super();
    }

    public void connect(Context context){
        Utils.openURI(context,buildUri());
    }

    private Uri buildUri(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("trust")
                .authority("sdk_get_accounts")
                .appendQueryParameter("action","get_accounts")
                .appendQueryParameter("app", ChainverseSDK.scheme)
                .appendQueryParameter("callback",ChainverseSDK.callbackHost)
                .appendQueryParameter("id","0")
                .appendQueryParameter("coins.0","60")
                .appendQueryParameter("coins.1","5741564")
                .appendQueryParameter("coins.2","283")
                .appendQueryParameter("coins.3","118")
                .appendQueryParameter("coins.4","714")
                ;

        return builder.build();
    }

    public static class Builder{
        public Builder(){}
        public TrustConnect build(){
            return new TrustConnect();
        }
    }
}
