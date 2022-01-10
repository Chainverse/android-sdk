package com.chainverse.sdk.model.service;

import java.io.Serializable;

public class Network implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String network;
    private String chain_id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getChainId() {
        return chain_id;
    }

    public void setChainId(String chain_id) {
        this.chain_id = chain_id;
    }
}
