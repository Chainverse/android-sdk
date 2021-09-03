package com.chainverse.sdk;

public class ChainverseItem {
    public static final int TRANSFER_ITEM_TO_USER = 1;
    public static final int TRANSFER_ITEM_FROM_USER = 2;

    private int item_id;
    private int category_id;
    private String game_address;
    private String attributes;

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getGame_address() {
        return game_address;
    }

    public void setGame_address(String game_address) {
        this.game_address = game_address;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }
}
