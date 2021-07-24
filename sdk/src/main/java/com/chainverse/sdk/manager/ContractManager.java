package com.chainverse.sdk.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.base.web3.BaseWeb3;
import com.chainverse.sdk.common.Constants;


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
        if(isGameContract() && isDeveloperContract() && isGamePaused()){
            return true;
        }else{
            return false;
        }
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
        EthCall ethCall = BaseWeb3.getInstance().init(mContext)
                .contract(
                        ChainverseSDK.developerAddress,
                        "isDeveloperContract",
                        new ArrayList<>()
                );
        if(Integer.decode(ethCall.getResult()) == 1){
            return true;
        }
        return false;
    }

    private boolean isGameContract(){
        EthCall ethCall = BaseWeb3.getInstance().init(mContext)
                .contract(
                        ChainverseSDK.gameAddress,
                        "isGameContract",
                        new ArrayList<>()
                );
        if(Integer.decode(ethCall.getResult()) == 1){
            return true;
        }
        return false;
    }

    private boolean isGamePaused(){
        EthCall ethCall = BaseWeb3.getInstance().init(mContext)
                .contract(
                        Constants.CONTRACT.ChainverseFactory,
                        "isGamePaused",
                        Arrays.asList(new Address(ChainverseSDK.gameAddress))
                );
        if(Integer.decode(ethCall.getResult()) == 1){
            return true;
        }
        return false;
    }

    private boolean isDeveloperPaused(){
        EthCall ethCall = BaseWeb3.getInstance().init(mContext)
                .contract(
                        Constants.CONTRACT.ChainverseFactory,
                        "isDeveloperPaused",
                        Arrays.asList(new Address(ChainverseSDK.developerAddress))
                );
        if(Integer.decode(ethCall.getResult()) == 1){
            return true;
        }
        return false;
    }
}
