package com.chainverse.sdk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.chainverse.sdk.base.web3.BaseWeb3;
import com.chainverse.sdk.common.CallbackToGame;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.listener.Action;
import com.chainverse.sdk.listener.OnEmitterListenter;
import com.chainverse.sdk.manager.ContractManager;
import com.chainverse.sdk.manager.TransferItemManager;
import com.chainverse.sdk.model.MarketItem.Currency;
import com.chainverse.sdk.model.MessageNonce;
import com.chainverse.sdk.model.NFT.InfoSell;
import com.chainverse.sdk.model.NFT.NFT;
import com.chainverse.sdk.model.Params.FilterMarket;
import com.chainverse.sdk.model.service.ChainverseService;
import com.chainverse.sdk.model.service.Token;
import com.chainverse.sdk.network.RESTful.RESTfulClient;
import com.chainverse.sdk.ui.ChainverseSDKActivity;
import com.chainverse.sdk.model.SignerData;
import com.chainverse.sdk.wallet.chainverse.ChainverseConnect;
import com.chainverse.sdk.wallet.chainverse.ChainverseResult;
import com.chainverse.sdk.wallet.trust.TrustConnect;
import com.chainverse.sdk.wallet.trust.TrustResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.EthCall;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class ChainverseSDK implements Chainverse {
    private static ChainverseSDK mInstance;
    public static String developerAddress;
    public static String gameAddress;
    public static String scheme;
    public static String callbackHost;
    public static ChainverseCallback mCallback;

    private boolean isKeepConnect = true;
    private Activity mContext;
    private EncryptPreferenceUtils encryptPreferenceUtils;
    private boolean isInitSDK = false;
    private BroadcastReceiver receiverCreatedWallet;
    private TransferItemManager transferItemManager;

    static {
        System.loadLibrary("TrustWalletCore");
    }

    public static ChainverseSDK getInstance() {
        if (mInstance == null) {
            synchronized (ChainverseSDK.class) {
                if (mInstance == null) {
                    mInstance = new ChainverseSDK();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void init(String developerAddress, String gameAddress, Activity activity, ChainverseCallback callback) {
        this.mCallback = callback;
        this.mContext = activity;
        this.gameAddress = gameAddress;
        this.developerAddress = developerAddress;
        encryptPreferenceUtils = EncryptPreferenceUtils.getInstance().init(mContext);

        getServiceByGame();
        exceptionSDK();
        checkContract();
        receiverCreatedWallet();
        setupBouncyCastle();
    }

    public void init(String developerAddress, String gameAddress, Activity activity) {
        this.mContext = activity;
        this.gameAddress = gameAddress;
        this.developerAddress = developerAddress;
        encryptPreferenceUtils = EncryptPreferenceUtils.getInstance().init(mContext);

        getServiceByGame();
        exceptionSDK();
        checkContract();
        receiverCreatedWallet();
        setupBouncyCastle();
    }

    private void receiverCreatedWallet() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION.CREATED_WALLET);
        receiverCreatedWallet = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.ACTION.CREATED_WALLET)) {
                    doConnectSuccess();
                }
            }
        };
        LocalBroadcastManager.getInstance(mContext).registerReceiver(receiverCreatedWallet, filter);
    }


    @Override
    public void setKeepConnect(boolean keep) {
        isKeepConnect = keep;
    }

    private void exceptionSDK() {
        ChainverseExeption.developerAddressExeption();
        ChainverseExeption.gameAddressExeption();
    }

    @Override
    public void setScheme(String scheme) {
        ChainverseSDK.scheme = scheme;
    }

    @Override
    public void setHost(String host) {
        callbackHost = host;
    }

    @Override
    public void getItems() {
        if (!isInitSDKSuccess()) {
            return;
        }
        if (isUserConnected()) {
            RESTfulClient.getItems(encryptPreferenceUtils.getXUserAddress(), ChainverseSDK.gameAddress)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(jsonElement -> {
                        if (Utils.getErrorCodeResponse(jsonElement) == 0) {
                            Gson gson = new Gson();
                            ArrayList<ChainverseItem> items = gson.fromJson(jsonElement.getAsJsonObject().get("items"), new TypeToken<ArrayList<ChainverseItem>>() {
                            }.getType());
                            CallbackToGame.onGetItems(items);
                        } else {
                            CallbackToGame.onError(ChainverseError.ERROR_REQUEST_ITEM);
                        }

                    }, throwable -> {
                        throwable.printStackTrace();
                        CallbackToGame.onError(ChainverseError.ERROR_REQUEST_ITEM);
                    });
        }
    }

    public void getListItemOnMarket(FilterMarket filterMarket) {
        RESTfulClient.getListItemOnMarket(ChainverseSDK.gameAddress, filterMarket).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    if (Utils.getErrorCodeResponse(jsonElement) == 0) {
                        ArrayList<NFT> items = new ArrayList<>();
                        JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonObject().get("rows").getAsJsonArray();
                        for (JsonElement el : data) {
                            Gson gson = new Gson();
                            NFT item = gson.fromJson(el, NFT.class);
                            InfoSell infoSell = gson.fromJson(el.getAsJsonObject().get("auctions").getAsJsonArray().get(0), InfoSell.class);
                            item.setInfoSell(infoSell);

                            items.add(item);
                        }

                        CallbackToGame.onGetListItemMarket(items);
                    } else {
                        CallbackToGame.onError(ChainverseError.ERROR_REQUEST_ITEM);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    CallbackToGame.onError(ChainverseError.ERROR_REQUEST_ITEM);
                });
    }

    public void getMyAsset() {
        RESTfulClient.getMyAsset(ChainverseSDK.gameAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    if (Utils.getErrorCodeResponse(jsonElement) == 0) {
                        ArrayList<NFT> items = new ArrayList<>();
                        JsonArray data = jsonElement.getAsJsonObject().get("data").getAsJsonArray();
                        for (JsonElement el : data) {
                            Gson gson = new Gson();
                            NFT item = gson.fromJson(el, NFT.class);
                            if (el.getAsJsonObject().has("auctions")) {
                                InfoSell infoSell = gson.fromJson(el.getAsJsonObject().get("auctions").getAsJsonArray().get(0), InfoSell.class);
                                item.setInfoSell(infoSell);
                            }
                            items.add(item);
                        }

                        CallbackToGame.onGetMyAssets(items);
                    } else {
                        CallbackToGame.onError(ChainverseError.ERROR_REQUEST_ITEM);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    CallbackToGame.onError(ChainverseError.ERROR_REQUEST_ITEM);
                });
    }

    protected void getServiceByGame() {
        RESTfulClient.getServiceByGame(gameAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    if (Utils.getErrorCodeResponse(jsonElement) == 0) {
                        Gson gson = new Gson();

                        ChainverseService service = gson.fromJson(jsonElement.getAsJsonObject().get("data"), new TypeToken<ChainverseService>() {
                        }.getType());
                        if (service != null) {
                            encryptPreferenceUtils.setService(service);
                        } else {
                            CallbackToGame.onError(ChainverseError.ERROR_SERVICE_NOT_FOUND);
                        }
                    } else {
                        CallbackToGame.onError(ChainverseError.ERROR_SERVICE_NOT_FOUND);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    CallbackToGame.onError(ChainverseError.ERROR_SERVICE_NOT_FOUND);
                });
    }

    public void getDetailNFT(String nft, BigInteger tokenId) {
        RESTfulClient.getDetailNFT(nft, tokenId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    if (Utils.getErrorCodeResponse(jsonElement) == 0) {
                        Gson gson = new Gson();
                        NFT infoNft = gson.fromJson(jsonElement.getAsJsonObject().get("data"), NFT.class);
                        InfoSell infoSell = gson.fromJson(jsonElement.getAsJsonObject().get("data").getAsJsonObject().get("auctions").getAsJsonArray().get(0), InfoSell.class);
                        infoNft.setInfoSell(infoSell);
                        CallbackToGame.onGetDetailItem(infoNft);
                    } else {
                        CallbackToGame.onError(ChainverseError.ERROR_REQUEST_ITEM);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    CallbackToGame.onError(ChainverseError.ERROR_REQUEST_ITEM);
                });
    }

    public NFT getNFT(String nft, BigInteger tokenId) {
        try {
            ContractManager contract = new ContractManager(mContext);
            NFT nftInfo = contract.getNFT(nft, tokenId);
            return nftInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void checkContract() {
        ContractManager checkContract = new ContractManager(mContext, new ContractManager.Listener() {
            @Override
            public void isChecked(boolean isCheck) {
                if (isCheck) {
                    CallbackToGame.onInitSDKSuccess();
                    doInit();
                } else {
                    CallbackToGame.onError(ChainverseError.ERROR_INIT_SDK);
                }
            }

        });
        checkContract.check();
    }


    private void doInit() {
        isInitSDK = true;
        if (isKeepConnect) {
            doConnectSuccess();
        } else {
            encryptPreferenceUtils.clearXUserAddress();
            encryptPreferenceUtils.clearXUserSignature();
        }

    }

    private Boolean isInitSDKSuccess() {
        if (!isInitSDK) {
            CallbackToGame.onError(ChainverseError.ERROR_WAITING_INIT_SDK);
            return false;
        }
        return true;
    }

    private void setAccessToken() {
        RESTfulClient.getNonce()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    if (Utils.getErrorCodeResponse(jsonElement) == 0) {
                        Gson gson = new Gson();
                        MessageNonce messageNonce = gson.fromJson(jsonElement.getAsJsonObject().get("data"), new TypeToken<MessageNonce>() {
                        }.getType());

                        try {
                            WalletUtils walletUtils = new WalletUtils().init(mContext);
                            EncryptPreferenceUtils encryptPreferenceUtils = EncryptPreferenceUtils.getInstance().init(mContext);

                            encryptPreferenceUtils.clearXUserMessageNonce();

                            String messageSigned = walletUtils.signPersonalMessage(messageNonce.getMessage());

                            messageNonce.setMessage(messageSigned);

                            encryptPreferenceUtils.setXUserMessageNonce(messageNonce);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, throwable -> {
                    System.out.println("error get nonce " + throwable);
                });
    }

    private void doConnectSuccess() {
        if (isUserConnected()) {
            setAccessToken();
            CallbackToGame.onConnectSuccess(encryptPreferenceUtils.getXUserAddress());

            transferItemManager = new TransferItemManager(mContext);
            transferItemManager.on(new OnEmitterListenter() {
                @Override
                public void call(String event, Object... args) {
                    switch (event) {
                        case "transfer_item_to_user":
                            if (args.length > 0) {
                                JsonElement jsonElement = new JsonParser().parse(args[0].toString());
                                Gson gson = new Gson();
                                ChainverseItem item = gson.fromJson(jsonElement.getAsJsonObject(), new TypeToken<ChainverseItem>() {
                                }.getType());
                                CallbackToGame.onItemUpdate(item, ChainverseItem.TRANSFER_ITEM_TO_USER);
                                getItems();
                            }

                            break;
                        case "transfer_item_from_user":
                            if (args.length > 0) {
                                JsonElement jsonElement = new JsonParser().parse(args[0].toString());
                                Gson gson = new Gson();
                                ChainverseItem item = gson.fromJson(jsonElement.getAsJsonObject(), new TypeToken<ChainverseItem>() {
                                }.getType());
                                CallbackToGame.onItemUpdate(item, ChainverseItem.TRANSFER_ITEM_FROM_USER);
                                getItems();
                            }
                            break;
                    }
                    LogUtil.log("socket_" + event, args);
                }
            });
            transferItemManager.connect();
        }
    }

    @Override
    public String getVersion() {
        return ChainverseVersion.BUILD;
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (encryptPreferenceUtils.getConnectWallet().equals("trust")) {
            String action = TrustResult.getAction(intent);
            switch (action) {
                case "get_accounts":
                    String xUserAddress = TrustResult.getUserAddress(intent);
                    encryptPreferenceUtils.setXUserAddress(xUserAddress);
                    doConnectSuccess();
                    break;
            }
        } else {
            String xUserAddress = ChainverseResult.getUserAddress(intent);
            String xUserSignature = ChainverseResult.getUserSignature(intent);
            encryptPreferenceUtils.setXUserAddress(xUserAddress);
            encryptPreferenceUtils.setXUserSignature(xUserSignature);
            doConnectSuccess();
        }
    }

    @Override
    public void showConnectView() {
        if (!isInitSDKSuccess()) {
            return;
        }
        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
        intent.putExtra("screen", Constants.SCREEN.CONNECT_VIEW);
        mContext.startActivity(intent);
    }

    @Override
    public void connectWithTrust() {
        if (!isInitSDKSuccess()) {
            return;
        }
        encryptPreferenceUtils.setConnectWallet("trust");
        TrustConnect trust = new TrustConnect.Builder().build();
        trust.connect(mContext);
    }

    @Override
    public void connectWithChainverse() {
        if (!isInitSDKSuccess()) {
            return;
        }

        if (Utils.isChainverseInstalled(mContext)) {
            encryptPreferenceUtils.setConnectWallet("chainverse");
            ChainverseConnect chainverse = new ChainverseConnect.Builder().build();
            chainverse.connect(mContext);
        }
    }

    @Override
    public void logout() {
        if (!isInitSDKSuccess()) {
            return;
        }
        CallbackToGame.onLogout(encryptPreferenceUtils.getXUserAddress());
        encryptPreferenceUtils.clearXUserAddress();
        encryptPreferenceUtils.clearXUserSignature();
        encryptPreferenceUtils.clearMnemonic();
        transferItemManager.disConnect();
    }

    @Override
    public Boolean isUserConnected() {
        if (!encryptPreferenceUtils.getXUserSignature().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public ChainverseUser getUser() {
        if (isUserConnected()) {
            ChainverseUser info = new ChainverseUser();
            info.setAddress(encryptPreferenceUtils.getXUserAddress());
            info.setSignature(encryptPreferenceUtils.getXUserSignature());
            return info;
        }
        return null;
    }

    @Override
    public BigDecimal getBalance() {
        try {
            return BaseWeb3.getInstance().init(mContext).getBalance(encryptPreferenceUtils.getXUserAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal getBalanceToken(String contractAddress) {
        try {
            ContractManager contract = new ContractManager(mContext);
            return contract.balanceOf(contractAddress, encryptPreferenceUtils.getXUserAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public EthCall callFunction(String address, String method, List<Type> inputParameters) {
        try {
            return BaseWeb3.getInstance().init(mContext).callFunction(address, method, inputParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void signMessage(String message) {
        SignerData data = new SignerData();
        data.setMessage(message);
        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("screen", Constants.SCREEN.CONFIRM_SIGN);
        bundle.putString("type", "message");
        bundle.putParcelable("data", data);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public void signTransaction(String chainId, String gasPrice, String gasLimit, String toAddress, String amount) {
        SignerData data = new SignerData();
        data.setChainId(chainId);
        data.setGasPrice(gasPrice);
        data.setGasLimit(gasLimit);
        data.setToAddress(toAddress);
        data.setAmount(amount);
        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("screen", Constants.SCREEN.CONFIRM_SIGN);
        bundle.putString("type", "transaction");
        bundle.putParcelable("data", data);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public String transfer(String to, BigDecimal amount) {
        try {
            BaseWeb3.getInstance().init(mContext).transfer(to, amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void showConnectWalletView() {
        Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
        intent.putExtra("screen", Constants.SCREEN.WALLET);
        intent.putExtra("type", "normal");
        mContext.startActivity(intent);
    }

    @Override
    public void showWalletInfoView() {
        if (isUserConnected()) {
            Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.WALLET_INFO);
            mContext.startActivity(intent);
        } else {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Đăng nhập!");
                alertDialog.setMessage("Bạn chưa đăng nhập");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Đăng nhập",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                showConnectWalletView();
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } catch (WindowManager.BadTokenException e) {
                System.out.println("error " + e);
            }
        }

    }

    @Override
    public void testBuy() {
        RESTfulClient.testBuy()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    LogUtil.log("nampv_testbuy", jsonElement.toString());

                }, throwable -> {
                    throwable.printStackTrace();
                });
    }

    public void publishNFT(String nft, BigInteger tokenId, Action.publishNFT publishNFT) {
        RESTfulClient.publishNFT(nft, tokenId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    if (Utils.getErrorCodeResponse(jsonElement) == 0) {
                        publishNFT.onSuccess();
                    } else {
                        Gson gson = new Gson();
                        String message = gson.fromJson(jsonElement.getAsJsonObject().get("message"), String.class);
                        publishNFT.onError(message);
                    }
                }, throwable -> {
                    publishNFT.onError(throwable.getMessage());
                });
    }

    public ArrayList<Currency> getListCurrencies() {
        ChainverseService chainverseService = encryptPreferenceUtils.getService();
        ArrayList<Token> tokens = chainverseService.getTokens();
        ArrayList<Currency> currencies = new ArrayList<>();

        for (Token token : tokens) {
            Currency currency = new Currency();
            currency.setCurrency(token.getAddress());
            currency.setSymbol(token.getSymbol());
            currency.setName(token.getName());
            currency.setDecimal(token.getDecimals());

            currencies.add(currency);
        }

        return currencies;
    }

    @Override
    public void buyNFT(String currency, Long listing_id, Double price, boolean isAuction) {
        if (isUserConnected()) {
            ChainverseService chainverseService = encryptPreferenceUtils.getService();
            if (chainverseService != null) {
                Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("screen", Constants.SCREEN.BUY_NFT);
                bundle.putString("currency", currency);
                bundle.putLong("listing_id", listing_id);
                bundle.putDouble("price", price);
                bundle.putBoolean("isAuction", isAuction);
                intent.putExtra("type", "buyNFT");
                intent.putExtras(bundle);

                mContext.startActivity(intent);
            } else {
                CallbackToGame.onError(ChainverseError.ERROR_SERVICE_NOT_FOUND);
            }
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
            alertDialog.setTitle("Đăng nhập!");
            alertDialog.setMessage("Bạn cần đăng nhập để mua");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Đăng nhập",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            showConnectWalletView();
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

    public BigInteger isApproved(String token, String owner, String spender) {
        BigInteger allowence = BigInteger.ZERO;
        ContractManager contractManager = new ContractManager(mContext);

        allowence = contractManager.allowance(token, owner, spender);

        return allowence;
    }

    public String approveToken(String token, String spender, double amount) throws Exception {
        String tx;
        ContractManager contractManager = new ContractManager(mContext);
        try {
            tx = contractManager.approved(token, spender, amount);
        } catch (Exception e) {
            throw e;
        }
        return tx;
    }

    public String buyNFT(String currency, BigInteger listingId, double price) throws Exception {
        String tx;
        ContractManager contractManager = new ContractManager(mContext);
        try {
            tx = contractManager.buyNFT(currency, listingId, price);
        } catch (Exception e) {
            throw e;
        }

        return tx;
    }

    public String bidNFT(String currency, BigInteger listingId, double price) throws Exception {
        String tx;
        ContractManager contractManager = new ContractManager(mContext);
        try {
            tx = contractManager.bidNFT(currency, listingId, price);
        } catch (Exception e) {
            throw e;
        }

        return tx;
    }

    public String sellNFT(String nft, BigInteger tokenId, double price, String currency) throws Exception {
        String tx;
        ContractManager contractManager = new ContractManager(mContext);
        try {
            tx = contractManager.list(nft, tokenId, price, currency);
        } catch (Exception e) {
            throw e;
        }

        return tx;
    }

    public String cancelSellNFT(BigInteger listingId) throws Exception {
        String tx;
        ContractManager contractManager = new ContractManager(mContext);
        try {
            tx = contractManager.unlist(listingId);
        } catch (Exception e) {
            throw e;
        }

        return tx;
    }

    public String approveNFT(String nft, BigInteger tokenId) {
        String tx = null;

        ContractManager contractManager = new ContractManager(mContext);

        try {
            tx = contractManager.approveNFT(nft, tokenId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tx;
    }

    public boolean isApproved(String nft, BigInteger tokenId) {
        boolean isChecked = false;
        ContractManager contractManager = new ContractManager(mContext);

        String allowence = contractManager.allowenceNFT(nft, tokenId);

        if (allowence.toLowerCase().equals(Constants.CONTRACT.MarketService.toLowerCase())) {
            isChecked = true;
        }

        return isChecked;
    }

    public String withdrawNFT(String nft, BigInteger tokenId) throws Exception {
        String tx;
        ContractManager contractManager = new ContractManager(mContext);
        try {
            tx = contractManager.withdrawNFT(nft, tokenId);
        } catch (Exception e) {
            throw e;
        }
        return tx;
    }

//    public String withdrawCVT(double amount) throws Exception {
//        String tx;
//        ContractManager contractManager = new ContractManager(mContext);
//        try {
//            tx = contractManager.withdrawCVT(amount);
//        } catch (Exception e) {
//            throw e;
//        }
//        return tx;
//    }
//
//    public String withdrawToken(String token, double amount) throws Exception {
//        String tx;
//        ContractManager contractManager = new ContractManager(mContext);
//        try {
//            tx = contractManager.withdrawToken(token, amount);
//        } catch (Exception e) {
//            throw e;
//        }
//        return tx;
//    }

    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            // Web3j will set up the provider lazily when it's first used.
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            // BC with same package name, shouldn't happen in real life.
            return;
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
}
