package com.chainverse.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SignerData implements Parcelable {
    private String message;
    private String chainId;
    private String gasPrice;
    private String gasLimit;
    private String toAddress;
    private String amount;

    public SignerData(){

    }

    protected SignerData(Parcel in) {
        message = in.readString();
        chainId = in.readString();
        gasPrice = in.readString();
        gasLimit = in.readString();
        toAddress = in.readString();
        amount = in.readString();
    }

    public static final Creator<SignerData> CREATOR = new Creator<SignerData>() {
        @Override
        public SignerData createFromParcel(Parcel in) {
            return new SignerData(in);
        }

        @Override
        public SignerData[] newArray(int size) {
            return new SignerData[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(String gasLimit) {
        this.gasLimit = gasLimit;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(chainId);
        dest.writeString(gasPrice);
        dest.writeString(gasLimit);
        dest.writeString(toAddress);
        dest.writeString(amount);
    }
}
