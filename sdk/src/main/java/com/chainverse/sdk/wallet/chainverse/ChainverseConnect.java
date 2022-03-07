package com.chainverse.sdk.wallet.chainverse;

import android.content.Context;
import android.net.Uri;

import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.common.WalletUtils;

import java.math.BigInteger;

public class ChainverseConnect {
    private Context context;

    public ChainverseConnect(Context context) {
        super();
        this.context = context;
    }

    public void connect(String message) {
        Utils.openURI(this.context, Uri.parse(buildUri(message, false)));
    }

    public void connect(String message, boolean isSignPersonal) {
        Utils.openURI(this.context, Uri.parse(buildUri(message, isSignPersonal)));
    }

    public void signMessageAndAccount(boolean isSignPersonal, String... args) {
        String uri = "chainverse://%s?action=%s&coin=%s&app=%s&callback=%s&id=%s&type=%s";
        for (int i = 0; i < args.length; i++) {
            uri += "&data." + i + "=" + args[i];
        }

        uri = String.format(uri,
                "sdk_account_sign_message",
                "account_sign_message",
                "20000714",
                ChainverseSDK.scheme,
                "sdk_account_sign_result",
                "2",
                isSignPersonal ? "personal" : "");

        Utils.openURI(this.context, Uri.parse(uri));
    }

    public void signMessage(boolean isSignPersonal, String message) {
        String uri = "chainverse://%s?action=%s&coin=%s&app=%s&callback=%s&id=%s&type=%s&data=%s";

        uri = String.format(uri,
                "sdk_sign_message",
                "sdk_sign_message",
                "20000714",
                ChainverseSDK.scheme,
                "sdk_sign_result",
                "1",
                isSignPersonal ? "personal" : "",
                message);

        Utils.openURI(this.context, Uri.parse(uri));
    }

    public void signTransaction(
            Constants.EFunction action,
            String to,
            String data,
            BigInteger value,
            BigInteger gasLimit,
            BigInteger gasPrice,
            BigInteger nonce) {

        String address = WalletUtils.getInstance().init(this.context).getAddress();

        String uri = String.format(
                "chainverse://sdk_sign_transaction?action=%s&asset=%s&to=%s&from=%s&data=%s&amount=%s&nonce=%s&fee_price=%s&fee_limit=%s&app=%s&callback=%s&confirm_type=%s&id=%s&meta=%s",
                action,
                "20000714",
                to,
                address,
                data,
                value.toString(),
                nonce.toString(),
                gasPrice.toString(),
                gasLimit.toString(),
                ChainverseSDK.scheme,
                "tx_callback",
                "sign",
                "1",
                "memo"
        );

        Utils.openURI(this.context, Uri.parse(uri));
    }

    public void sendTransaction(
            Constants.EFunction action,
            String to,
            String data,
            BigInteger value,
            BigInteger gasLimit,
            BigInteger gasPrice,
            BigInteger nonce) {

        String address = WalletUtils.getInstance().init(this.context).getAddress();

        String uri = String.format(
                "chainverse://sdk_transaction?action=%s&asset=%s&to=%s&from=%s&data=%s&amount=%s&nonce=%s&fee_price=%s&fee_limit=%s&app=%s&callback=%s&confirm_type=%s&id=%s&meta=%s",
                action,
                "20000714",
                to,
                address,
                data,
                value.toString(),
                nonce.toString(),
                gasPrice.toString(),
                gasLimit.toString(),
                ChainverseSDK.scheme,
                "tx_callback",
                "send",
                "2",
                "memo"
        );

        Utils.openURI(this.context, Uri.parse(uri));
    }

    private String buildUri(String message, boolean isSignPersonal) {
        return String.format("chainverse://%s?action=%s&coins.0=%s&coin=%s&data=%s&app=%s&callback=%s&id=%s&type=%s",
                "sdk_account_sign_message",
                "account_sign_message",
                "20000714",
                "20000714",
                message,
                ChainverseSDK.scheme,
                "sdk_account_sign_result",
                "2",
                isSignPersonal ? "personal" : "none");
    }

    public static class Builder {
        private ChainverseConnect chainverseConnect;

        public Builder() {
        }

        public ChainverseConnect build(Context context) {
            if (chainverseConnect == null) {
                this.chainverseConnect = new ChainverseConnect(context);
            }
            return this.chainverseConnect;
        }
    }
}
