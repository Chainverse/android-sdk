package com.chainverse.sdk.rpc;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseRPCURL {
    public static RPCEndpoint getInstance(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.101.144:8545")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(RPCEndpoint.class);
    }
}
