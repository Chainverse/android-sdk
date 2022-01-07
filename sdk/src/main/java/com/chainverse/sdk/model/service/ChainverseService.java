package com.chainverse.sdk.model.service;

import java.io.Serializable;
import java.util.ArrayList;

public class ChainverseService implements Serializable {
    private String name;
    private String address;
    private Network network_info;
    private ArrayList<Service> services;
    private ArrayList<Item> items;
    private ArrayList<Token> tokens;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Network getNetworkInfo() {
        return network_info;
    }

    public void setNetworkInfo(Network network_info) {
        this.network_info = network_info;
    }

    public ArrayList<Service> getServices() {
        return services;
    }

    public void setServices(ArrayList<Service> services) {
        this.services = services;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public void setTokens(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }
}
