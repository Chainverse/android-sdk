package com.chainverse.sdk.network.RESTful;

import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.network.RESTful.raw.TestRaw;
import com.chainverse.sdk.network.RPC.RPCClient;
import com.google.gson.JsonElement;

import io.reactivex.Observable;

public class RESTfulClient {
    public static String TAG = RPCClient.class.getSimpleName();

    public static Observable<JsonElement> getItems(String xUserAddress, String gameAddress) {
        return RESTfulURL.getInstance().getItems(xUserAddress, gameAddress);
    }

    public static Observable<JsonElement> getItemOnMarket(String gameAddress, int page, int pageSize, String name) {
        return RESTfulURL.getInstanceMarket().getItemOnMarket(gameAddress, page, pageSize, name);
    }

    public static Observable<JsonElement> getMyAsset() {
        return RESTfulURL.getInstanceMarket().getMyAsset();
    }

    public static Observable<JsonElement> getNonce() {
        return RESTfulURL.getInstanceMarket().getNonce();
    }

    public static Observable<JsonElement> testBuy() {
        TestRaw raw = new TestRaw();
        raw.setGame_contract(ChainverseSDK.gameAddress);
        raw.setPlayer_address(EncryptPreferenceUtils.getInstance().getXUserAddress());
        raw.setCategory_id("1");
        raw.setType("2");

        return RESTfulURL.getInstanceTest().testBuy(raw);
    }
}
