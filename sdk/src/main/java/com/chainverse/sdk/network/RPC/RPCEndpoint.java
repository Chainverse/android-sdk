package com.chainverse.sdk.network.RPC;

import com.chainverse.sdk.network.RPC.raw.RPCRequestRaw;
import com.google.gson.JsonElement;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RPCEndpoint {
    @POST("/")
    Observable<JsonElement> connect(@Body RPCRequestRaw body);
}
