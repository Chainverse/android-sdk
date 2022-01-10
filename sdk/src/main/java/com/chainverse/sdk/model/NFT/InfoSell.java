package com.chainverse.sdk.model.NFT;

import com.chainverse.sdk.model.MarketItem.Currency;

import java.math.BigInteger;

public class InfoSell {
    private BigInteger listing_id;
    private Double price;
    private Boolean is_auction;
    private Currency currency_info;

    public BigInteger getListingId() {
        return listing_id;
    }

    public void setListingId(BigInteger listing_id) {
        this.listing_id = listing_id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean isAuction() {
        return is_auction;
    }

    public void setIsAuction(Boolean is_auction) {
        this.is_auction = is_auction;
    }

    public Currency getCurrencyInfo() {
        return currency_info;
    }

    public void setCurrencyInfo(Currency currency_info) {
        this.currency_info = currency_info;
    }
}
