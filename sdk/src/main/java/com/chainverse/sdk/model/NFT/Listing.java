package com.chainverse.sdk.model.NFT;

import java.math.BigInteger;

public class Listing extends BaseInfo {
    private BigInteger price;

    public Listing(boolean isEnded, String nft, String owner, String currency, BigInteger tokenId, int fee, BigInteger id, BigInteger price) {
        super(isEnded, nft, owner, currency, tokenId, fee, id);
        this.price = price;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }
}