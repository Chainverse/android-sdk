package com.chainverse.sdk.listener;

public interface Action {
    interface publishNFT {
        void onSuccess();
        void onError(String message);
    }
}
