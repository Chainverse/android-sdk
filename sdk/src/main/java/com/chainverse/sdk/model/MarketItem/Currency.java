package com.chainverse.sdk.model.MarketItem;

import java.io.Serializable;

public class Currency implements Serializable {
    private static final long serialVersionUID = 1L;
    private String currency;
    private String symbol;
    private int decimal;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }
}
