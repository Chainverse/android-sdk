package com.chainverse.sdk.manager;

import android.content.Context;

import com.chainverse.sdk.ChainverseError;
import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.base.web3.BaseWeb3;
import com.chainverse.sdk.common.CallbackToGame;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.Convert;


import org.web3j.abi.datatypes.Address;
import org.web3j.protocol.core.methods.response.EthCall;

import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rx.functions.Func0;


public class ContractManager {
    public interface Listener{
        void isChecked(boolean isCheck);
    }
    private Listener listener;
    private Context mContext;
    public ContractManager(Context context, Listener listener){
        this.listener = listener;
        mContext = context;
    }

    private Observable<Boolean> checkContractObservable(){
        return Observable.defer(new Func0<ObservableSource<? extends Boolean>>(){

            @Override
            public ObservableSource<? extends Boolean> call() {
                return Observable.just(checkContract());
            }
        });
    }

    private Boolean checkContract(){
        if(!isGameContract()){
            CallbackToGame.onError(ChainverseError.ERROR_GAME_ADDRESS);
        }

        if(!isDeveloperContract()){
            CallbackToGame.onError(ChainverseError.ERROR_DEVELOPER_ADDRESS);
        }

        if(isGamePaused()){
            CallbackToGame.onError(ChainverseError.ERROR_GAME_PAUSE);
        }

        if(isDeveloperPaused()){
            CallbackToGame.onError(ChainverseError.ERROR_DEVELOPER_PAUSE);
        }

        if(isGameContract() && isDeveloperContract() && !isGamePaused() && !isDeveloperPaused()){
            return true;
        }
        return false;
    }


    public void check(){
        checkContractObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isChecked -> {
                    listener.isChecked(isChecked);
                });
    }

    private boolean isDeveloperContract(){
        try {
            EthCall ethCall = BaseWeb3.getInstance().init(mContext).callFunction(ChainverseSDK.developerAddress, "isDeveloperContract", new ArrayList<>());
            if(ethCall != null && ethCall.getResult() != null){
                return Convert.hexToBool(ethCall.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isGameContract(){
        try {
            EthCall ethCall = BaseWeb3.getInstance().init(mContext).callFunction(ChainverseSDK.gameAddress, "isGameContract", new ArrayList<>());
            if(ethCall != null && ethCall.getResult() != null){
                return Convert.hexToBool(ethCall.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isGamePaused(){
        try {
            EthCall ethCall = BaseWeb3.getInstance().init(mContext).callFunction(Constants.CONTRACT.ChainverseFactory, "isGamePaused", Arrays.asList(new Address(ChainverseSDK.gameAddress)));
            if(ethCall != null && ethCall.getResult() != null){
                return Convert.hexToBool(ethCall.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isDeveloperPaused(){
        try {
            EthCall ethCall = BaseWeb3.getInstance().init(mContext).callFunction(Constants.CONTRACT.ChainverseFactory, "isDeveloperPaused", Arrays.asList(new Address(ChainverseSDK.developerAddress)));
            if(ethCall != null && ethCall.getResult() != null){
                return Convert.hexToBool(ethCall.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
