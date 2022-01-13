package com.chainverse.sdk.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.chainverse.sdk.ChainverseError;
import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.base.web3.BaseWeb3;
import com.chainverse.sdk.blockchain.ERC20;
import com.chainverse.sdk.blockchain.ERC721;
import com.chainverse.sdk.blockchain.HandleContract;
import com.chainverse.sdk.common.CallbackToGame;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.Convert;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.model.NFT.Auction;
import com.chainverse.sdk.model.NFT.InfoSell;
import com.chainverse.sdk.model.NFT.Listing;
import com.chainverse.sdk.model.NFT.NFT;
import com.chainverse.sdk.model.service.Service;


import org.bouncycastle.util.encoders.Hex;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rx.functions.Func0;


public class ContractManager {
    public interface Listener {
        void isChecked(boolean isCheck);
    }

    private Listener listener;
    private Context mContext;

    Web3j web3 = Web3j.build(new HttpService(Constants.URL.urlBlockchain));

    public ContractManager(Context context, Listener listener) {
        this.listener = listener;
        mContext = context;
    }

    public ContractManager(Context context) {
        mContext = context;
    }

    public BigDecimal balanceOf(String contractAddress, String owner) throws Exception {
        BigDecimal balance = new BigDecimal(0);
        try {
            EthCall ethCall = BaseWeb3.getInstance().init(mContext).callFunction(contractAddress, "balanceOf", Arrays.asList(new Address(owner)));

            if (ethCall != null && ethCall.getResult() != null) {
                BigDecimal value = new BigDecimal(new BigInteger(ethCall.getResult().replace("0x", ""), 16));
                balance = org.web3j.utils.Convert.fromWei(value, org.web3j.utils.Convert.Unit.ETHER);
            }
            return balance;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public int decimals(String currency) {
        int decimals = 18;
        if (currency.toLowerCase().equals(Constants.TOKEN_SUPPORTED.NativeCurrency.toLowerCase())) {
            return decimals;
        }
        try {
            Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair());
            ERC20 erc20 = ERC20.load(currency, web3, dummyCredentials, new DefaultGasProvider());

            RemoteCall<BigInteger> result = erc20.decimals();

            decimals = result.sendAsync().get().intValue();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return decimals;
    }

    private Observable<Boolean> checkContractObservable() {
        return Observable.defer(new Func0<ObservableSource<? extends Boolean>>() {

            @Override
            public ObservableSource<? extends Boolean> call() {
                return Observable.just(checkContract());
            }
        });
    }

    private Boolean checkContract() {
        if (!isGameContract()) {
            CallbackToGame.onError(ChainverseError.ERROR_GAME_ADDRESS);
        }

        if (!isDeveloperContract()) {
            CallbackToGame.onError(ChainverseError.ERROR_DEVELOPER_ADDRESS);
        }

        if (isGamePaused()) {
            CallbackToGame.onError(ChainverseError.ERROR_GAME_PAUSE);
        }

        if (isDeveloperPaused()) {
            CallbackToGame.onError(ChainverseError.ERROR_DEVELOPER_PAUSE);
        }

        if (isGameContract() && isDeveloperContract() && !isGamePaused() && !isDeveloperPaused()) {
            return true;
        }
        return false;
    }


    public void check() {
        checkContractObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isChecked -> {
                    listener.isChecked(isChecked);
                });
    }

    /**
     * Get Information NFT on chain
     *
     * @param nft
     * @param tokenId
     * @return
     * @throws Exception
     */
    public NFT getNFT(String nft, BigInteger tokenId) throws Exception {
        NFT item = new NFT();
        Service service = new ServiceManager(mContext).getService(Constants.CONTRACT.MarketService);

        if (service != null) {
            try {
                Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair());
                HandleContract handleContract = HandleContract.load(Constants.CONTRACT.MarketService, service.getAbi(), web3, dummyCredentials, new DefaultGasProvider());
                ERC721 contractERC721 = ERC721.load(nft, web3, dummyCredentials, new DefaultGasProvider());
                RemoteCall<String> remoteCallUri = contractERC721.tokenURI(tokenId);
                String uri = remoteCallUri.sendAsync().get();
                String ownerOf = contractERC721.ownerOf(tokenId).sendAsync().get();

                String content = handleTokenUri(uri);

                item.setTokenId(tokenId);
                item.setOwnerOnChain(ownerOf);
                if (content != null && !content.isEmpty()) {
                    JSONObject json = new JSONObject(content);

                    String image = "";
                    String asset = "";

                    if (json.has("image")) {
                        image = (String) json.get("image");
                    }
                    if (json.has("asset")) {
                        asset = (String) json.get("asset");
                    }

                    item.setImage(image);
                    item.setImagePreview((!asset.isEmpty()) ? asset : image);
                    item.setName((String) json.get("name"));
                    item.setAttributes(content);
                }

                RemoteFunctionCall<Tuple2<HandleContract.Auction, HandleContract.Listing>> data = handleContract.callFunc(
                        "getByNFT",
                        Arrays.asList(nft, tokenId),
                        Arrays.asList(new TypeReference<HandleContract.Auction>() {
                        }, new TypeReference<HandleContract.Listing>() {
                        }));

                HandleContract.Auction auctionInfo = data.sendAsync().get().component1();
                HandleContract.Listing listingInfo = data.sendAsync().get().component2();

                if (ownerOf.toLowerCase().equals(ChainverseSDK.gameAddress.toLowerCase())) {
                    String owner = ownerOfOnGame(nft, tokenId);
                    item.setOwner(owner);
                } else {
                    item.setOwner(ownerOf);
                }

                String owner = ownerOfOnGame("0x7eAdaF22D3a4C10E0bA1aC692654b80954084bdD", new BigInteger("235"));


                int feeAuction = Integer.parseInt(auctionInfo.fee.toString(), 8);
                BigInteger bidDuration = new BigInteger(auctionInfo.bidDuration.toString(), 18);
                BigInteger bidEnd = new BigInteger(auctionInfo.end.toString(), 18);
                Auction auction = new Auction(auctionInfo.isEnded, auctionInfo.nft, auctionInfo.owner, auctionInfo.currency, auctionInfo.tokenId, feeAuction, auctionInfo.id, auctionInfo.winner, auctionInfo.bid, bidDuration, bidEnd);

                int feeListing = Integer.parseInt(listingInfo.fee.toString(), 8);
                Listing listing = new Listing(listingInfo.isEnded, listingInfo.nft, listingInfo.owner, listingInfo.currency, listingInfo.tokenId, feeListing, listingInfo.id, listingInfo.price);

                item.setAuction(auction);
                item.setListing(listing);

                InfoSell infoSell = new InfoSell();
                infoSell.setIsAuction((auction.getId().equals(BigInteger.ZERO)) ? false : true);
                infoSell.setPrice(0.0);
                infoSell.setListingId(BigInteger.ZERO);
                if (!auction.getId().equals(BigInteger.ZERO)) {
                    BigDecimal price = org.web3j.utils.Convert.fromWei(new BigDecimal(auction.getBid()), org.web3j.utils.Convert.Unit.ETHER);
                    infoSell.setListingId(auction.getId());
                    infoSell.setPrice(Double.parseDouble(price.toString()));
                    item.setOwner(auction.getOwner());
                }
                if (!listing.getId().equals(BigInteger.ZERO) && listing.getPrice() != null) {
                    BigDecimal price = org.web3j.utils.Convert.fromWei(new BigDecimal(listing.getPrice()), org.web3j.utils.Convert.Unit.ETHER);
                    infoSell.setListingId(listing.getId());
                    infoSell.setPrice(Double.parseDouble(price.toString()));
                    item.setOwner(listing.getOwner());
                }

                item.setInfoSell(infoSell);

                return item;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            throw new Exception("Service is not supported");
        }

    }

    public BigInteger allowance(String token, String owner, String spender) {
        BigInteger allowance = BigInteger.ZERO;
        Credentials credentials = WalletUtils.getInstance().init(mContext).getCredential();

        ERC20 erc20 = ERC20.load(token, web3, credentials, new DefaultGasProvider());

        RemoteCall<BigInteger> remoteCall = (RemoteCall<BigInteger>) erc20.allowance(owner, spender);
        try {
            allowance = remoteCall.sendAsync().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return allowance;
    }

    public String approved(String token, String spender, double amout) throws Exception {
        String tx;

        try {
            Credentials credentials = WalletUtils.getInstance().init(mContext).getCredential();

            int decimals = decimals(token);
            BigInteger priceFormat = BigDecimal.valueOf(amout * Math.pow(10, decimals)).toBigInteger();

            Function function = new Function("approve",
                    Arrays.asList(new Address(spender), new Uint256(priceFormat)),
                    Collections.emptyList()
            );

            String functionEncoder = FunctionEncoder.encode(function);

            RawTransactionManager rawTransactionManager = new RawTransactionManager(web3, credentials);

            BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
            BigInteger gasLimit = getGasLimit(BigInteger.ZERO, token, functionEncoder);
            BigInteger nonce = getNonce();

            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, token, BigInteger.ZERO, "0x" + functionEncoder);
            String signedTransaction = rawTransactionManager.sign(rawTransaction);

            EthSendTransaction sendRawTransaction = web3.ethSendRawTransaction(signedTransaction).sendAsync().get();

            tx = sendRawTransaction.getTransactionHash();
        } catch (InterruptedException e) {
            throw e;
        } catch (ExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }

        return tx;
    }

    public String buyNFT(String currency, BigInteger listingId, double price) throws Exception {
        String tx;
        Service service = new ServiceManager(mContext).getService(Constants.CONTRACT.MarketService);

        if (service != null) {
            try {
                Credentials credentials = WalletUtils.getInstance().init(mContext).getCredential();

                int decimals = decimals(currency);
                BigInteger priceFormat = BigDecimal.valueOf(price * Math.pow(10, decimals)).toBigInteger();

                Function function = new Function("buy", Arrays.asList(new Uint256(listingId), new Uint256(priceFormat)), Collections.emptyList());

                String functionEncoder = FunctionEncoder.encode(function);

                RawTransactionManager rawTransactionManager = new RawTransactionManager(web3, credentials);

                BigInteger value = BigInteger.ZERO;

                if (currency.toLowerCase().equals(Constants.TOKEN_SUPPORTED.NativeCurrency.toLowerCase())) {
                    value = priceFormat;
                }

                String data = "0x" + functionEncoder;
                BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                BigInteger nonce = getNonce();
                BigInteger gasLimit = getGasLimit(value, Constants.CONTRACT.MarketService, functionEncoder);

                RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, Constants.CONTRACT.MarketService, value, data);
                String signedTransaction = rawTransactionManager.sign(rawTransaction);

                EthSendTransaction sendRawTransaction = web3.ethSendRawTransaction(signedTransaction).sendAsync().get();

                tx = sendRawTransaction.getTransactionHash();
            } catch (InterruptedException e) {
                throw e;
            } catch (ExecutionException e) {
                throw e;
            } catch (Exception e) {
                throw e;
            }
        } else {
            throw new Exception("Service is not supported");
        }

        return tx;
    }

