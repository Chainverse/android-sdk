package com.chainverse.sdk.model.NFT;

import java.io.Serializable;
import java.math.BigInteger;

public class BaseInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean isEnded;
    private String nft;
    private String owner;
    private String currency;
    private BigInteger tokenId;
    private int fee;
    private BigInteger id;

    public BaseInfo(boolean isEnded, String nft, String owner, String currency, BigInteger tokenId, int fee, BigInteger id) {
        this.isEnded = isEnded;
        this.nft = nft;
        this.owner = owner;
        this.currency = currency;
        this.tokenId = tokenId;
        this.fee = fee;
        this.id = id;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public void setEnded(boolean ended) {
        isEnded = ended;
    }

    public String getNft() {
        return nft;
    }

    public void setNft(String nft) {
        this.nft = nft;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigInteger getTokenId() {
        return tokenId;
    }

    public void setTokenId(BigInteger tokenId) {
        this.tokenId = tokenId;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}