package com.chainverse.sdk;

import android.app.Activity;
import android.content.Intent;

public interface Chainverse {
    /**
     * init: Hàm init ChainverseSDK
     * @param activity
     * @param callback
     */
    void init(String developerAddress, String gameAddress, Activity activity, ChainverseCallback callback);

    /**
     * setKeepConnectWallet: Hàm thiết lập keep connect wallet
     * @param keep: true(giữ trạng thái connect từ lần trước đó) | false (phải connect lại)
     */
    void setKeepConnect(boolean keep);

    /**
     * setCallbackScheme: Hàm thiết lập scheme để ví Trust wallet callback về
     * @param scheme
     */
    void setScheme(String scheme);

    /**
     * setCallbackHost: Hàm thiết lập host để ví Trust wallet callback về
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
     * choose wallet
     */
    void showConnectView();

    /**
     * Connect with Trust Wallet
     */
    void connectWithTrust();

    void connectWithChainverse();

    /**
     * logout: Hàm thực hiện logout
     */
    void logout();

    void testBuy();
}
