package com.chainverse.sdk.model.MarketItem;

import com.chainverse.sdk.model.NFT.Auction;
import com.chainverse.sdk.model.NFT.Listing;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;

public class ChainverseItemMarket implements Serializable {
    private static final long serialVersionUID = 1L;
    private String attributes;
    private BigInteger token_id;
    private String name;
    private Double price;
    private String nft;
    private BigInteger listing_id;
    private boolean auction;
    private String image_preview;
    private String image;
    private Type type;
    private Currency currency;
    private ArrayList<Categories> categories;
    private Auction auctionInfo;
    private Listing listingInfo;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public BigInteger getTokenId() {
        return token_id;
    }

    public void setTokenId(BigInteger token_id) {
        this.token_id = token_id;
    }

    public BigInteger getListingId() {
        return listing_id;
    }

    public void setListingId(BigInteger listing_id) {
        this.listing_id = listing_id;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getNft() {
        return nft;
    }

    public void setNft(String nft) {
        this.nft = nft;
    }

    public Boolean isAuction() {
        return auction;
    }

    public void setAuction(boolean auction) {
        this.auction = auction;
    }

    public String getImage_preview() {
        return image_preview;
    }

    public void setImage_preview(String image_preview) {
        this.image_preview = image_preview;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public ArrayList<Categories> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Categories> categories) {
        this.categories = categories;
    }

    public Auction getAuctionInfo() {
        return auctionInfo;
    }

    public void setAuctionInfo(Auction auctionInfo) {
        this.auctionInfo = auctionInfo;
    }

    public Listing getListingInfo() {
        return listingInfo;
    }

    public void setListingInfo(Listing listingInfo) {
        this.listingInfo = listingInfo;
    }
}
