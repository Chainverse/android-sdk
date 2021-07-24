package com.chainverse.sdk;

import android.app.Activity;
import android.content.Intent;

import com.chainverse.sdk.model.Item;

import java.util.ArrayList;

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
    void setKeepConnectWallet(boolean keep);

    /**
     * setCallbackScheme: Hàm thiết lập scheme để ví Trust wallet callback về
     * @param scheme
     */
    void setCallbackScheme(String scheme);

    /**
     * setCallbackHost: Hàm thiết lập host để ví Trust wallet callback về
     * @param host
     */
    void setCallbackHost(String host);

    /**
     * return list support wallet
     * @return
     */
    ArrayList<Item> getItems();

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
    void showConnectWalletView();

    /**
     * Connect with Trust Wallet
     */
    void connectTrust();

    /**
     * Transfer with Trust Wallet
     * @param callbackScheme
     * @param asset
     * @param to
     * @param amount
     */
    void transferTrustWL(String callbackScheme, int asset, String to, String amount);

    /**
     * logout: Hàm thực hiện logout
     */
    void logout();
}
