package com.chainverse.sdk.model.NFT;

import com.chainverse.sdk.model.MarketItem.Categories;
import com.chainverse.sdk.model.service.Network;

import java.math.BigInteger;
import java.util.ArrayList;

public class NFT {
    private BigInteger token_id;
    private String name;
    private String nft;
    private String owner;
    private String attributes;
    private String image;
    private String image_preview;
    private InfoSell infoSell;
    private Type type;
    private ArrayList<Categories> categories;
    private Network network;
    private Auction auction;
    private Listing listing;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getImagePreview() {
        return image_preview;
    }

    public void setImagePreview(String image_preview) {
        this.image_preview = image_preview;
    }

    public InfoSell getInfoSell() {
        return infoSell;
    }

    public void setInfoSell(InfoSell infoSell) {
        this.infoSell = infoSell;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ArrayList<Categories> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Categories> categories) {
        this.categories = categories;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }
}
