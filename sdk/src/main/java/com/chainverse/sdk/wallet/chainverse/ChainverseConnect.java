package com.chainverse.sdk.wallet.chainverse;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.wallet.trust.TrustConnect;

public class ChainverseConnect {
    public ChainverseConnect(){
        super();
    }

    public void connect(Context context){
        try{
            String test = "chainverse://sdk_account_sign_message?action=account_sign_message&coins.0=20000714&coin=20000714&data=chainverse&app=trust-rn-example://&callback=sdk_account_sign_result&id=2";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(test));
            context.startActivity(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private Uri buildUri(){
        Uri.Builder builder = new Uri.Builder();
        /*builder.scheme("chainverse")
                .authority("sdk_account_sign_message")
                .appendQueryParameter("action","account_sign_message")
                .appendQueryParameter("coins.0","20000714")
                .appendQueryParameter("coin","20000714")
                .appendQueryParameter("data","chainverse")
                .appendQueryParameter("app", ChainverseSDK.scheme)
                .appendQueryParameter("callback","accounts_callback")
                .appendQueryParameter("id","0")
        ;*/
        builder.scheme("chainverse")
                .authority("sdk_get_accounts")
                .appendQueryParameter("action","get_accounts")
                .appendQueryParameter("app", ChainverseSDK.scheme)
                .appendQueryParameter("callback","accounts_callback")
                .appendQueryParameter("coins.0","20000714")
                .appendQueryParameter("coins.1","0")
                .appendQueryParameter("id","0")
        ;

        Log.e("chainverse_connect", builder.build().toString());
        return builder.build();
    }

    public static class Builder{
        public Builder(){}
        public ChainverseConnect build(){
            return new ChainverseConnect();
        }
    }
}