    public String bidNFT(String currency, BigInteger listingId, double price) throws Exception {
        String tx;
        Service service = new ServiceManager(mContext).getService(Constants.CONTRACT.MarketService);
        if (service != null) {
            try {
                Credentials credentials = WalletUtils.getInstance().init(mContext).getCredential();

                int decimals = decimals(currency);
                BigInteger priceFormat = BigDecimal.valueOf(price * Math.pow(10, decimals)).toBigInteger();

                Function function = new Function("bid", Arrays.asList(new Uint256(listingId), new Uint256(priceFormat)), Collections.emptyList());

                String functionEncoder = FunctionEncoder.encode(function);

                RawTransactionManager rawTransactionManager = new RawTransactionManager(web3, credentials);

                BigInteger value = BigInteger.ZERO;

                if (currency.toLowerCase().equals(Constants.TOKEN_SUPPORTED.NativeCurrency.toLowerCase())) {
                    value = priceFormat;
                }

                String data = "0x" + functionEncoder;
                BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                BigInteger nonce = getNonce();
                BigInteger gasLimit = getGasLimit(value, Constants.CONTRACT.MarketService, functionEncoder);

                RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, Constants.CONTRACT.MarketService, value, data);
                String signedTransaction = rawTransactionManager.sign(rawTransaction);

                EthSendTransaction sendRawTransaction = web3.ethSendRawTransaction(signedTransaction).sendAsync().get();

                tx = sendRawTransaction.getTransactionHash();
            } catch (InterruptedException e) {
                throw e;
            } catch (ExecutionException e) {
                throw e;
            }
        } else {
            throw new Exception("Service is not supported");
        }
        return tx;
    }

    public String allowenceNFT(String nft, BigInteger tokenId) {
        String allowence = null;

        try {
            Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair());

            ERC721 erc721 = ERC721.load(nft, web3, dummyCredentials, new DefaultGasProvider());

            RemoteFunctionCall<String> approved = erc721.getApproved(tokenId);

            allowence = approved.sendAsync().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }

        return allowence;
    }

    public String approveNFT(String nft, BigInteger tokenId) throws Exception {
        String tx;
        Service service = new ServiceManager(mContext).getService(Constants.CONTRACT.MarketService);
        if (service != null) {
            try {
                Credentials credentials = WalletUtils.getInstance().init(mContext).getCredential();

                Function function = new Function("approve",
                        Arrays.asList(new Address(Constants.CONTRACT.MarketService), new Uint256(tokenId)),
                        Collections.emptyList()
                );

                String functionEncoder = FunctionEncoder.encode(function);

                RawTransactionManager rawTransactionManager = new RawTransactionManager(web3, credentials);

                BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                BigInteger gasLimit = getGasLimit(BigInteger.ZERO, nft, functionEncoder);
                BigInteger nonce = getNonce();

                RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, nft, BigInteger.ZERO, "0x" + functionEncoder);
                String signedTransaction = rawTransactionManager.sign(rawTransaction);

                EthSendTransaction sendRawTransaction = web3.ethSendRawTransaction(signedTransaction).sendAsync().get();

                tx = sendRawTransaction.getTransactionHash();
            } catch (Exception e) {
                throw e;
            }
        } else {
            throw new Exception("Service is not supported");
        }

        return tx;
    }

    public String list(String nft, BigInteger tokenId, double price, String currency) throws Exception {
        String tx;
        Service service = new ServiceManager(mContext).getService(Constants.CONTRACT.MarketService);

        if (service != null) {
            try {
                Credentials credentials = WalletUtils.getInstance().init(mContext).getCredential();

                int decimals = decimals(currency);
                Double priceFormat = price * Math.pow(10, decimals);

                Function function = new Function("list",
                        Arrays.asList(
                                new Address(nft),
                                new Uint256(tokenId),
                                new Uint256(BigDecimal.valueOf(priceFormat).toBigInteger()),
                                new Address(currency)),
                        Collections.emptyList()
                );

                String functionEncoder = FunctionEncoder.encode(function);

                RawTransactionManager rawTransactionManager = new RawTransactionManager(web3, credentials);

                BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                BigInteger gasLimit = getGasLimit(BigInteger.ZERO, Constants.CONTRACT.MarketService, functionEncoder);
                BigInteger nonce = getNonce();

                RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, Constants.CONTRACT.MarketService, BigInteger.ZERO, "0x" + functionEncoder);
                String signedTransaction = rawTransactionManager.sign(rawTransaction);

                EthSendTransaction sendRawTransaction = web3.ethSendRawTransaction(signedTransaction).sendAsync().get();

                tx = sendRawTransaction.getTransactionHash();
            } catch (Exception e) {
                throw e;
            }
        } else {
            throw new Exception("Service is not supported");
        }

        return tx;
    }

    public String unlist(BigInteger listingId) throws Exception {
        String tx;
        Service service = new ServiceManager(mContext).getService(Constants.CONTRACT.MarketService);

        if (service != null) {
            try {
                Credentials credentials = WalletUtils.getInstance().init(mContext).getCredential();

                Function function = new Function("unList", Arrays.asList(new Uint256(listingId)), Collections.emptyList());

                String functionEncoder = FunctionEncoder.encode(function);

                RawTransactionManager rawTransactionManager = new RawTransactionManager(web3, credentials);

                BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                BigInteger gasLimit = getGasLimit(BigInteger.ZERO, Constants.CONTRACT.MarketService, functionEncoder);
                BigInteger nonce = getNonce();

                RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, Constants.CONTRACT.MarketService, BigInteger.ZERO, "0x" + functionEncoder);
                String signedTransaction = rawTransactionManager.sign(rawTransaction);

                EthSendTransaction sendRawTransaction = web3.ethSendRawTransaction(signedTransaction).sendAsync().get();

                tx = sendRawTransaction.getTransactionHash();
            } catch (Exception e) {
                throw e;
            }
        } else {
            throw new Exception("Service is not supported");
        }

        return tx;
    }

    public String withdrawNFT(String nft, BigInteger tokenId) throws Exception {
        String tx;
        Service service = new ServiceManager(mContext).getService(ChainverseSDK.gameAddress);

        if (service != null) {
            try {
                Credentials credentials = WalletUtils.getInstance().init(mContext).getCredential();

                Function function = new Function("withdrawItem", Arrays.asList(new Address(nft), new Uint256(tokenId)), Collections.emptyList());

                String functionEncoder = FunctionEncoder.encode(function);

                RawTransactionManager rawTransactionManager = new RawTransactionManager(web3, credentials);

                BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                BigInteger gasLimit = getGasLimit(BigInteger.ZERO, ChainverseSDK.gameAddress, functionEncoder);
                BigInteger nonce = getNonce();

                RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ChainverseSDK.gameAddress, BigInteger.ZERO, "0x" + functionEncoder);
                String signedTransaction = rawTransactionManager.sign(rawTransaction);

                EthSendTransaction sendRawTransaction = web3.ethSendRawTransaction(signedTransaction).sendAsync().get();

                tx = sendRawTransaction.getTransactionHash();
            } catch (Exception e) {
                throw e;
            }
        } else {
            throw new Exception("Service is not supported");
        }

        return tx;
    }

