package com.chainverse.sdk.manager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.chainverse.sdk.ChainverseError;
import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.base.web3.BaseWeb3;
import com.chainverse.sdk.blockchain.Contract;
import com.chainverse.sdk.blockchain.ERC20;
import com.chainverse.sdk.blockchain.ERC721;
import com.chainverse.sdk.blockchain.HandleContract;
import com.chainverse.sdk.common.BroadcastUtil;
import com.chainverse.sdk.common.CallbackToGame;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.Convert;
import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.listener.Action;
import com.chainverse.sdk.model.MarketItem.Currency;
import com.chainverse.sdk.model.NFT.Auction;
import com.chainverse.sdk.model.NFT.InfoSell;
import com.chainverse.sdk.model.NFT.Listing;
import com.chainverse.sdk.model.NFT.NFT;
import com.chainverse.sdk.model.TransactionData;
import com.chainverse.sdk.model.service.Service;
import com.chainverse.sdk.ui.ChainverseSDKActivity;
import com.chainverse.sdk.wallet.chainverse.ChainverseConnect;
import com.esaulpaugh.headlong.abi.Tuple;
import com.esaulpaugh.headlong.util.FastHex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint128;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rx.functions.Func0;
import wallet.core.jni.AnyAddress;
import wallet.core.jni.CoinType;


public class ContractManager {
    public interface Listener {
        void isChecked(boolean isCheck);
    }

    private static ContractManager instance;
    private Listener listener;
    private Context mContext;
    private EncryptPreferenceUtils encryptPreferenceUtils;
    private ChainverseConnect chainverseConnect;

    Web3j web3;

    public static ContractManager getInstance() {
        if (instance == null) {
            instance = new ContractManager();
        }
        return instance;
    }

    public ContractManager init(Context context, Listener listener) {
        this.listener = listener;
        mContext = context;
        encryptPreferenceUtils = EncryptPreferenceUtils.getInstance().init(mContext);
        chainverseConnect = new ChainverseConnect.Builder().build(mContext);
        if (ServiceManager.getInstance().init(context).getRPC() != null) {
            web3 = Web3j.build(new HttpService(ServiceManager.getInstance().init(context).getRPC()));
        }
        return instance;
    }

    public ContractManager init(Context context) {
        mContext = context;
        encryptPreferenceUtils = EncryptPreferenceUtils.getInstance().init(mContext);
        chainverseConnect = new ChainverseConnect.Builder().build(mContext);
        if (ServiceManager.getInstance().init(context).getRPC() != null) {
            web3 = Web3j.build(new HttpService(ServiceManager.getInstance().init(context).getRPC()));
        }
        return instance;
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
        if (web3 != null) {
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
        }

        return decimals;
    }

    public String symbol(String currency) {
        String symbol = "";
        if (currency.toLowerCase().equals(Constants.TOKEN_SUPPORTED.NativeCurrency.toLowerCase())) {
            return symbol;
        }
        if (web3 != null) {
            try {
                Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair());
                ERC20 erc20 = ERC20.load(currency, web3, dummyCredentials, new DefaultGasProvider());

                RemoteCall<String> result = erc20.symbol();

                symbol = result.sendAsync().get();
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
        }

        return symbol;
    }

