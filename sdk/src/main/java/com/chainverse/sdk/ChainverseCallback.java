package com.chainverse.sdk;

import com.chainverse.sdk.model.Item;

public interface ChainverseCallback {
    void onInitSDK(boolean status);
    void onError(int error);
    void onItemUpdate(Item item);
    void onUserAddress(String address);
    void onUserLogout(String address);
}
