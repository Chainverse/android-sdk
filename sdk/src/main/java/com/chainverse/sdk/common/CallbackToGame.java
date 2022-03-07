package com.chainverse.sdk.common;


import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.ChainverseItem;
import com.chainverse.sdk.model.NFT.NFT;

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

    public static void onGetMyAssets(ArrayList<NFT> items) {
        if (ChainverseSDK.mCallback != null) {
            ChainverseSDK.mCallback.onGetMyAssets(items);
        }
    }

    public static void onItemUpdate(ChainverseItem item, int type) {
        if (ChainverseSDK.mCallback != null) {
            ChainverseSDK.mCallback.onItemUpdate(item, type);
        }
    }

    public static void onGetListItemMarket(ArrayList<NFT> items, int count) {
        if (ChainverseSDK.mCallback != null) {
            ChainverseSDK.mCallback.onGetListItemMarket(items, count);
        }
    }

    public static void onGetDetailItem(NFT nft) {
        if (ChainverseSDK.mCallback != null) {
            ChainverseSDK.mCallback.onGetDetailItem(nft);
        }
    }

    public static void onTransact(Constants.EFunction function, String tx) {
        if (ChainverseSDK.mCallback != null) {
            ChainverseSDK.mCallback.onTransact(function, tx);
        }
    }

    public static void onSignMessage(String signedMessage) {
        if (ChainverseSDK.mCallback != null) {
            ChainverseSDK.mCallback.onSignMessage(signedMessage);
        }
    }

    public static void onSignTransaction(Constants.EFunction function, String signed) {
        if (ChainverseSDK.mCallback != null) {
            ChainverseSDK.mCallback.onSignTransaction(function, signed);
        }
    }
}
