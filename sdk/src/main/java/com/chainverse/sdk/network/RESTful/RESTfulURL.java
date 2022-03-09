package com.chainverse.sdk.network.RESTful;

import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.model.MessageNonce;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RESTfulURL {
    public static RESTfulEndpoint getInstance() {

        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                final Request request = chain.request().newBuilder()
                        .addHeader("X-User-Signature", EncryptPreferenceUtils.getInstance().getXUserSignature())
                        .addHeader("X-Signature-Ethers", "false")
                        .build();

                return chain.proceed(request);

            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL.urlRestful)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(RESTfulEndpoint.class);
    }

    public static RESTfulEndpoint getInstanceMarket() {

        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                MessageNonce messageNonce = EncryptPreferenceUtils.getInstance().getXUserMessageNonce();

                String nonce = messageNonce != null && messageNonce.getNonce() != null ? String.valueOf(messageNonce.getNonce()) : "";
                String time = messageNonce != null && messageNonce.getTime() != null ? String.valueOf(messageNonce.getTime()) : "";
                String signature = messageNonce != null && messageNonce.getMessage() != null ? String.valueOf(messageNonce.getMessage()) : "";
                final Request request = chain.request().newBuilder()
                        .addHeader("x-address", WalletUtils.getInstance().getAddress())
                        .addHeader("x-signature", signature)
                        .addHeader("x-nonce", nonce)
                        .addHeader("x-time", time)
                        .build();

                return chain.proceed(request);

            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL.urlResfulMarket)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(RESTfulEndpoint.class);
    }

    public static RESTfulEndpoint getInstanceTest() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                final Request request = chain.request().newBuilder()
                        .addHeader("X-User-Signature", EncryptPreferenceUtils.getInstance().getXUserSignature())
                        .build();

                return chain.proceed(request);

            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL.urlBuyTest)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(RESTfulEndpoint.class);
    }
}
