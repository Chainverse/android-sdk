package com.chainverse.sdk.network.RESTful;

import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.common.EncryptPreferenceUser;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.network.RESTful.raw.TestRaw;
import com.chainverse.sdk.network.RPC.RPCClient;
import com.chainverse.sdk.network.RPC.RPCURL;
import com.chainverse.sdk.network.RPC.raw.RPCParams;
import com.chainverse.sdk.network.RPC.raw.RPCRequestRaw;
import com.google.gson.JsonElement;

import java.util.ArrayList;

import io.reactivex.Observable;

public class RESTfulClient {
    public static String TAG = RPCClient.class.getSimpleName();
    public static Observable<JsonElement> getItems(String xUserAddress, String gameAddress){
        return RESTfulURL.getInstance().getItems(xUserAddress,gameAddress);
    }

    public static Observable<JsonElement> testBuy(){
        TestRaw raw = new TestRaw();
        raw.setGame_contract(ChainverseSDK.gameAddress);
        raw.setPlayer_address(EncryptPreferenceUser.getInstance().getXUserAddress());
        raw.setCategory_id("1");
        raw.setType("2");

        return RESTfulURL.getInstanceTest().testBuy(raw);
    }
}
