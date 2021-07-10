package com.chainverse.sdk.network.RPC;

import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.network.RPC.raw.RPCParams;
import com.chainverse.sdk.network.RPC.raw.RPCRequestRaw;
import com.google.gson.JsonElement;

import java.util.ArrayList;

import io.reactivex.Observable;

public class RPCClient {
    public static String TAG = RPCClient.class.getSimpleName();
    public static Observable<JsonElement> request(ArrayList<Object> params, String method){
        RPCRequestRaw request = new RPCRequestRaw();
        request.setJsonrpc("2.0");
        request.setMethod(method);
        request.setParams(params);
        request.setId(1);
        LogUtil.log(TAG,request);
        return RPCURL.getInstance().connect(request);
    }

    public static ArrayList<Object> createParams(RPCParams param){
        ArrayList<Object> params = new ArrayList<>();
        if(param != null){
            params.add(param);
        }
        params.add("latest");
        return params;
    }
}
