package com.chainverse.sdk.model.NFT;

import java.math.BigInteger;

public class Listing extends BaseInfo {
    private double price;

    public Listing(boolean isEnded, String nft, String owner, String currency, BigInteger tokenId, int fee, BigInteger id, double price) {
        super(isEnded, nft, owner, currency, tokenId, fee, id);
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}