    public org.web3j.utils.Convert.Unit getWei(String currency) {
        org.web3j.utils.Convert.Unit decimals = org.web3j.utils.Convert.Unit.WEI;
        if (currency.toLowerCase().equals(Constants.TOKEN_SUPPORTED.NativeCurrency.toLowerCase())) {
            return decimals;
        }
        if (web3 != null) {
            try {
                Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair());
                ERC20 erc20 = ERC20.load(currency, web3, dummyCredentials, new DefaultGasProvider());

                RemoteCall<BigInteger> result = erc20.decimals();

                int decimalToken = result.sendAsync().get().intValue();
                switch (decimalToken) {
                    case 0:
                        return org.web3j.utils.Convert.Unit.WEI;
                    case 3:
                        return org.web3j.utils.Convert.Unit.KWEI;
                    case 6:
                        return org.web3j.utils.Convert.Unit.MWEI;
                    case 9:
                        return org.web3j.utils.Convert.Unit.GWEI;
                    case 12:
                        return org.web3j.utils.Convert.Unit.SZABO;
                    case 15:
                        return org.web3j.utils.Convert.Unit.FINNEY;
                    case 18:
                        return org.web3j.utils.Convert.Unit.ETHER;
                    case 21:
                        return org.web3j.utils.Convert.Unit.KETHER;
                    case 24:
                        return org.web3j.utils.Convert.Unit.METHER;
                    case 27:
                        return org.web3j.utils.Convert.Unit.GETHER;
                }
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
        Service service = ServiceManager.getInstance().init(mContext, Constants.CONTRACT.MarketService).getService();

        if (service != null && web3 != null) {
            try {
                Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair());
                HandleContract handleContract = HandleContract.load(Constants.CONTRACT.MarketService, service.getAbi(), web3, dummyCredentials, new DefaultGasProvider());
                ERC721 contractERC721 = ERC721.load(nft, web3, dummyCredentials, new DefaultGasProvider());
                ERC20 contractERC20 = null;

                RemoteCall<String> remoteCallUri = contractERC721.tokenURI(tokenId);

                String uri = remoteCallUri.sendAsync().get();

                String ownerOf = contractERC721.ownerOf(tokenId).sendAsync().get();

                String content = new DownloadContent().execute(uri).get();

                item.setTokenId(tokenId);
                item.setNft(nft);
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

                int feeAuction = Integer.parseInt(auctionInfo.fee.toString(), 8);
                BigInteger bidDuration = new BigInteger(auctionInfo.bidDuration.toString(), 18);
                BigInteger bidEnd = new BigInteger(auctionInfo.end.toString(), 18);
                BigDecimal bid = org.web3j.utils.Convert.fromWei(new BigDecimal(auctionInfo.bid), getWei(auctionInfo.currency));
                double bidDouble = bid != null ? Double.parseDouble(bid.toString()) : 0.0;

                Auction auction = new Auction(auctionInfo.isEnded, auctionInfo.nft, auctionInfo.owner, auctionInfo.currency, auctionInfo.tokenId, feeAuction, auctionInfo.id, auctionInfo.winner, bidDouble, bidDuration, bidEnd);

                int feeListing = Integer.parseInt(listingInfo.fee.toString(), 8);
                BigDecimal priceListing = org.web3j.utils.Convert.fromWei(new BigDecimal(listingInfo.price), getWei(listingInfo.currency));
                double priceDouble = priceListing != null ? Double.parseDouble(priceListing.toString()) : 0.0;

                Listing listing = new Listing(listingInfo.isEnded, listingInfo.nft, listingInfo.owner, listingInfo.currency, listingInfo.tokenId, feeListing, listingInfo.id, priceDouble);

                item.setAuction(auction);
                item.setListing(listing);

                Currency currency = new Currency();

                InfoSell infoSell = new InfoSell();
                infoSell.setIsAuction((auction.getId().equals(BigInteger.ZERO)) ? false : true);
                infoSell.setPrice(0.0);
                infoSell.setListingId(BigInteger.ZERO);

                if (!auction.getId().equals(BigInteger.ZERO)) {
                    contractERC20 = ERC20.load(auction.getCurrency(), web3, dummyCredentials, new DefaultGasProvider());
                    currency.setCurrency(auction.getCurrency());

                    infoSell.setListingId(auction.getId());
                    infoSell.setPrice(bidDouble);
                    item.setOwner(auction.getOwner());
                }
                if (!listing.getId().equals(BigInteger.ZERO) && listing.getPrice() != 0.0) {
                    contractERC20 = ERC20.load(listing.getCurrency(), web3, dummyCredentials, new DefaultGasProvider());
                    currency.setCurrency(listing.getCurrency());

                    infoSell.setListingId(listing.getId());
                    infoSell.setPrice(priceDouble);
                    item.setOwner(listing.getOwner());
                }

                if (contractERC20 != null) {
                    int decimals = contractERC20.decimals().sendAsync().get().intValue();
                    String name = contractERC20.name().sendAsync().get();
                    String symbol = contractERC20.symbol().sendAsync().get();

                    currency.setDecimal(decimals);
                    currency.setName(name);
                    currency.setSymbol(symbol);
                    infoSell.setCurrencyInfo(currency);
                }

                item.setInfoSell(infoSell);

                return item;
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            throw new Exception(ChainverseError.SERVICE_NOT_SUPPORTED);
        }

    }

    public BigInteger allowance(String token, String owner, String spender) {
        BigInteger allowance = BigInteger.ZERO;
        if (web3 != null) {
            try {
                Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair());

                ERC20 erc20 = ERC20.load(token, web3, dummyCredentials, new DefaultGasProvider());

                RemoteCall<BigInteger> remoteCall = (RemoteCall<BigInteger>) erc20.allowance(owner, spender);

                allowance = remoteCall.sendAsync().get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
        }

        return allowance;
    }

    public void approved(String token, String spender, double amount) {
        showLoading(() -> {
            ServiceManager serviceManager = ServiceManager.getInstance().init(mContext);

            if (web3 != null) {
                if (checkAddress(spender, serviceManager.getNetworkInfo().getChainId())) {
                    try {

                        String typeConnect = encryptPreferenceUtils.getConnectWallet();
                        int decimals = decimals(token);
                        BigInteger priceFormat = BigDecimal.valueOf(amount * Math.pow(10, decimals)).toBigInteger();

                        Function function = new Function("approve",
                                Arrays.asList(new Address(spender), new Uint256(priceFormat)),
                                Collections.emptyList()
                        );

                        String functionEncoder = FunctionEncoder.encode(function);

                        BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                        BigInteger gasLimit = getGasLimit(BigInteger.ZERO, token, functionEncoder);
                        BigInteger nonce = getNonce();
                        String data = "0x" + functionEncoder;

                        if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
                            WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);
                            String symbol = symbol(token);

                            TransactionData transactionData = new TransactionData(
                                    nonce.longValue(), gasPrice.longValue(), gasLimit.longValue(),
                                    0, data, token, walletUtils.getAddress(),
                                    Constants.EFunction.approveToken, symbol);
                            transactionData.setDecimals(decimals);
                            transactionData.setReceiver(spender);
                            transactionData.setPrice(amount);
                            transactionData.setSymbol(symbol);

                            Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
                            intent.putExtra("transactionData", transactionData);
                            intent.putExtra("screen", Constants.SCREEN.CONFIRM_TRANSACTION);
                            mContext.startActivity(intent);
                        } else if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.CHAINVERSE)) {
                            chainverseConnect.sendTransaction(Constants.EFunction.approveToken, token, functionEncoder, BigInteger.ZERO, gasLimit, gasPrice, nonce);
                        }
                    } catch (InterruptedException e) {
                        CallbackToGame.onErrorTransaction(Constants.EFunction.approveToken, e.getLocalizedMessage());
                    } catch (ExecutionException e) {
                        CallbackToGame.onErrorTransaction(Constants.EFunction.approveToken, e.getLocalizedMessage());
                    } catch (Exception e) {
                        CallbackToGame.onErrorTransaction(Constants.EFunction.approveToken, e.getLocalizedMessage());
                    }
                } else {
                    CallbackToGame.onErrorTransaction(Constants.EFunction.approveToken, ChainverseError.ADDRESS_INVALID);
                }
            } else {
                CallbackToGame.onErrorTransaction(Constants.EFunction.approveToken, ChainverseError.SERVICE_NOT_SUPPORTED);
            }
            BroadcastUtil.send(mContext, Constants.ACTION.DIMISS_LOADING);
        });
    }

    public void buyNFT(String currency, BigInteger listingId, double price) {
        Service service = ServiceManager.getInstance().init(mContext, Constants.CONTRACT.MarketService).getService();
        showLoading(() -> {
            if (service != null && web3 != null) {
                try {
                    String typeConnect = encryptPreferenceUtils.getConnectWallet();
                    int decimals = decimals(currency);
                    BigInteger priceFormat = BigDecimal.valueOf(price * Math.pow(10, decimals)).toBigInteger();

                    Function function = new Function("buy", Arrays.asList(new Uint256(listingId), new Uint256(priceFormat)), Collections.emptyList());

                    String functionEncoder = FunctionEncoder.encode(function);

                    BigInteger value = BigInteger.ZERO;

                    if (currency.toLowerCase().equals(Constants.TOKEN_SUPPORTED.NativeCurrency.toLowerCase())) {
                        value = priceFormat;
                    }

                    String data = "0x" + functionEncoder;
                    BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                    BigInteger nonce = getNonce();
                    BigInteger gasLimit = getGasLimit(value, Constants.CONTRACT.MarketService, functionEncoder);


                    if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
                        WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);

                        TransactionData transactionData = new TransactionData(
                                nonce.longValue(), gasPrice.longValue(), gasLimit.longValue(),
                                0, data, Constants.CONTRACT.MarketService, walletUtils.getAddress(),
                                Constants.EFunction.buyNFT, "NFT Game");
                        transactionData.setReceiver(Constants.CONTRACT.MarketService);
                        transactionData.setPrice(price);
                        transactionData.setSymbol(symbol(currency));
                        transactionData.setDecimals(decimals);

                        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
                        intent.putExtra("transactionData", transactionData);
                        intent.putExtra("screen", Constants.SCREEN.CONFIRM_TRANSACTION);

                        mContext.startActivity(intent);

                    } else if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.CHAINVERSE)) {
                        chainverseConnect.sendTransaction(Constants.EFunction.buyNFT, Constants.CONTRACT.MarketService, functionEncoder, value, gasLimit, gasPrice, nonce);
                    }
                } catch (InterruptedException e) {
                    CallbackToGame.onErrorTransaction(Constants.EFunction.buyNFT, e.getLocalizedMessage());
                } catch (ExecutionException e) {
                    CallbackToGame.onErrorTransaction(Constants.EFunction.buyNFT, e.getLocalizedMessage());
                } catch (Exception e) {
                    CallbackToGame.onErrorTransaction(Constants.EFunction.buyNFT, e.getLocalizedMessage());
                }
            } else {
                CallbackToGame.onErrorTransaction(Constants.EFunction.buyNFT, ChainverseError.SERVICE_NOT_SUPPORTED);
            }
            BroadcastUtil.send(mContext, Constants.ACTION.DIMISS_LOADING);
        });

    }

    public void bidNFT(String currency, BigInteger listingId, double price) {
        Service service = ServiceManager.getInstance().init(mContext, Constants.CONTRACT.MarketService).getService();
        showLoading(() -> {
            if (service != null && web3 != null) {
                try {
                    String typeConnect = encryptPreferenceUtils.getConnectWallet();

                    int decimals = decimals(currency);
                    BigInteger priceFormat = BigDecimal.valueOf(price * Math.pow(10, decimals)).toBigInteger();

                    Function function = new Function("bid", Arrays.asList(new Uint256(listingId), new Uint256(priceFormat)), Collections.emptyList());

                    String functionEncoder = FunctionEncoder.encode(function);

                    BigInteger value = BigInteger.ZERO;

                    if (currency.toLowerCase().equals(Constants.TOKEN_SUPPORTED.NativeCurrency.toLowerCase())) {
                        value = priceFormat;
                    }

                    String data = "0x" + functionEncoder;
                    BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                    BigInteger nonce = getNonce();
                    BigInteger gasLimit = getGasLimit(value, Constants.CONTRACT.MarketService, functionEncoder);

                    if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
                        WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);

                        TransactionData transactionData = new TransactionData(
                                nonce.longValue(), gasPrice.longValue(), gasLimit.longValue(),
                                0, data, Constants.CONTRACT.MarketService, walletUtils.getAddress(),
                                Constants.EFunction.bidNFT, "NFT Game");
                        transactionData.setReceiver(Constants.CONTRACT.MarketService);
                        transactionData.setPrice(price);
                        transactionData.setSymbol(symbol(currency));
                        transactionData.setDecimals(decimals);

                        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
                        intent.putExtra("transactionData", transactionData);
                        intent.putExtra("screen", Constants.SCREEN.CONFIRM_TRANSACTION);

                        mContext.startActivity(intent);
                    } else if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.CHAINVERSE)) {
                        chainverseConnect.sendTransaction(Constants.EFunction.bidNFT, Constants.CONTRACT.MarketService, functionEncoder, value, gasLimit, gasPrice, nonce);
                    }
                } catch (InterruptedException e) {
                    CallbackToGame.onErrorTransaction(Constants.EFunction.bidNFT, e.getLocalizedMessage());
                } catch (ExecutionException e) {
                    CallbackToGame.onErrorTransaction(Constants.EFunction.bidNFT, e.getLocalizedMessage());
                } catch (Exception e) {
                    CallbackToGame.onErrorTransaction(Constants.EFunction.buyNFT, e.getLocalizedMessage());
                }
            } else {
                CallbackToGame.onErrorTransaction(Constants.EFunction.bidNFT, ChainverseError.SERVICE_NOT_SUPPORTED);
            }
            BroadcastUtil.send(mContext, Constants.ACTION.DIMISS_LOADING);
        });
    }

    public String allowenceNFT(String nft, BigInteger tokenId) {
        String allowence = null;

        if (web3 != null) {
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
        }

        return allowence;
    }

    public void approveNFT(String nft, BigInteger tokenId) {
        this.approveNFT(nft, tokenId, Constants.CONTRACT.MarketService);
    }

    public void approveNFTForGame(String nft, BigInteger tokenId) {
        this.approveNFT(nft, tokenId, ChainverseSDK.gameAddress);
    }

    public void approveNFTForService(String nft, String service, BigInteger tokenId) {
        this.approveNFT(nft, tokenId, service);
    }

    public void list(String nft, BigInteger tokenId, double price, String currency) {
        Service service = ServiceManager.getInstance().init(mContext, Constants.CONTRACT.MarketService).getService();

        showLoading(() -> {
            if (service != null && web3 != null) {
                try {
                    String typeConnect = encryptPreferenceUtils.getConnectWallet();

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
                    String data = "0x" + functionEncoder;

                    BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                    BigInteger gasLimit = getGasLimit(BigInteger.ZERO, Constants.CONTRACT.MarketService, functionEncoder);
                    BigInteger nonce = getNonce();

                    if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
                        WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);

                        TransactionData transactionData = new TransactionData(
                                nonce.longValue(), gasPrice.longValue(), gasLimit.longValue(),
                                0, data, Constants.CONTRACT.MarketService, walletUtils.getAddress(),
                                Constants.EFunction.sell, "NFT Game");
                        transactionData.setReceiver(Constants.CONTRACT.MarketService);
                        transactionData.setPrice(price);
                        transactionData.setSymbol(symbol(currency));
                        transactionData.setDecimals(decimals);

                        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
                        intent.putExtra("transactionData", transactionData);
                        intent.putExtra("screen", Constants.SCREEN.CONFIRM_TRANSACTION);

                        mContext.startActivity(intent);
                    } else if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.CHAINVERSE)) {
                        chainverseConnect.sendTransaction(Constants.EFunction.sell, Constants.CONTRACT.MarketService, functionEncoder, BigInteger.ZERO, gasLimit, gasPrice, nonce);
                    }
                } catch (Exception e) {
                    CallbackToGame.onErrorTransaction(Constants.EFunction.sell, e.getLocalizedMessage());
                }
            } else {
                CallbackToGame.onErrorTransaction(Constants.EFunction.sell, ChainverseError.SERVICE_NOT_SUPPORTED);
            }
            BroadcastUtil.send(mContext, Constants.ACTION.DIMISS_LOADING);
        });
    }

    public void unlist(BigInteger listingId) {
        Service service = ServiceManager.getInstance().init(mContext, Constants.CONTRACT.MarketService).getService();
        showLoading(() -> {
            if (service != null && web3 != null) {
                try {
                    String typeConnect = encryptPreferenceUtils.getConnectWallet();
                    Function function = new Function("unList", Arrays.asList(new Uint256(listingId)), Collections.emptyList());

                    String functionEncoder = FunctionEncoder.encode(function);
                    String data = "0x" + functionEncoder;

                    BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                    BigInteger gasLimit = getGasLimit(BigInteger.ZERO, Constants.CONTRACT.MarketService, functionEncoder);
                    BigInteger nonce = getNonce();

                    if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
                        WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);

                        TransactionData transactionData = new TransactionData(
                                nonce.longValue(), gasPrice.longValue(), gasLimit.longValue(),
                                0, data, Constants.CONTRACT.MarketService, walletUtils.getAddress(),
                                Constants.EFunction.cancelSell, "NFT Game");
                        transactionData.setReceiver(Constants.CONTRACT.MarketService);

                        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
                        intent.putExtra("transactionData", transactionData);
                        intent.putExtra("screen", Constants.SCREEN.CONFIRM_TRANSACTION);

                        mContext.startActivity(intent);

                    } else if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.CHAINVERSE)) {
                        chainverseConnect.sendTransaction(Constants.EFunction.cancelSell, Constants.CONTRACT.MarketService, functionEncoder, BigInteger.ZERO, gasLimit, gasPrice, nonce);
                    }
                } catch (Exception e) {
                    CallbackToGame.onErrorTransaction(Constants.EFunction.cancelSell, e.getLocalizedMessage());
                }
            } else {
                CallbackToGame.onErrorTransaction(Constants.EFunction.cancelSell, ChainverseError.SERVICE_NOT_SUPPORTED);
            }
            BroadcastUtil.send(mContext, Constants.ACTION.DIMISS_LOADING);
        });
    }

    public void withdrawNFT(String nft, BigInteger tokenId) {
        Service service = ServiceManager.getInstance().init(mContext, ChainverseSDK.gameAddress).getService();
        showLoading(() -> {
            if (service != null && web3 != null) {
                try {
                    String typeConnect = encryptPreferenceUtils.getConnectWallet();

                    Function function = new Function("withdrawItem", Arrays.asList(new Address(nft), new Uint256(tokenId)), Collections.emptyList());

                    String functionEncoder = FunctionEncoder.encode(function);
                    String data = "0x" + functionEncoder;

                    BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                    BigInteger gasLimit = getGasLimit(BigInteger.ZERO, ChainverseSDK.gameAddress, functionEncoder);
                    BigInteger nonce = getNonce();

                    if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
                        WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);

                        TransactionData transactionData = new TransactionData(
                                nonce.longValue(), gasPrice.longValue(), gasLimit.longValue(),
                                0, data, ChainverseSDK.gameAddress, walletUtils.getAddress(),
                                Constants.EFunction.withdrawItem, "#" + tokenId);
                        transactionData.setReceiver(ChainverseSDK.gameAddress);

                        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
                        intent.putExtra("transactionData", transactionData);
                        intent.putExtra("screen", Constants.SCREEN.CONFIRM_TRANSACTION);

                        mContext.startActivity(intent);
                    } else if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.CHAINVERSE)) {
                        chainverseConnect.sendTransaction(Constants.EFunction.withdrawItem, ChainverseSDK.gameAddress, functionEncoder, BigInteger.ZERO, gasLimit, gasPrice, nonce);
                    }

                } catch (Exception e) {
                    CallbackToGame.onErrorTransaction(Constants.EFunction.withdrawItem, e.getLocalizedMessage());
                }
            } else {
                CallbackToGame.onErrorTransaction(Constants.EFunction.withdrawItem, ChainverseError.SERVICE_NOT_SUPPORTED);
            }
            BroadcastUtil.send(mContext, Constants.ACTION.DIMISS_LOADING);
        });
    }

