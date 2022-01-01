package com.chainverse.sdk.model.MarketItem;

import java.io.Serializable;

public class Type implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
