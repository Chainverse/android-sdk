package com.chainverse.sdk.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.base.web3.BaseWeb3;
import com.chainverse.sdk.common.Constants;


import org.web3j.abi.datatypes.Address;
import org.web3j.protocol.core.methods.response.EthCall;

import java.util.ArrayList;
import java.util.Arrays;


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
