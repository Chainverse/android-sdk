package com.chainverse.sdk;

import android.app.Activity;
import android.content.Intent;

public interface Chainverse {
    /**
     * init: init ChainverseSDK
     * @param activity
     * @param callback
     */
    void init(String developerAddress, String gameAddress, Activity activity, ChainverseCallback callback);

    /**
     * setKeepConnect: Keep connect wallet
     * @param keep: true(keep connect wallet) | false (reconnect wallet)
     */
    void setKeepConnect(boolean keep);

    /**
     * setScheme: setup connect wallet
     * @param scheme
     */
    void setScheme(String scheme);

    /**
     * setCallbackHost: setup connect wallet
     * @param host
     */
    void setHost(String host);

    /**
     * return list support wallet
     * @return
     */
    void getItems();

    /**
     * return version
     * @return
     */
    String getVersion();

    /**
     * handle result intent
     * @param intent
     */
    void onNewIntent(Intent intent);

    /**
     * showConnectView: Show screen choose wallet
     */
    void showConnectView();

    /**
     * connectWithTrust: Connect with Trust Wallet
     */
    void connectWithTrust();

    /**
     * connectWithChainverse: Connect with Chainverse
     */
    void connectWithChainverse();

    /**
     * logout: Logout
     */
    void logout();

    /**
     * isUserConnected: return status connected or no connected
     * @return
     */
    Boolean isUserConnected();

    /**
     * getUserInfo: Return user info
     * @return
     */
    ChainverseUser getUser();

    void testBuy();
}
