package com.chainverse.sdk.model.NFT;

import java.math.BigInteger;

public class Auction extends BaseInfo {
    private String winner;
    private BigInteger bid;
    private BigInteger duration;
    private BigInteger end;

    public Auction(boolean isEnded, String nft, String owner, String currency, BigInteger tokenId, int fee, BigInteger id, String winner, BigInteger bid, BigInteger duration, BigInteger end) {
        super(isEnded, nft, owner, currency, tokenId, fee, id);
        this.winner = winner;
        this.bid = bid;
        this.duration = duration;
        this.end = end;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public BigInteger getBid() {
        return bid;
    }

    public void setBid(BigInteger bid) {
        this.bid = bid;
    }

    public BigInteger getDuration() {
        return duration;
    }

    public void setDuration(BigInteger duration) {
        this.duration = duration;
    }

    public BigInteger getEnd() {
        return end;
    }

    public void setEnd(BigInteger end) {
        this.end = end;
    }
}
