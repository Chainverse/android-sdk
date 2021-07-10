package com.chainverse.sdk.manager;

import android.annotation.SuppressLint;
import android.util.Log;

import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.network.RPC.RPCClient;
import com.chainverse.sdk.network.RPC.raw.RPCParams;

import java.util.ArrayList;

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
        param.setTo("0x690FDdc2a98050f924Bd7Ec5900f2D2F49b6aEC7");
        param.setData("0x61f718bb");

        RPCClient.request(RPCClient.createParams(param),"eth_call")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    //Log.e("nampv_log",jsonElement.toString() );
                    boolean respone = true;
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
        param.setTo("0x3F57BF31E55de54306543863E079aD234f477b88");
        param.setData("0x244675aa");

        RPCClient.request(RPCClient.createParams(param),"eth_call")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    //Log.e("nampv_log",jsonElement.toString() );
                    boolean respone = true;
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
        param.setTo("0xd786Db6012d7A542e7531068b0f987Da6414C54B");
        param.setData("0x44e097aa0000000000000000000000003f57bf31e55de54306543863e079ad234f477b88");
        RPCClient.request(RPCClient.createParams(param),"eth_call")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    //Log.e("nampv_log",jsonElement.toString() );
                    boolean respone = true;
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
        param.setTo("0xd786Db6012d7A542e7531068b0f987Da6414C54B");
        param.setData("0x1298d00d000000000000000000000000690fddc2a98050f924bd7ec5900f2d2f49b6aec7");

        RPCClient.request(RPCClient.createParams(param),"eth_call")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    Log.e("nampv_log",jsonElement.toString() );
                    boolean respone = true;
                    if(respone){
                        listener.isChecked(respone);
                    }
                },throwable -> {

                });
    }
}
