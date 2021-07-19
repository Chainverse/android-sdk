package com.chainverse.sdk.wallet.trust;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.chainverse.sdk.ChainverseSDK;

public class TrustConnect {
    public TrustConnect(){
        super();
    }

    public void connect(Context context){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, buildUri());
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Uri buildUri(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("trust")
                .authority("sdk_get_accounts")
                .appendQueryParameter("action","get_accounts")
                .appendQueryParameter("app", ChainverseSDK.callbackScheme)
                .appendQueryParameter("callback",ChainverseSDK.callbackHost)
                .appendQueryParameter("id","0")
                .appendQueryParameter("coins.0","60")
                .appendQueryParameter("coins.1","5741564")
                .appendQueryParameter("coins.2","283")
                .appendQueryParameter("coins.3","118")
                .appendQueryParameter("coins.4","714")
                .appendQueryParameter("coins.5","145");

        return builder.build();
    }

    public static class Builder{
        public Builder(){}
        public TrustConnect build(){
            return new TrustConnect();
        }
    }
}
