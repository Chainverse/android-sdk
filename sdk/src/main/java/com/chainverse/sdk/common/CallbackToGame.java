package com.chainverse.sdk.common;


import com.chainverse.sdk.model.MarketItem.ChainverseItemMarket;
import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.ChainverseItem;

import java.util.ArrayList;

public class CallbackToGame {
    public static void onConnectSuccess(String address) {
        if (!address.isEmpty()) {
            if (ChainverseSDK.mCallback != null) {
                ChainverseSDK.mCallback.onConnectSuccess(address);
            }
        }
    }

    public static void onInitSDKSuccess() {
        if (ChainverseSDK.mCallback != null) {
            ChainverseSDK.mCallback.onInitSDKSuccess();
        }
    }

    public static void onLogout(String address) {
        if (!address.isEmpty()) {
            if (ChainverseSDK.mCallback != null) {
                ChainverseSDK.mCallback.onLogout(address);
            }
        }
    }

    public static void onError(int errorCode) {
        if (ChainverseSDK.mCallback != null) {
            ChainverseSDK.mCallback.onError(errorCode);
        }
    }

    public static void onGetItems(ArrayList<ChainverseItem> items) {
        if (ChainverseSDK.mCallback != null) {
            ChainverseSDK.mCallback.onGetItems(items);
        }
    }

    public static void onGetItemMarket(ArrayList<ChainverseItemMarket> items) {
        if (ChainverseSDK.mCallback != null) {
            ChainverseSDK.mCallback.onGetItemMarket(items);
        }
    }

    public static void onGetMyAssets(ArrayList<ChainverseItemMarket> items) {
        if (ChainverseSDK.mCallback != null) {
            ChainverseSDK.mCallback.onGetMyAssets(items);
        }
    }

    public static void onItemUpdate(ChainverseItem item, int type) {
        if (ChainverseSDK.mCallback != null) {
            ChainverseSDK.mCallback.onItemUpdate(item, type);
        }
    }
}
