package com.chainverse.sdk;

import com.chainverse.sdk.model.MarketItem.ChainverseItemMarket;

import java.util.ArrayList;

public interface ChainverseCallback {
    void onInitSDKSuccess();

    void onError(int error);

    void onItemUpdate(ChainverseItem item, int type);

    void onGetItems(ArrayList<ChainverseItem> items);

    void onGetItemMarket(ArrayList<ChainverseItemMarket> items);

    void onGetMyAssets(ArrayList<ChainverseItemMarket> items);

    void onConnectSuccess(String address);

    void onLogout(String address);

    void onSignMessage(String signed);

    void onSignTransaction(String signed);

    void onBuy(String tx);
}