//    public String withdrawCVT(double amount) throws Exception {
//        String tx;
//        Service service = ServiceManager(mContext,ChainverseSDK.gameAddress).getService();
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
//    if (sendRawTransaction.hasError()) {
//        throw new Exception(sendRawTransaction.getError().getMessage());
//    }
//                tx = sendRawTransaction.getTransactionHash();
//            } catch (Exception e) {
//                throw e;
//            }
//        } else {
//            throw new Exception(ChainverseError.SERVICE_NOT_SUPPORTED);
//        }
//
//        return tx;
//    }
//
//    public String withdrawToken(String token, double amount) throws Exception {
//        String tx;
//        Service service = new ServiceManager(mContext,ChainverseSDK.gameAddress).getService();
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
//    if (sendRawTransaction.hasError()) {
//        throw new Exception(sendRawTransaction.getError().getMessage());
//    }
//                tx = sendRawTransaction.getTransactionHash();
//            } catch (Exception e) {
//                throw e;
//            }
//        } else {
//            throw new Exception(ChainverseError.SERVICE_NOT_SUPPORTED);
//        }
//
//        return tx;
//    }

    public void moveItemToGame(String nft, BigInteger tokenId) {
        Service service = ServiceManager.getInstance().init(mContext, ChainverseSDK.gameAddress).getService();
        if (service != null) {
            this.moveService(nft, ChainverseSDK.gameAddress, tokenId);
        } else {
            CallbackToGame.onErrorTransaction(Constants.EFunction.moveService, ChainverseError.SERVICE_NOT_SUPPORTED);
        }
    }

    public void moveService(String nft, String serviceAddress, BigInteger tokenId) {
        Service service = ServiceManager.getInstance().init(mContext, ChainverseSDK.gameAddress).getService();

        showLoading(() -> {
            if (service != null && web3 != null) {
                try {
                    String typeConnect = encryptPreferenceUtils.getConnectWallet();
                    Function function = new Function("moveService", Arrays.asList(new Address(nft), new Uint256(tokenId), new Address(serviceAddress)), Collections.emptyList());

                    String functionEncoder = FunctionEncoder.encode(function);
                    String data = "0x" + functionEncoder;

                    BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                    BigInteger gasLimit = getGasLimit(BigInteger.ZERO, serviceAddress, functionEncoder);
                    BigInteger nonce = getNonce();

                    if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
                        WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);

                        TransactionData transactionData = new TransactionData(
                                nonce.longValue(), gasPrice.longValue(), gasLimit.longValue(),
                                0, data, serviceAddress, walletUtils.getAddress(),
                                Constants.EFunction.moveService, "#" + tokenId);
                        transactionData.setReceiver(serviceAddress);

                        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
                        intent.putExtra("transactionData", transactionData);
                        intent.putExtra("screen", Constants.SCREEN.CONFIRM_TRANSACTION);

                        mContext.startActivity(intent);
                    } else if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.CHAINVERSE)) {
                        chainverseConnect.sendTransaction(Constants.EFunction.moveService, serviceAddress, functionEncoder, BigInteger.ZERO, gasLimit, gasPrice, nonce);
                    }
                } catch (Exception e) {
                    CallbackToGame.onErrorTransaction(Constants.EFunction.moveService, e.getLocalizedMessage());
                }
            } else {
                CallbackToGame.onErrorTransaction(Constants.EFunction.moveService, ChainverseError.SERVICE_NOT_SUPPORTED);
            }
            BroadcastUtil.send(mContext, Constants.ACTION.DIMISS_LOADING);
        });
    }

    public void transferItem(String from, String to, String nft, BigInteger tokenId) {
        ServiceManager serviceManager = ServiceManager.getInstance().init(mContext);

        showLoading(() -> {
            if (web3 != null) {
                if (checkAddress(to, serviceManager.getNetworkInfo().getChainId())) {
                    try {
                        String typeConnect = encryptPreferenceUtils.getConnectWallet();

                        Function function = new Function("transferFrom", Arrays.asList(new Address(from), new Address(to), new Uint256(tokenId)), Collections.emptyList());

                        String functionEncoder = FunctionEncoder.encode(function);
                        String data = "0x" + functionEncoder;

                        BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                        BigInteger gasLimit = getGasLimit(BigInteger.ZERO, nft, functionEncoder);
                        BigInteger nonce = getNonce();

                        if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
                            WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);

                            TransactionData transactionData = new TransactionData(
                                    nonce.longValue(), gasPrice.longValue(), gasLimit.longValue(),
                                    0, data, nft, from,
                                    Constants.EFunction.transferItem, "#" + tokenId);
                            transactionData.setReceiver(to);

                            Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
                            intent.putExtra("transactionData", transactionData);
                            intent.putExtra("screen", Constants.SCREEN.CONFIRM_TRANSACTION);

                            mContext.startActivity(intent);
                        } else if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.CHAINVERSE)) {
                            chainverseConnect.sendTransaction(Constants.EFunction.transferItem, nft, functionEncoder, BigInteger.ZERO, gasLimit, gasPrice, nonce);
                        }
                    } catch (Exception e) {
                        CallbackToGame.onErrorTransaction(Constants.EFunction.transferItem, e.getLocalizedMessage());
                    }
                } else {
                    CallbackToGame.onErrorTransaction(Constants.EFunction.transferItem, ChainverseError.ADDRESS_INVALID);
                }
            } else {
                CallbackToGame.onErrorTransaction(Constants.EFunction.transferItem, ChainverseError.SERVICE_NOT_SUPPORTED);
            }
            BroadcastUtil.send(mContext, Constants.ACTION.DIMISS_LOADING);
        });
    }

    public double estimateGasDefault(Constants.EFunction function, List inputs) throws Exception {
        double fee;

        try {
            fee = estimateFee(function, inputs);
        } catch (Exception e) {
            throw e;
        }

        return fee;
    }

    public boolean checkAddress(String address, String chainId) {
        boolean check;

        CoinType coinType = getCoinType(chainId);
        if (coinType == null) {
            check = false;
        } else {
            check = AnyAddress.isValid(address, coinType);
        }


        return check;
    }

    private double estimateFee(Constants.EFunction function, List inputs) throws Exception {
        double fee;
        String func = "";
        String service = "";
        List contractInputs;
        int decimals = 18;
        BigInteger price;
        BigInteger value = BigInteger.ZERO;

        switch (function) {
            case approveNFT:
                func = "approve";
                service = (String) inputs.get(0);
                contractInputs = Arrays.asList(new Address((String) inputs.get(1)), new Uint256((BigInteger) inputs.get(2)));
                break;
            case approveToken:
                func = "approve";
                service = (String) inputs.get(0);
                decimals = decimals((String) inputs.get(0));
                price = BigDecimal.valueOf((double) inputs.get(2) * Math.pow(10, decimals)).toBigInteger();
                if (((String) inputs.get(0)).toLowerCase().equals(Constants.TOKEN_SUPPORTED.NativeCurrency.toLowerCase())) {
                    value = price;
                }
                contractInputs = Arrays.asList(new Address((String) inputs.get(1)), new Uint256(price));
                break;
            case bidNFT:
                func = "bid";
                service = Constants.CONTRACT.MarketService;
                decimals = decimals((String) inputs.get(0));
                price = BigDecimal.valueOf((double) inputs.get(2) * Math.pow(10, decimals)).toBigInteger();
                if (((String) inputs.get(0)).toLowerCase().equals(Constants.TOKEN_SUPPORTED.NativeCurrency.toLowerCase())) {
                    value = price;
                }
                contractInputs = Arrays.asList(new Uint256((BigInteger) inputs.get(1)), new Uint256(price));
                break;
            case buyNFT:
                func = "buy";
                service = Constants.CONTRACT.MarketService;
                decimals = decimals((String) inputs.get(0));
                price = BigDecimal.valueOf((double) inputs.get(2) * Math.pow(10, decimals)).toBigInteger();
                if (((String) inputs.get(0)).toLowerCase().equals(Constants.TOKEN_SUPPORTED.NativeCurrency.toLowerCase())) {
                    value = price;
                }
                contractInputs = Arrays.asList(new Uint256((BigInteger) inputs.get(1)), new Uint256(price));
                break;
            case cancelSell:
                func = "unList";
                service = Constants.CONTRACT.MarketService;
                contractInputs = Arrays.asList(new Uint256((BigInteger) inputs.get(0)));
                break;
            case sell:
                func = "list";
                service = Constants.CONTRACT.MarketService;
                decimals = decimals((String) inputs.get(0));
                price = BigDecimal.valueOf((double) inputs.get(2) * Math.pow(10, decimals)).toBigInteger();
                contractInputs = Arrays.asList(
                        new Address((String) inputs.get(1)),
                        new Uint256((BigInteger) inputs.get(3)),
                        new Uint256(price),
                        new Address((String) inputs.get(0)));
                break;
            case moveService:
                func = "moveService";
                service = (String) inputs.get(1);
                contractInputs = Arrays.asList(new Address((String) inputs.get(0)), new Uint256((BigInteger) (inputs.get(2))), new Address((String) inputs.get(1)));
                break;
            case withdrawItem:
                func = "withdrawItem";
                service = ChainverseSDK.gameAddress;
                contractInputs = Arrays.asList(new Address((String) inputs.get(0)), new Uint256((BigInteger) inputs.get(1)));
                break;
            default:
                throw new Exception("The function is not supported");
        }

        if (web3 != null) {
            try {
                Function contractFunction = new Function(func, contractInputs, Collections.emptyList());

                String functionEncoder = FunctionEncoder.encode(contractFunction);
                EthGasPrice ethGasPrice = web3.ethGasPrice().sendAsync().get();

                double gasPrice = ethGasPrice.getGasPrice().intValue() * Math.pow(10, -decimals);
                BigInteger gasLimit = getGasLimit(value, service, functionEncoder);

                fee = gasLimit.intValue() * gasPrice;
            } catch (Exception e) {
                throw e;
            }
        } else {
            throw new Exception(ChainverseError.SERVICE_NOT_SUPPORTED);
        }

        return fee;
    }

    private String ownerOfOnGame(String nft, BigInteger tokenId) throws Exception {
        String owner = "";
        if (ChainverseSDK.gameAddress != null && web3 != null) {
            try {
                Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair());

                Service service = ServiceManager.getInstance().init(mContext, ChainverseSDK.gameAddress).getService();
                HandleContract handleContract = HandleContract.load(ChainverseSDK.gameAddress, service.getAbi(), web3, dummyCredentials, new DefaultGasProvider());

                RemoteFunctionCall<String> functionCall = handleContract.callFunc("ownerOf", Arrays.asList(nft, tokenId));

                owner = functionCall.sendAsync().get();
            } catch (ExecutionException e) {
                throw e;
            } catch (InterruptedException e) {
                throw e;
            } catch (Exception e) {
                throw e;
            }
        }

        return owner;
    }

    private void approveNFT(String nft, BigInteger tokenId, String serviceAddress) {
        Service service = ServiceManager.getInstance().init(mContext, serviceAddress).getService();
        showLoading(() -> {
            if (service != null && web3 != null) {
                try {
                    String typeConnect = encryptPreferenceUtils.getConnectWallet();

                    Function function = new Function("approve",
                            Arrays.asList(new Address(serviceAddress), new Uint256(tokenId)),
                            Collections.emptyList()
                    );

                    String functionEncoder = FunctionEncoder.encode(function);
                    String data = "0x" + functionEncoder;

                    BigInteger gasPrice = web3.ethGasPrice().sendAsync().get().getGasPrice();
                    BigInteger gasLimit = getGasLimit(BigInteger.ZERO, nft, functionEncoder);
                    BigInteger nonce = getNonce();

                    if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
                        WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);

                        TransactionData transactionData = new TransactionData(
                                nonce.longValue(), gasPrice.longValue(), gasLimit.longValue(),
                                0, data, nft, walletUtils.getAddress(),
                                Constants.EFunction.approveNFT, "#" + tokenId);
                        transactionData.setReceiver(serviceAddress);

                        BroadcastUtil.send(mContext, Constants.ACTION.DIMISS_LOADING);

                        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
                        intent.putExtra("transactionData", transactionData);
                        intent.putExtra("screen", Constants.SCREEN.CONFIRM_TRANSACTION);
                        mContext.startActivity(intent);
                    } else if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.CHAINVERSE)) {
                        chainverseConnect.sendTransaction(Constants.EFunction.approveNFT, nft, functionEncoder, BigInteger.ZERO, gasLimit, gasPrice, nonce);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                CallbackToGame.onErrorTransaction(Constants.EFunction.approveNFT, ChainverseError.SERVICE_NOT_SUPPORTED);
            }
            BroadcastUtil.send(mContext, Constants.ACTION.DIMISS_LOADING);
        });

    }

    public List callContract(String contractAddress, String nameFunction, Object[] args) throws Exception {
        String abi = ServiceManager.getInstance().init(mContext, contractAddress).getService().getAbi();
        Contract contract = Contract.load(mContext, abi, contractAddress);
        return contract.callContract(nameFunction, args);
    }

    public List callContract(String contractAddress, String nameFunction, Object[] args, BigInteger value) throws Exception {
        String abi = ServiceManager.getInstance().init(mContext, contractAddress).getService().getAbi();
        Contract contract = Contract.load(mContext, abi, contractAddress);
        return contract.callContract(nameFunction, args, value);
    }

    public List callContract(String contractAddress, String nameFunction, String typeInputs, Object[] args) throws Exception {
        String abi = ServiceManager.getInstance().init(mContext, contractAddress).getService().getAbi();
        Contract contract = Contract.load(mContext, abi, contractAddress);
        return contract.callContract(nameFunction, typeInputs, args);
    }

    public List callContract(String contractAddress, String nameFunction, String typeInputs, Object[] args, BigInteger value) throws Exception {
        String abi = ServiceManager.getInstance().init(mContext, contractAddress).getService().getAbi();
        Contract contract = Contract.load(mContext, abi, contractAddress);
        return contract.callContract(nameFunction, typeInputs, args, value);
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

    private void showLoading(Action.Callback callback) {
        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
        intent.putExtra("screen", Constants.SCREEN.LOADING);
        mContext.startActivity(intent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onCallback();
            }
        }, 500);
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
        }

        return content.toString();
    }

    private class DownloadContent extends AsyncTask<String, Void, String> {
        String content;

        protected String doInBackground(String... urls) {
            String content = handleTokenUri(urls[0]);
            return content;
        }

        protected void onPostExecute(String result) {
            this.content = result;
        }
    }

    private BigInteger getNonce() {
        BigInteger nonce = BigInteger.ONE;

        try {
            String address = WalletUtils.getInstance().init(mContext).getAddress();
//            EthBlock blockNumber = web3.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).sendAsync().get();
//            BigInteger block = blockNumber.getBlock().getNumber().add(new BigInteger("5"));

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

    private CoinType getCoinType(String chainId) {
        switch (chainId) {
            case "97":
            case "56":
                return CoinType.SMARTCHAIN;
            case "1":
                return CoinType.ETHEREUM;
            default:
                return null;
        }
    }
}
