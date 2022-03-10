package com.chainverse.sdk.manager;

import android.content.Context;

import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.model.service.ChainverseService;
import com.chainverse.sdk.model.service.Network;
import com.chainverse.sdk.model.service.Service;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.http.HttpService;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ServiceManager {
    private static ServiceManager instance;
    private ChainverseService chainverseService;
    private EncryptPreferenceUtils encryptPreferenceUtils;
    private String address;
    private String rpc;


    public static ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }

    public ServiceManager init(Context mContext, String address) {
        encryptPreferenceUtils = EncryptPreferenceUtils.getInstance().init(mContext);
        chainverseService = encryptPreferenceUtils.getService();
        this.address = address;
        return instance;
    }

    public ServiceManager init(Context mContext) {
        encryptPreferenceUtils = EncryptPreferenceUtils.getInstance().init(mContext);
        chainverseService = encryptPreferenceUtils.getService();
        return instance;
    }

    public Service getService() {
        Service service = null;
        if (chainverseService != null) {
            for (int i = 0; i < chainverseService.getServices().size(); i++) {
                if (address.toLowerCase().equals(chainverseService.getServices().get(i).getAddress().toLowerCase())) {
                    service = chainverseService.getServices().get(i);
                }
            }
        }
        return service;
    }

    public Network getNetworkInfo() {
        Network network = null;
        if (chainverseService != null) {
            network = chainverseService.getNetworkInfo();
        }
        return network;
    }

    public String getRPC() {
        return this.rpc;
    }

    public void setRPC(String rpc) {
        this.rpc = rpc;
    }

    public void checkRPC() {
        Gson gson = new Gson();
        ArrayList<String> rpcs = gson.fromJson(chainverseService.getNetworkInfo().getRpcs(), new TypeToken<ArrayList<String>>() {
        }.getType());
        this.rpc = rpcs.get(0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                BigInteger block = BigInteger.ZERO;
                for (int i = 0; i < rpcs.size(); i++) {
                    Web3j web3 = Web3j.build(new HttpService(rpcs.get(i)));
                    EthBlock blockNumber = null;
                    try {
                        blockNumber = web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).sendAsync().get();
                        BigInteger newBlock = blockNumber.getBlock().getNumber();
                        if (newBlock.compareTo(block) == 1) {
                            block = newBlock;
                            ServiceManager.getInstance().setRPC(rpcs.get(i));
                        }
                    } catch (ExecutionException e) {
                        continue;
                    } catch (InterruptedException e) {
                        continue;
                    }
                }
            }
        }).start();
    }
}
