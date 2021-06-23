package com.chainverse.sdk;

import android.app.Activity;
import android.content.Intent;

import com.chainverse.sdk.model.Item;
import com.chainverse.sdk.model.User;
import com.chainverse.sdk.model.WL;
import com.chainverse.sdk.manager.ContractManager;
import com.chainverse.sdk.ui.ChainverseSDKActivity;
import com.chainverse.sdk.wl.trust.TrustConnect;
import com.chainverse.sdk.wl.trust.TrustResult;
import com.chainverse.sdk.wl.trust.TrustTransfer;


import java.math.BigDecimal;
import java.util.ArrayList;

public class ChainverseSDK implements Chainverse {
    private static ChainverseSDK mInstance;
    private ChainverseCallback mCallback;
    private Activity mContext;
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
        mCallback = callback;
        mContext = activity;
        checkContract(developerAddress, gameAddress);
    }

    private void checkContract(String developerAddress, String gameAddress){
        ContractManager checkContract = new ContractManager(developerAddress, gameAddress, new ContractManager.Listener() {
            @Override
            public void isChecked(boolean isCheck) {
                if(isCheck){
                    doInit();
                }
            }

        });
        checkContract.check();
    }

    private void doInit(){

    }

    @Override
    public ArrayList<WL> getSupportedWallets() {
        return null;
    }

    @Override
    public void loginWithSignature(String signature) {

    }

    @Override
    public User getUser() {
        return null;
    }

    @Override
    public Item getItem() {
        return null;
    }

    @Override
    public String getVersion() {
        return "1.0.0" ;
    }

    @Override
    public void handleResult(Intent intent) {
        String trustWLData = TrustResult.handleConnect(intent);
        if(mCallback != null){
            mCallback.onConnectedWallet(trustWLData);
        }
    }

    @Override
    public void chooseWallet() {
        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
        intent.putExtra("screen","choosewl");
        mContext.startActivity(intent);
    }

    @Override
    public void connectTrustWL(String callbackScheme, String callbackHost) {
        TrustConnect trustConnect = new TrustConnect.Builder()
                .setCallbackScheme(callbackScheme)
                .setCallbackHost(callbackHost)
                .build();
        trustConnect.connect(mContext);
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


}
