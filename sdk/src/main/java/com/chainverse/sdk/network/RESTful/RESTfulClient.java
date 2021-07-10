package com.chainverse.sdk.network.RESTful;

import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.network.RPC.RPCClient;
import com.chainverse.sdk.network.RPC.RPCURL;
import com.chainverse.sdk.network.RPC.raw.RPCParams;
import com.chainverse.sdk.network.RPC.raw.RPCRequestRaw;
import com.google.gson.JsonElement;

import java.util.ArrayList;

import io.reactivex.Observable;

public class RESTfulClient {
    public static String TAG = RPCClient.class.getSimpleName();
    public static Observable<JsonElement> request(ArrayList<RPCParams> params, String method){
        RPCRequestRaw request = new RPCRequestRaw();
        request.setJsonrpc("2.0");
        request.setMethod(method);
        //request.setParams(params);
        request.setId(1);
        LogUtil.log(TAG,request);
        return RESTfulURL.getInstance().connect(request);
    }
}
