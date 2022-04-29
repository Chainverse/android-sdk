package com.chainverse.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.chainverse.sdk.common.Constants;

import org.web3j.crypto.RawTransaction;

import java.math.BigInteger;


public class TransactionData implements Parcelable {
    private Long nonce;
    private Long gasPrice;
    private Long gasLimit;
    private Long value;
    private String data;
    private String to;
    private String from;
    private String message;
    private Constants.EFunction type;
    private String asset;
    private String receiver;
    private Integer decimals;
    private Double price;
    private String symbol;

    public TransactionData() {
    }

    public TransactionData(long nonce, long gasPrice, long gasLimit, long value, String data, String to, String from, Constants.EFunction type, String asset) {
        this.nonce = nonce;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.value = value;
        this.data = data;
        this.to = to;
        this.from = from;
        this.type = type;
        this.asset = asset;
    }

    protected TransactionData(Parcel in) {
        if (in.readByte() == 0) {
            nonce = null;
        } else {
            nonce = in.readLong();
        }
        if (in.readByte() == 0) {
            gasPrice = null;
        } else {
            gasPrice = in.readLong();
        }
        if (in.readByte() == 0) {
            gasLimit = null;
        } else {
            gasLimit = in.readLong();
        }
        if (in.readByte() == 0) {
            value = null;
        } else {
            value = in.readLong();
        }
        data = in.readString();
        to = in.readString();
        from = in.readString();
        message = in.readString();
        asset = in.readString();
        receiver = in.readString();
        type = Constants.EFunction.valueOf(in.readString());
        if (in.readByte() == 0) {
            decimals = null;
        } else {
            decimals = in.readInt();
        }
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        symbol = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (nonce == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(nonce);
        }
        if (gasPrice == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(gasPrice);
        }
        if (gasLimit == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(gasLimit);
        }
        if (value == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(value);
        }
        dest.writeString(data);
        dest.writeString(to);
        dest.writeString(from);
        dest.writeString(message);
        dest.writeString(asset);
        dest.writeString(receiver);
        dest.writeString(type.toString());
        if (decimals == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(decimals);
        }
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(price);
        }
        dest.writeString(symbol);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransactionData> CREATOR = new Creator<TransactionData>() {
        @Override
        public TransactionData createFromParcel(Parcel in) {
            return new TransactionData(in);
        }

        @Override
        public TransactionData[] newArray(int size) {
            return new TransactionData[size];
        }
    };

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
    }

    public Long getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(Long gasPrice) {
        this.gasPrice = gasPrice;
    }

    public Long getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(Long gasLimit) {
        this.gasLimit = gasLimit;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Constants.EFunction getType() {
        return type;
    }

    public void setType(Constants.EFunction type) {
        this.type = type;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
