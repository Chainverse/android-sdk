package com.chainverse.sdk;

public interface ChainverseCallback {
    void onInitSuccess();
    void onItemUpdate();
    void onConnectedWallet(String address);
}
