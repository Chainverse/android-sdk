package com.chainverse.sdk;

public interface ChainverseCallback {
    void onItemUpdate();
    void onConnectedWallet(String address);
}
