package com.chainverse.sdk;

import android.app.Activity;
import android.content.Intent;

import com.chainverse.sdk.common.CallbackToGame;
import com.chainverse.sdk.common.EncryptPreferenceUser;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.listener.OnEmitterListenter;
import com.chainverse.sdk.manager.ContractManager;
import com.chainverse.sdk.manager.TransferItemManager;
import com.chainverse.sdk.network.RESTful.RESTfulClient;
import com.chainverse.sdk.ui.ChainverseSDKActivity;
import com.chainverse.sdk.wallet.chainverse.ChainverseConnect;
import com.chainverse.sdk.wallet.chainverse.ChainverseResult;
import com.chainverse.sdk.wallet.trust.TrustConnect;
import com.chainverse.sdk.wallet.trust.TrustResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;


import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChainverseSDK implements Chainverse {
    private static ChainverseSDK mInstance;
    public static String developerAddress;
    public static String gameAddress;
    public static String scheme;
    public static String callbackHost;
    public static ChainverseCallback mCallback;

    private boolean isKeepConnect = true;
    private Activity mContext;
    private EncryptPreferenceUser encryptPreferenceUser;
    private boolean isInitSDK = false;
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
    public void setKeepConnect(boolean keep) {
        isKeepConnect = keep;
    }

    private void exceptionSDK(){
        ChainverseExeption.developerAddressExeption();
        ChainverseExeption.gameAddressExeption();
    }

    @Override
    public void setScheme(String scheme) {
        ChainverseSDK.scheme = scheme;
    }

    @Override
    public void setHost(String host) {
        callbackHost = host;
    }

    @Override
    public void getItems() {
        if(!isInitSDKSuccess()){
            return;
        }
        if(isUserConnected()){
            RESTfulClient.getItems(encryptPreferenceUser.getXUserAddress(),ChainverseSDK.gameAddress)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(jsonElement -> {
                        if(Utils.getErrorCodeResponse(jsonElement) == 0){
                            Gson gson = new Gson();
                            ArrayList<ChainverseItem> items = gson.fromJson(jsonElement.getAsJsonObject().get("items"),new TypeToken<ArrayList<ChainverseItem>>(){}.getType());
                            CallbackToGame.onGetItems(items);
                        }else{
                            CallbackToGame.onError(ChainverseError.ERROR_REQUEST_ITEM);
                        }

                    },throwable -> {
                        throwable.printStackTrace();
                        CallbackToGame.onError(ChainverseError.ERROR_REQUEST_ITEM);
                    });
        }


    }

    private void checkContract(){
        ContractManager checkContract = new ContractManager(mContext, new ContractManager.Listener() {
            @Override
            public void isChecked(boolean isCheck) {
                if(isCheck){
                    CallbackToGame.onInitSDKSuccess();
                    doInit();
                }else{
                    CallbackToGame.onError(ChainverseError.ERROR_INIT_SDK);
                }
            }

        });
        checkContract.check();
    }


    private void doInit(){
        isInitSDK = true;
        if(isKeepConnect){
            doConnectSuccess();
        }else{
            encryptPreferenceUser.clearXUserAddress();
            encryptPreferenceUser.clearXUserSignature();
        }

    }

    private Boolean isInitSDKSuccess(){
        if(!isInitSDK){
            CallbackToGame.onError(ChainverseError.ERROR_WAITING_INIT_SDK);
            return false;
        }
        return true;
    }

    private void doConnectSuccess(){
        if(isUserConnected()){
            CallbackToGame.onConnectSuccess(encryptPreferenceUser.getXUserAddress());
            TransferItemManager transferItemManager = new TransferItemManager(mContext);
            transferItemManager.on(new OnEmitterListenter() {
                @Override
                public void call(String event, Object... args) {
                    switch (event){
                        case "transfer_item_to_user":
                            if(args.length > 0){
                                JsonElement jsonElement = new JsonParser().parse(args[0].toString());
                                Gson gson = new Gson();
                                ChainverseItem item = gson.fromJson(jsonElement.getAsJsonObject(),new TypeToken<ChainverseItem>(){}.getType());
                                CallbackToGame.onItemUpdate(item,ChainverseItem.TRANSFER_ITEM_TO_USER);
                                getItems();
                            }

                            break;
                        case "transfer_item_from_user":
                            if(args.length > 0){
                                JsonElement jsonElement = new JsonParser().parse(args[0].toString());
                                Gson gson = new Gson();
                                ChainverseItem item = gson.fromJson(jsonElement.getAsJsonObject(),new TypeToken<ChainverseItem>(){}.getType());
                                CallbackToGame.onItemUpdate(item,ChainverseItem.TRANSFER_ITEM_FROM_USER);
                                getItems();
                            }
                            break;
                    }
                    LogUtil.log("socket_" + event,args);
                }
            });
            transferItemManager.connect();
        }
    }

    @Override
    public String getVersion() {
        return ChainverseVersion.BUILD ;
    }

    @Override
    public void onNewIntent(Intent intent) {
        if(encryptPreferenceUser.getConnectWallet().equals("trust")){
            String action = TrustResult.getAction(intent);
            switch (action){
                case "get_accounts":
                    String xUserAddress = TrustResult.getUserAddress(intent);
                    encryptPreferenceUser.setXUserAddress(xUserAddress);
                    doConnectSuccess();
                    break;
            }
        }else{
            String xUserAddress = ChainverseResult.getUserAddress(intent);
            String xUserSignature = ChainverseResult.getUserSignature(intent);
            encryptPreferenceUser.setXUserAddress(xUserAddress);
            encryptPreferenceUser.setXUserSignature(xUserSignature);
            doConnectSuccess();
        }
    }

    @Override
    public void showConnectView() {
        if(!isInitSDKSuccess()){
            return;
        }
        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
        intent.putExtra("screen","showConnectView");
        mContext.startActivity(intent);
    }

    @Override
    public void connectWithTrust() {
        if(!isInitSDKSuccess()){
            return;
        }
        encryptPreferenceUser.setConnectWallet("trust");
        TrustConnect trust = new TrustConnect.Builder().build();
        trust.connect(mContext);
    }

    @Override
    public void connectWithChainverse() {
        if(!isInitSDKSuccess()){
            return;
        }

        if(Utils.isChainverseInstalled(mContext)){
            encryptPreferenceUser.setConnectWallet("chainverse");
            ChainverseConnect chainverse = new ChainverseConnect.Builder().build();
            chainverse.connect(mContext);
        }
    }


    @Override
    public void logout() {
        if(!isInitSDKSuccess()){
            return;
        }
        CallbackToGame.onLogout(encryptPreferenceUser.getXUserAddress());
        encryptPreferenceUser.clearXUserAddress();
        encryptPreferenceUser.clearXUserSignature();
    }

    @Override
    public Boolean isUserConnected() {
        if(!encryptPreferenceUser.getXUserSignature().isEmpty()){
            return true;
        }
        return false;
    }

    @Override
    public ChainverseUser getUser() {
        if(isUserConnected()){
            ChainverseUser info = new ChainverseUser();
            info.setAddress(encryptPreferenceUser.getXUserAddress());
            info.setSignature(encryptPreferenceUser.getXUserSignature());
            return info;
        }
        return null;
    }

    @Override
    public void testBuy() {
        RESTfulClient.testBuy()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    LogUtil.log("nampv_testbuy",jsonElement.toString());

                },throwable -> {
                    throwable.printStackTrace();
                });
    }


}
