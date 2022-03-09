package com.chainverse.sdk;

import android.app.Activity;
import android.content.Intent;

import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.EthCall;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public interface Chainverse {
    /**
     * init: init ChainverseSDK
     *
     * @param activity
     * @param callback
     */
    void init(String developerAddress, String gameAddress, Activity activity, ChainverseCallback callback);

    /**
     * setKeepConnect: Keep connect wallet
     *
     * @param keep: true(keep connect wallet) | false (reconnect wallet)
     */
    void setKeepConnect(boolean keep);

    /**
     * setScheme: setup connect wallet
     *
     * @param scheme
     */
    void setScheme(String scheme);

    /**
     * setCallbackHost: setup connect wallet
     *
     * @param host
     */
    void setHost(String host);

    /**
     * return list support wallet
     *
     * @return
     */
    void getItems();

    /**
     * return version
     *
     * @return
     */
    String getVersion();

    /**
     * handle result intent
     *
     * @param intent
     */
    void onNewIntent(Intent intent);

    /**
     * showConnectView: Show screen choose wallet
     */
    void showConnectView();

    /**
     * connectWithTrust: Connect with Trust Wallet
     */
    void connectWithTrust();

    /**
     * connectWithChainverse: Connect with Chainverse
     */
    void connectWithChainverse() throws Exception;

    /**
     * logout: Logout
     */
    void logout();

    /**
     * isUserConnected: return status connected or no connected
     *
     * @return
     */
    Boolean isUserConnected();

    /**
     * getUserInfo: Return user info
     *
     * @return
     */
    ChainverseUser getUser();

    /**
     * getBalance
     */
    BigDecimal getBalance();

    /**
     * getBalanceToken
     *
     * @param contractAddress
     */
    BigDecimal getBalanceToken(String contractAddress);

    /**
     * callFunction
     */
    EthCall callFunction(String address, String method, List<Type> inputParameters);

    /**
     * signMessage
     *
     * @param data
     */
    void signMessage(String data);

    /**
     * signTransaction
     *
     * @param chainId
     * @param gasPrice
     * @param gasLimit
     * @param toAddress
     * @param amount
     */
    void signTransaction(String chainId, String gasPrice, String gasLimit, String toAddress, String amount);

    /**
     * @param to
     * @param amount
     * @return
     */
    String transfer(String to, BigDecimal amount);

    /**
     * showWalletView
     */
    void showConnectWalletView();

    String genMnemonic(int strength);

    boolean isValidMnemonic(String phrase);

    boolean isValidAddress(String address);

    String importWalletByMnemonic(String phrase) throws Exception;

    String importWalletByPrivateKey(String privateKey) throws Exception;

    void showWalletInfoView();

    void testBuy();

//    /**
//     * Buy NFT
//     * @param currency
//     * @param listing_id
//     * @param price
//     * return
//     */
//    void buyNFT(String currency, Long listing_id, Double price, boolean isAuction);
}
