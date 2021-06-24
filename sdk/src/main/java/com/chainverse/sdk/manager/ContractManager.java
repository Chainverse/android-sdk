package com.chainverse.sdk.manager;

import android.annotation.SuppressLint;
import android.util.Log;

import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.rpc.RPCClient;
import com.chainverse.sdk.rpc.raw.RPCParams;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ContractManager {
    public interface Listener{
        void isChecked(boolean isCheck);
    }

    private String developerAddress;
    private String gameAddress;
    private Listener listener;
    public ContractManager(String developerAddress, String gameAddress, Listener listener){
        this.listener = listener;
        this.developerAddress = developerAddress;
        this.gameAddress = gameAddress;
    }

    public void check(){
        checkDeveloperContract();
    }

    @SuppressLint("CheckResult")
    private void checkDeveloperContract(){
        //RPC param
        RPCParams param = new RPCParams();
        param.setDeveloperAddress(developerAddress);

        RPCClient.request(RPCClient.createParams(param),"eth_blockNumber")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    Log.e("nampv","ok3" );
                    boolean respone = false;
                    if(respone){
                        checkGameContract();
                    }
                },throwable -> {

                });
    }

    @SuppressLint("CheckResult")
    private void checkGameContract(){
        //RPC param
        RPCParams param = new RPCParams();
        param.setGameAddress(gameAddress);

        RPCClient.request(RPCClient.createParams(param),"eth_blockNumber")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    Log.e("nampv","ok3" );
                    boolean respone = false;
                    if(respone){
                        checkGamePaused();
                    }
                },throwable -> {

                });
    }

    @SuppressLint("CheckResult")
    private void checkGamePaused(){
        //RPC param
        RPCParams param = new RPCParams();
        param.setChainverseFactory(Constants.CONTRACT.ChainverseFactory);
        RPCClient.request(RPCClient.createParams(param),"eth_blockNumber")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    Log.e("nampv","ok3" );
                    boolean respone = false;
                    if(respone){
                        checkDeveloperPaused();
                    }

                },throwable -> {

                });
    }

    @SuppressLint("CheckResult")
    private void checkDeveloperPaused(){
        //RPC param
        RPCParams param = new RPCParams();
        param.setChainverseFactory(Constants.CONTRACT.ChainverseFactory);

        RPCClient.request(RPCClient.createParams(param),"eth_blockNumber")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    Log.e("nampv","ok3" );
                    boolean respone = false;
                    if(respone){
                        listener.isChecked(respone);
                    }
                },throwable -> {

                });
    }
}
