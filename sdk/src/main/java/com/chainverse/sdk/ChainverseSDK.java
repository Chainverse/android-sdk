package com.chainverse.sdk;

import android.app.Activity;
import android.content.Intent;

import com.chainverse.sdk.common.CallbackToGame;
import com.chainverse.sdk.common.EncryptPreferenceUser;
import com.chainverse.sdk.model.Item;
import com.chainverse.sdk.manager.ContractManager;
import com.chainverse.sdk.ui.ChainverseSDKActivity;
import com.chainverse.sdk.wallet.trust.TrustConnect;
import com.chainverse.sdk.wallet.trust.TrustResult;
import com.chainverse.sdk.wallet.trust.TrustTransfer;


import java.math.BigDecimal;
import java.util.ArrayList;

public class ChainverseSDK implements Chainverse {
    private static ChainverseSDK mInstance;
    public static String developerAddress;
    public static String gameAddress;
    public static String callbackScheme;
    public static String callbackHost;
    public static ChainverseCallback mCallback;

    private boolean isKeepConnectWallet = true;
    private Activity mContext;
    private EncryptPreferenceUser encryptPreferenceUser;
    public static ChainverseSDK getInstance(){
        if(mInstance == null){
            synchronized (ChainverseSDK.class){
                if(mInstance == null){
                    mInstance = new ChainverseSDK();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void init(String developerAddress, String gameAddress, Activity activity, ChainverseCallback callback) {
        this.mCallback = callback;
        this.mContext = activity;
        this.gameAddress = gameAddress;
        this.developerAddress = developerAddress;
        encryptPreferenceUser = EncryptPreferenceUser.getInstance().init(mContext);
        exceptionSDK();
        checkContract();
    }

    @Override
    public void setKeepConnectWallet(boolean keep) {
        isKeepConnectWallet = keep;
    }

    private void exceptionSDK(){
        ChainverseExeption.developerAddressExeption();
        ChainverseExeption.gameAddressExeption();
    }

    @Override
    public void setCallbackScheme(String scheme) {
        callbackScheme = scheme;
    }

    @Override
    public void setCallbackHost(String host) {
        callbackHost = host;
    }

    @Override
    public ArrayList<Item> getItems() {
        return null;
    }

    private void checkContract(){
        ContractManager checkContract = new ContractManager(mContext, new ContractManager.Listener() {
            @Override
            public void isChecked(boolean isCheck) {
                CallbackToGame.onInitSDK(isCheck);
                if(isCheck){
                    doInit();
                }
            }

        });
        checkContract.check();
    }


    private void doInit(){
        if(isKeepConnectWallet){
            keepConnectWallet();
        }else{
            encryptPreferenceUser.clearXUserAddress();
        }

    }

    private void keepConnectWallet(){
        if(!encryptPreferenceUser.getXUserAddress().isEmpty()){
            CallbackToGame.onUserAddress(encryptPreferenceUser.getXUserAddress());
        }
    }

    @Override
    public String getVersion() {
        return "1.0.0" ;
    }

    @Override
    public void onNewIntent(Intent intent) {
        String action = TrustResult.getAction(intent);
        switch (action){
            case "get_accounts":
                String xUserAddress = TrustResult.getUserAddress(intent);
                EncryptPreferenceUser.getInstance().init(mContext).setXUserAddress(xUserAddress);
                CallbackToGame.onUserAddress(xUserAddress);
                break;
        }
    }

    @Override
    public void showConnectWalletView() {
        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
        intent.putExtra("screen","showConnectWalletView");
        mContext.startActivity(intent);
    }

    @Override
    public void connectTrust() {
        TrustConnect trust = new TrustConnect.Builder().build();
        trust.connect(mContext);
    }

    @Override
    public void transferTrustWL(String callbackScheme, int asset, String toAddress, String amount) {
        TrustTransfer trustTransfer = new TrustTransfer.Builder()
                .setCallbackScheme(callbackScheme)
                .asset(asset)
                .to(toAddress)
                .amount(new BigDecimal(amount))
                .build();
        trustTransfer.transfer(mContext);
    }

    @Override
    public void logout() {
        CallbackToGame.onUserLogout(encryptPreferenceUser.getXUserAddress());
        encryptPreferenceUser.clearXUserAddress();
    }


}
