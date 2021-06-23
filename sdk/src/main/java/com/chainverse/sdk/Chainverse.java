package com.chainverse.sdk;

import android.app.Activity;
import android.content.Intent;

import com.chainverse.sdk.model.Item;
import com.chainverse.sdk.model.User;
import com.chainverse.sdk.model.WL;

import java.util.ArrayList;

public interface Chainverse {
    /**
     * init SDK
     * @param activity
     * @param callback
     */
    void init(String developerAddress, String gameAddress, Activity activity, ChainverseCallback callback);

    /**
     * return list support wallet
     * @return
     */
    ArrayList<WL> getSupportedWallets();

    /**
     * login with signature
     * @param signature
     */
    void loginWithSignature(String signature);

    /**
     * return user
     * @return
     */
    User getUser();

    /**
     * return item;
     * @return
     */
    Item getItem();

    /**
     * return version
     * @return
     */
    String getVersion();

    /**
     * handle result intent
     * @param intent
     */
    void handleResult(Intent intent);

    /**
     * choose wallet
     */
    void chooseWallet();

    /**
     * Connect with Trust Wallet
     * @param callbackScheme
     * @param callbackHost
     */
    void connectTrustWL(String callbackScheme, String callbackHost);

    /**
     * Transfer with Trust Wallet
     * @param callbackScheme
     * @param asset
     * @param to
     * @param amount
     */
    void transferTrustWL(String callbackScheme, int asset, String to, String amount);
}
