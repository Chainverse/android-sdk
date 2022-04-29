package com.chainverse.sdk.listener;

public interface Action {
    interface publishNFT {
        void onSuccess();

        void onError(String message);
    }

    interface eventMoveService {
        void onSuccess(String tx);

        void onError(String message);
    }

    interface Callback {
        void onCallback();
    }
}