//    public String withdrawCVT(double amount) throws Exception {
//        String tx;
//        Service service = new ServiceManager(mContext).getService(ChainverseSDK.gameAddress);
//
//        if (service != null) {
//            try {
//                Credentials credentials = WalletUtils.getInstance().init(mContext).getCredential();
//                int decimals = decimals(Constants.TOKEN_SUPPORTED.CVT);
//                Double amountFormat = amount * Math.pow(10, decimals);
//                Function function = new Function("withdrawCVT", Arrays.asList(new Uint256(BigDecimal.valueOf(amountFormat).toBigInteger())), Collections.emptyList());
//
//                String functionEncoder = FunctionEncoder.encode(function);
//
//                RawTransactionManager rawTransactionManager = new RawTransactionManager(web3, credentials);
//
//                BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
//                BigInteger gasLimit = getGasLimit(BigInteger.ZERO, ChainverseSDK.gameAddress, functionEncoder);
//                BigInteger nonce = getNonce();
//
//                RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ChainverseSDK.gameAddress, BigInteger.ZERO, "0x" + functionEncoder);
//                String signedTransaction = rawTransactionManager.sign(rawTransaction);
//
//                EthSendTransaction sendRawTransaction = web3.ethSendRawTransaction(signedTransaction).sendAsync().get();
//
//                tx = sendRawTransaction.getTransactionHash();
//            } catch (Exception e) {
//                throw e;
//            }
//        } else {
//            throw new Exception("Service is not supported");
//        }
//
//        return tx;
//    }
//
//    public String withdrawToken(String token, double amount) throws Exception {
//        String tx;
//        Service service = new ServiceManager(mContext).getService(ChainverseSDK.gameAddress);
//
//        if (service != null) {
//            try {
//                Credentials credentials = WalletUtils.getInstance().init(mContext).getCredential();
//                int decimals = decimals(token);
//                Double amountFormat = amount * Math.pow(10, decimals);
//                Function function = new Function("withdraw", Arrays.asList(new Uint256(BigDecimal.valueOf(amountFormat).toBigInteger())), Collections.emptyList());
//
//                String functionEncoder = FunctionEncoder.encode(function);
//
//                RawTransactionManager rawTransactionManager = new RawTransactionManager(web3, credentials);
//
//                BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
//                BigInteger gasLimit = getGasLimit(BigInteger.ZERO, ChainverseSDK.gameAddress, functionEncoder);
//                BigInteger nonce = getNonce();
//
//                RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, ChainverseSDK.gameAddress, BigInteger.ZERO, "0x" + functionEncoder);
//                String signedTransaction = rawTransactionManager.sign(rawTransaction);
//
//                EthSendTransaction sendRawTransaction = web3.ethSendRawTransaction(signedTransaction).sendAsync().get();
//
//                tx = sendRawTransaction.getTransactionHash();
//            } catch (Exception e) {
//                throw e;
//            }
//        } else {
//            throw new Exception("Service is not supported");
//        }
//
//        return tx;
//    }

    private String ownerOfOnGame(String nft, BigInteger tokenId) {
        String owner = "";
        if (ChainverseSDK.gameAddress != null) {
            Credentials credentials = WalletUtils.getInstance().init(mContext).getCredential();

            Service service = new ServiceManager(mContext).getService(ChainverseSDK.gameAddress);
            HandleContract handleContract = HandleContract.load(ChainverseSDK.gameAddress, service.getAbi(), web3, credentials, new DefaultGasProvider());

            RemoteFunctionCall<String> functionCall = handleContract.callFunc("ownerOf", Arrays.asList(nft, tokenId));

            try {
                owner = functionCall.sendAsync().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return owner;
    }

    private boolean isDeveloperContract() {
        try {
            EthCall ethCall = BaseWeb3.getInstance().init(mContext).callFunction(ChainverseSDK.developerAddress, "isDeveloperContract", new ArrayList<>());
            if (ethCall != null && ethCall.getResult() != null) {
                return Convert.hexToBool(ethCall.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isGameContract() {
        try {
            EthCall ethCall = BaseWeb3.getInstance().init(mContext).callFunction(ChainverseSDK.gameAddress, "isGameContract", new ArrayList<>());
            if (ethCall != null && ethCall.getResult() != null) {
                return Convert.hexToBool(ethCall.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isGamePaused() {
        try {
            EthCall ethCall = BaseWeb3.getInstance().init(mContext).callFunction(Constants.CONTRACT.ChainverseFactory, "isGamePaused", Arrays.asList(new Address(ChainverseSDK.gameAddress)));
            if (ethCall != null && ethCall.getResult() != null) {
                return Convert.hexToBool(ethCall.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isDeveloperPaused() {
        try {
            EthCall ethCall = BaseWeb3.getInstance().init(mContext).callFunction(Constants.CONTRACT.ChainverseFactory, "isDeveloperPaused", Arrays.asList(new Address(ChainverseSDK.developerAddress)));
            if (ethCall != null && ethCall.getResult() != null) {
                return Convert.hexToBool(ethCall.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String handleTokenUri(String hash) {
        try {
            String[] gateways = new String[]{"https://ipfs.io/ipfs/", "https://gateway.pinata.cloud/ipfs/"};
            int n = 0;
            int indHttp = -1;
            boolean check = true;

            String url = "";
            String reIpfs = "/(ipfs:\\/\\/)|(ipfs\\/)/gm";
            indHttp = hash.indexOf("https://");
            if (indHttp < 0) {
                indHttp = hash.indexOf("http://");
            }

            while (check) {
                String gateway = gateways[n];
                if (indHttp >= 0) {
                    check = false;
                    int indHash = hash.indexOf("\\?id=");
                    if (indHash >= 0 && !hash.isEmpty()) {
                        url = hash.replace("\\?id=", "?id=" + hash);
                    } else {
                        url = hash;
                    }
                } else if (hash.matches(reIpfs)) {
                    url = gateway + hash.replace(reIpfs, "");
                } else {
                    url = gateway + hash;
                }

                try {
                    String content = getUrlContents(url);
                    check = false;
                    return content;
                } catch (Exception e) {
                    if (n == gateways.length - 1) {
                        check = false;
                        return "";
                    }
                    n++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getUrlContents(String theUrl) throws Exception {
        StringBuilder content = new StringBuilder();
        // Use try and catch to avoid the exceptions
        try {
            URL url = new URL(theUrl); // creating a url object
            URLConnection urlConnection = url.openConnection(); // creating a urlconnection object

            // wrapping the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            // reading from the urlconnection using the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            throw e;
//            e.printStackTrace();
        }
        return content.toString();
    }

    private class DownloadContent extends AsyncTask<String, Void, String> {
        String content;

        protected String doInBackground(String... urls) {
            System.out.println("run background ");
            return handleTokenUri(urls[0]);
        }

        protected void onPostExecute(String result) {
            this.content = result;
        }
    }

    private BigInteger getNonce() {
        BigInteger nonce = BigInteger.ONE;

        try {
            String address = WalletUtils.getInstance().init(mContext).getAddress();

            EthBlock blockNumber = web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).sendAsync().get();
            BigInteger block = blockNumber.getBlock().getNumber().add(new BigInteger("5"));

            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                    address, DefaultBlockParameterName.LATEST).sendAsync().get();

            nonce = ethGetTransactionCount.getTransactionCount();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return nonce;
    }

    private BigInteger getGasLimit(BigInteger value, String to, String data) throws Exception {
        BigInteger gasLimit;
        String address = WalletUtils.getInstance().init(mContext).getAddress();
        try {

            Transaction transaction = Transaction.createFunctionCallTransaction(address, getNonce(), null, null, to, value, data);
            Request<?, EthEstimateGas> rs = web3.ethEstimateGas(transaction);
            EthEstimateGas eGasLimit = rs.sendAsync().get();
            if (eGasLimit.hasError()) {
                throw new Exception(eGasLimit.getError().getMessage());
            } else {
                gasLimit = eGasLimit.getAmountUsed();
            }
        } catch (Exception e) {
            throw e;
        }
        return gasLimit;
    }
}
