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
import com.chainverse.sdk.blockchain.ERC20;
import com.chainverse.sdk.blockchain.HandleContract;
import com.chainverse.sdk.blockchain.MarketServiceV1;
import com.chainverse.sdk.common.CallbackToGame;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.listener.OnEmitterListenter;
import com.chainverse.sdk.manager.ContractManager;
import com.chainverse.sdk.manager.TransferItemManager;
import com.chainverse.sdk.model.MarketItem.ChainverseItemMarket;
import com.chainverse.sdk.model.NFT.NFT;
import com.chainverse.sdk.network.RESTful.RESTfulClient;
import com.chainverse.sdk.ui.ChainverseSDKActivity;
import com.chainverse.sdk.model.SignerData;
import com.chainverse.sdk.wallet.chainverse.ChainverseConnect;
import com.chainverse.sdk.wallet.chainverse.ChainverseResult;
import com.chainverse.sdk.wallet.trust.TrustConnect;
import com.chainverse.sdk.wallet.trust.TrustResult;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.Tuple;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple5;
import org.web3j.tx.Contract;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wallet.core.jni.HDWallet;


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

        exceptionSDK();
        checkContract();
        receiverCreatedWallet();
        setupBouncyCastle();
    }

    public void init(Activity activity, ChainverseCallback callback) {
        this.mCallback = callback;
        this.mContext = activity;
    }

    public void init(Activity activity) {
        this.mContext = activity;
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
                        System.out.println("get game item" + jsonElement);
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

    public void getItemOnMarket(int page, int pageSize, String name) {
        RESTfulClient.getItemOnMarket(ChainverseSDK.gameAddress, page, pageSize, name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    if (Utils.getErrorCodeResponse(jsonElement) == 0) {
                        Gson gson = new Gson();

                        ArrayList<ChainverseItemMarket> items = gson.fromJson(jsonElement.getAsJsonObject().get("data").getAsJsonObject().get("rows"), new TypeToken<ArrayList<ChainverseItemMarket>>() {
                        }.getType());
                        CallbackToGame.onGetItemMarket(items);
                    } else {
                        CallbackToGame.onError(ChainverseError.ERROR_REQUEST_ITEM);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    CallbackToGame.onError(ChainverseError.ERROR_REQUEST_ITEM);
                });
    }

    public void getMyAsset() {
        RESTfulClient.getMyAsset()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(jsonElement -> {
                    if (Utils.getErrorCodeResponse(jsonElement) == 0) {
                        Gson gson = new Gson();

                        System.out.println("my assset " + jsonElement);
                        ArrayList<ChainverseItemMarket> items = gson.fromJson(jsonElement.getAsJsonObject().get("data").getAsJsonObject().get("rows"), new TypeToken<ArrayList<ChainverseItemMarket>>() {
                        }.getType());
                        CallbackToGame.onGetMyAssets(items);
                    } else {
                        CallbackToGame.onError(ChainverseError.ERROR_REQUEST_ITEM);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    CallbackToGame.onError(ChainverseError.ERROR_REQUEST_ITEM);
                });
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

    private void doConnectSuccess() {
        if (isUserConnected()) {

//            RESTfulClient.getNonce()
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(jsonElement -> {
//                        JsonObject res = jsonElement.getAsJsonObject();
//                        System.out.println("asdfsadf " + res.has("data"));
//                        if (res.has("data")) {
//                            String message = res.getAsJsonObject("data").get("message").getAsString();
//                            try {
//                                System.out.println("run herer111");
//                                EncryptPreferenceUtils encryptPreferenceUtils = EncryptPreferenceUtils.getInstance().init(mContext);
//                                encryptPreferenceUtils.setNonceSignature(res.getAsJsonObject("data").toString());
//                                encryptPreferenceUtils.setXUserSignatureMarket(WalletUtils.getInstance().init(mContext).signMessage(message));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        System.out.println("run herer111");
//                    }, throwable -> {
//                        System.out.println("error get nonce " + throwable);
//                    });

            try {
                encryptPreferenceUtils.setXUserSignatureMarket(WalletUtils.getInstance().init(mContext).signMessage("ChainVerse"));
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public void callContract() {
        try {
            Web3j web3 = Web3j.build(new HttpService(Constants.URL.urlBlockchain));

            Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair());

//            MarketServiceV1 contract = MarketServiceV1.load(Constants.CONTRACT.MarketService, web3, dummyCredentials, new DefaultGasProvider());
//            RemoteFunctionCall<Tuple2> output = contract.getByNFT("0x2bB0966B95Bf340C76a10b4D2e6364Da5A303F15", new BigInteger("1221"));
//
//            MarketServiceV1.Auction info = (MarketServiceV1.Auction) output.sendAsync().get().component1();
//
//            System.out.println("ruybn erer " + info.currency);
            HandleContract handleContract = HandleContract.load(Constants.CONTRACT.MarketService, Constants.TEST_ABI, web3, dummyCredentials);
            RemoteFunctionCall<Tuple2> output = handleContract.callFunc("nfts", Arrays.asList("0x2bB0966B95Bf340C76a10b4D2e6364Da5A303F15"));
//
            Boolean param = (Boolean) output.sendAsync().get().component1();
            System.out.println("run here " + param);
//            String uri = output.sendAsync();

        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ChainverseItemMarket getNFT(String nft, BigInteger tokenId) {
        try {
            ContractManager contract = new ContractManager(mContext);
            ChainverseItemMarket nftInfo = contract.getNFT(nft, tokenId);
            return nftInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void buyNFT(String currency, Long listing_id, Double price, boolean isAuction) {
        if (isUserConnected()) {
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

    public void approvedToken(BigInteger amout) {
        String address = WalletUtils.getInstance().init(mContext).getAddress();

        try {
            Web3j web3 = Web3j.build(new HttpService(Constants.URL.urlBlockchain));

            Credentials credential = WalletUtils.getInstance().init(mContext).getCredential();
//            Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair());

            RawTransactionManager rawTransactionManager = new RawTransactionManager(web3, credential, 97);

            ERC20 erc20 = ERC20.load("0x672021e3c741910896cad6D6121446a328ba5634", web3, credential, new BigInteger("10000000000"), new BigInteger("80000"));

            RemoteFunctionCall<TransactionReceipt> receiptRemoteFunctionCall = (RemoteFunctionCall<TransactionReceipt>) erc20.approve(Constants.CONTRACT.MarketService, new BigInteger("10"));
            TransactionReceipt receipt = receiptRemoteFunctionCall.sendAsync().get();

            String tx = receipt.getTransactionHash();

            System.out.println(tx);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

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
