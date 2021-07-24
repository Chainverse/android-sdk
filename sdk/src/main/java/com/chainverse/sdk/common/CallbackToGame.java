package com.chainverse.sdk.common;



import com.chainverse.sdk.ChainverseSDK;

public class CallbackToGame {
    public static void onUserAddress(String address) {
        if(!address.isEmpty()){
            if(ChainverseSDK.mCallback != null){
                ChainverseSDK.mCallback.onUserAddress(address);
            }
        }
    }

    public static void onInitSDK(boolean isCheck){
        if(ChainverseSDK.mCallback != null){
            ChainverseSDK.mCallback.onInitSDK(isCheck);
        }
    }

    public static void onUserLogout(String address) {
        if(!address.isEmpty()){
            if(ChainverseSDK.mCallback != null){
                ChainverseSDK.mCallback.onUserLogout(address);
            }
        }
    }
}
