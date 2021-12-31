package com.chainverse.sdk.model.NFT;

public class NFT {
    private Auction auction;
    private Listing listing;

    public NFT(Auction auction, Listing listing) {
        this.auction = auction;
        this.listing = listing;
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
