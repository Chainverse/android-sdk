package com.chainverse.sdk.exception;

public class SDKException extends RuntimeException {
    public enum EXCEPTION_TYPE{
        ChainverseSDKDeveloperAddressException,
        ChainverseSDKGameAddressException
    }
    public SDKException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public SDKException(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
    }

    public SDKException(Throwable cause) {
        super(cause);
    }
}
