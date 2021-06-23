package com.chainverse.sdk.wl.trust;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class TrustConnect {
    private String callbackScheme;
    private String callbackHost;

    public TrustConnect(String callbackScheme, String callbackHost){
        super();
        this.callbackScheme = callbackScheme;
        this.callbackHost = callbackHost;
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
                .appendQueryParameter("app",callbackScheme)
                .appendQueryParameter("callback",callbackHost)
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
        private String callbackScheme;
        private String callbackHost;

        public Builder(){}

        public Builder setCallbackScheme(String scheme){
            this.callbackScheme = scheme;
            return this;
        }

        public Builder setCallbackHost(String host){
            this.callbackHost = host;
            return this;
        }

        public TrustConnect build(){
            return new TrustConnect(callbackScheme,callbackHost);
        }
    }
}
