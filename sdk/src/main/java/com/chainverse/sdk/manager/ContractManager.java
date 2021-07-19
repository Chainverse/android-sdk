package com.chainverse.sdk.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.chainverse.sdk.base.web3.BaseWeb3;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.network.RPC.RPCClient;
import com.chainverse.sdk.network.RPC.raw.RPCParams;

import org.web3j.abi.datatypes.Address;
import org.web3j.protocol.core.methods.response.EthCall;

import java.util.ArrayList;
import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ContractManager {
    public interface Listener{
        void isChecked(boolean isCheck);
    }

    private String developerAddress;
    private String gameAddress;
    private Listener listener;
    private Context mContext;
    public ContractManager(Context context, String developerAddress, String gameAddress, Listener listener){
        this.listener = listener;
        mContext = context;
        this.developerAddress = developerAddress;
        this.gameAddress = gameAddress;
    }

    public void check(){
        if(isGameContract() && isDeveloperContract() && isGamePaused()){
            listener.isChecked(true);
        }else{
            listener.isChecked(false);
        }
    }

    private boolean isDeveloperContract(){
        EthCall ethCall = BaseWeb3.getInstance().init(mContext)
                .contract(
                        developerAddress,
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
                        gameAddress,
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
                        "0xd786Db6012d7A542e7531068b0f987Da6414C54B",
                        "isGamePaused",
                        Arrays.asList(new Address(gameAddress))
                );
        if(Integer.decode(ethCall.getResult()) == 1){
            return true;
        }
        return false;
    }

    private boolean isDeveloperPaused(){
        EthCall ethCall = BaseWeb3.getInstance().init(mContext)
                .contract(
                        "0xd786Db6012d7A542e7531068b0f987Da6414C54B",
                        "isDeveloperPaused",
                        Arrays.asList(new Address(developerAddress))
                );
        if(Integer.decode(ethCall.getResult()) == 1){
            return true;
        }
        return false;
    }
}
