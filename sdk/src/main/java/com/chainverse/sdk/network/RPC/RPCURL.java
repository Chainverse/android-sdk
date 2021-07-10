package com.chainverse.sdk.network.RPC;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RPCURL {
    public static RPCEndpoint getInstance(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://data-seed-prebsc-1-s1.binance.org:8545")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(RPCEndpoint.class);
    }
}
