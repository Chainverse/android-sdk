package com.chainverse.sdk;

import com.chainverse.sdk.exception.SDKException;

public class ChainverseExeption {
    public static void developerAddressExeption(){
        if(ChainverseSDK.developerAddress.isEmpty()){
            throw new SDKException(SDKException.EXCEPTION_TYPE.ChainverseSDKDeveloperAddressException.toString());
        }
    }

    public static void gameAddressExeption(){
        if(ChainverseSDK.gameAddress.isEmpty()){
            throw new SDKException(SDKException.EXCEPTION_TYPE.ChainverseSDKGameAddressException.toString());
        }
    }

}
