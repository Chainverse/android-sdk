package com.chainverse.sdk.rpc.raw;

public class RPCParams {
    private String developerAddress;
    private String gameAddress;
    private String chainverseFactory;

    public String getDeveloperAddress() {
        return developerAddress;
    }

    public void setDeveloperAddress(String developerAddress) {
        this.developerAddress = developerAddress;
    }

    public String getGameAddress() {
        return gameAddress;
    }

    public void setGameAddress(String gameAddress) {
        this.gameAddress = gameAddress;
    }

    public String getChainverseFactory() {
        return chainverseFactory;
    }

    public void setChainverseFactory(String chainverseFactory) {
        this.chainverseFactory = chainverseFactory;
    }
}
