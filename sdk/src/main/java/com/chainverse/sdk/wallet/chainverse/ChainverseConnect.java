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
        Utils.openURI(this.context, Uri.parse(buildUri(message)));
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
                "2",
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

    private String buildUri(String message) {
        return String.format("chainverse://%s?action=%s&coins.0=%s&coin=%s&data=%s&app=%s&callback=%s&id=%s",
                "sdk_account_sign_message",
                "account_sign_message",
                "20000714",
                "20000714",
                message,
                ChainverseSDK.scheme,
                "sdk_account_sign_result",
                "2");
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
