package com.chainverse.sdk.base.web3;

import android.content.Context;

import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.common.EncryptPreferenceUser;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.http.HttpService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BaseWeb3 {
    private static BaseWeb3 instance;
    private Web3j web3;
    public static BaseWeb3 getInstance(){
        if(instance == null){
            synchronized (EncryptPreferenceUser.class){
                if(instance == null){
                    instance = new BaseWeb3();
                }
            }
        }
        return instance;
    }


    public BaseWeb3 init(Context context){
        web3 = Web3j.build(new HttpService("https://data-seed-prebsc-1-s1.binance.org:8545"));
        return instance;
    }

    public EthCall contract(String contractAddress, String method, List<Type> inputParameters){
        EthCall result = null;
        try {
            Function function = new Function(method, inputParameters, Collections.emptyList());
            String encodedFunction = FunctionEncoder.encode(function);
            LogUtil.log("nampv_web3",encodedFunction);

            result = web3.ethCall(Transaction.createEthCallTransaction(
                    null,
                    contractAddress,
                    encodedFunction),
                    DefaultBlockParameter.valueOf("latest")).sendAsync().get();
        }catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return result;

    }

}
