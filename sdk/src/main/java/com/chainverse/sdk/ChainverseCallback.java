package com.chainverse.sdk;

import java.util.ArrayList;

public interface ChainverseCallback {
    void onInitSDKSuccess();
    void onError(int error);
    void onItemUpdate(ChainverseItem item, int type);
    void onGetItems(ArrayList<ChainverseItem> items);
    void onConnectSuccess(String address);
    void onLogout(String address);
}
