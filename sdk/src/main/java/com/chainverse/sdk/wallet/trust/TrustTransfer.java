package com.chainverse.sdk.wallet.trust;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.math.BigDecimal;

public class TrustTransfer {
    private String callbackScheme;
    private int asset;
    private String to;
    private BigDecimal amount;

    public TrustTransfer(String scheme, int asset, String to, BigDecimal amount){
        super();
        this.callbackScheme = scheme;
        this.asset = asset;
        this.to = to;
        this.amount = amount;
    }

    public void transfer(Context context){
        Intent intent = new Intent(Intent.ACTION_VIEW, buildUri());
        context.startActivity(intent);
    }

    private Uri buildUri(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("trust")
                .authority("sdk_transaction")
                .appendQueryParameter("action","transfer")
                .appendQueryParameter("asset","c" + asset)
                .appendQueryParameter("to",to)
                .appendQueryParameter("amount",amount.toString())
                .appendQueryParameter("nonce","-1")
                .appendQueryParameter("app",callbackScheme)
                .appendQueryParameter("callback","tx_callback")
                .appendQueryParameter("confirm_type","send")
                .appendQueryParameter("id","1")
                .appendQueryParameter("meta","memo");

        return builder.build();
    }

    public static class Builder{
        private String callbackScheme;
        private int asset;
        private String to;
        private BigDecimal amount;
        public Builder(){}

        public Builder setCallbackScheme(String scheme){
            this.callbackScheme = scheme;
            return this;
        }

        public Builder asset(int asset){
            this.asset = asset;
            return this;
        }

        public Builder to(String toAddress){
            this.to = toAddress;
            return this;
        }

        public Builder amount(BigDecimal amount){
            this.amount = amount;
            return this;
        }

        public TrustTransfer build(){
            return new TrustTransfer(callbackScheme,asset,to,amount);
        }
    }
}
