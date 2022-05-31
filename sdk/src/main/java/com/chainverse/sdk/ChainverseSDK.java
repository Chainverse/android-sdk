package com.chainverse.sdk;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.chainverse.sdk.base.web3.BaseWeb3;
import com.chainverse.sdk.blockchain.HandleContract;
import com.chainverse.sdk.common.BroadcastUtil;
import com.chainverse.sdk.common.CallbackToGame;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.listener.Action;
import com.chainverse.sdk.listener.OnEmitterListenter;
import com.chainverse.sdk.manager.ContractManager;
import com.chainverse.sdk.manager.ServiceManager;
import com.chainverse.sdk.manager.TransferItemManager;
import com.chainverse.sdk.model.MarketItem.Currency;
import com.chainverse.sdk.model.MessageNonce;
import com.chainverse.sdk.model.NFT.InfoSell;
import com.chainverse.sdk.model.NFT.NFT;
import com.chainverse.sdk.model.Params.FilterMarket;
import com.chainverse.sdk.model.SignerData;
import com.chainverse.sdk.model.TransactionData;
import com.chainverse.sdk.model.service.ChainverseService;
import com.chainverse.sdk.model.service.Token;
import com.chainverse.sdk.network.RESTful.RESTfulClient;
import com.chainverse.sdk.ui.ChainverseSDKActivity;
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
import org.bouncycastle.util.encoders.Hex;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.methods.response.EthCall;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import wallet.core.jni.AnyAddress;
import wallet.core.jni.CoinType;
import wallet.core.jni.HDWallet;
import wallet.core.jni.PrivateKey;
import wallet.core.jni.StoredKey;


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
        receiverCreatedWallet();
        setupBouncyCastle();
        getServiceByGame();
    }

    public void init(String developerAddress, String gameAddress, Activity activity) {
        this.mContext = activity;
        this.gameAddress = gameAddress;
        this.developerAddress = developerAddress;
        encryptPreferenceUtils = EncryptPreferenceUtils.getInstance().init(mContext);

        exceptionSDK();
        receiverCreatedWallet();
        setupBouncyCastle();
        getServiceByGame();
    }

    private void receiverCreatedWallet() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION.CREATED_WALLET);
        receiverCreatedWallet = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.ACTION.CREATED_WALLET)) {
                    encryptPreferenceUtils.setConnectWallet(Constants.TYPE_IMPORT_WALLET.IMPORTED);
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
            WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);
            RESTfulClient.getItems(walletUtils.getAddress(), ChainverseSDK.gameAddress)
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
                        int count = jsonElement.getAsJsonObject().get("data").getAsJsonObject().get("count").getAsInt();
                        for (JsonElement el : data) {
                            Gson gson = new Gson();
                            NFT item = gson.fromJson(el, NFT.class);

                            InfoSell infoSell = gson.fromJson(el.getAsJsonObject().get("auctions").getAsJsonArray().get(0), InfoSell.class);
                            item.setInfoSell(infoSell);

                            items.add(item);
                        }

                        CallbackToGame.onGetListItemMarket(items, count);
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
                            ServiceManager serviceManager = ServiceManager.getInstance().init(mContext);

                            if (service.getNetworkInfo() != null && service.getNetworkInfo().getRpcs() != null) {
                                ArrayList<String> rpcs = gson.fromJson(service.getNetworkInfo().getRpcs(), new TypeToken<ArrayList<String>>() {
                                }.getType());
                                serviceManager.setRPC(rpcs.get(0));
                            }

                            checkContract();
                            serviceManager.checkRPC();
                        } else {
                            CallbackToGame.onError(ChainverseError.ERROR_SERVICE_NOT_FOUND);
                        }
                    } else {
                        CallbackToGame.onError(ChainverseError.ERROR_SERVICE_NOT_FOUND);
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    CallbackToGame.onError(ChainverseError.ERROR_SERVICE_NOT_FOUND);
                    checkContract();
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
                        if (jsonElement.getAsJsonObject().get("data").getAsJsonObject().get("auctions").getAsJsonArray().size() > 0) {
                            InfoSell infoSell = gson.fromJson(jsonElement.getAsJsonObject().get("data").getAsJsonObject().get("auctions").getAsJsonArray().get(0), InfoSell.class);
                            infoNft.setInfoSell(infoSell);
                        }
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
            ContractManager contract = ContractManager.getInstance().init(mContext);
            NFT nftInfo = contract.getNFT(nft, tokenId);
            return nftInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void checkContract() {
        ContractManager checkContract = ContractManager.getInstance().init(mContext, new ContractManager.Listener() {
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
        if (isKeepConnect) {
            doConnectSuccess();
        } else {
            encryptPreferenceUtils.clearXUserAddress();
            encryptPreferenceUtils.clearXUserSignature();
        }
        isInitSDK = true;
    }

    private Boolean isInitSDKSuccess() {
        if (!isInitSDK) {
            CallbackToGame.onError(ChainverseError.ERROR_WAITING_INIT_SDK);
            return false;
        }
        return true;
    }

    private void setAccessToken() {
        String typeConnect = encryptPreferenceUtils.getConnectWallet();
        if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
            WalletUtils walletUtils = new WalletUtils().init(mContext);
            encryptPreferenceUtils.setXUserSignature(walletUtils.signPersonalMessage("ChainVerse"));
            encryptPreferenceUtils.clearXUserMessageNonce();
            RESTfulClient.getNonce()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(jsonElement -> {
                        if (Utils.getErrorCodeResponse(jsonElement) == 0) {
                            Gson gson = new Gson();
                            MessageNonce messageNonce = gson.fromJson(jsonElement.getAsJsonObject().get("data"), new TypeToken<MessageNonce>() {
                            }.getType());

                            try {
                                EncryptPreferenceUtils encryptPreferenceUtils = EncryptPreferenceUtils.getInstance().init(mContext);

                                encryptPreferenceUtils.clearXUserMessageNonce();

                                String messageSigned = walletUtils.signPersonalMessage(messageNonce.getMessage());

                                messageNonce.setMessage(messageSigned);

                                encryptPreferenceUtils.setXUserMessageNonce(messageNonce);

                                socketListener();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, throwable -> {
                        System.out.println("error get nonce " + throwable);
                    });
        } else if (typeConnect.equals(Constants.TYPE_IMPORT_WALLET.CHAINVERSE)) {
            MessageNonce messageNonce = encryptPreferenceUtils.getXUserMessageNonce();
            if (messageNonce != null) {
                Calendar cal = Calendar.getInstance();
                long time = messageNonce.getTime() * 1000L;
                cal.setTimeInMillis(time);
                cal.add(Calendar.HOUR, 24);
                long diff = cal.getTime().getTime() - new Date().getTime();
                if (diff <= 0) {
                    this.logout();
                    CallbackToGame.onError(ChainverseError.EXPIRED_NONCE);
                }
            } else {
                this.logout();
                CallbackToGame.onError(ChainverseError.EXPIRED_NONCE);
            }
        }
    }

    private void doConnectSuccess() {
        if (isUserConnected()) {
            WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);
            CallbackToGame.onConnectSuccess(walletUtils.getAddress());
            setAccessToken();
        }
    }

    private void socketListener() {
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

    @Override
    public String getVersion() {
        return ChainverseVersion.BUILD;
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (encryptPreferenceUtils.getConnectWallet().equals(Constants.TYPE_IMPORT_WALLET.TRUST)) {
            String action = TrustResult.getAction(intent);
            switch (action) {
                case "get_accounts":
                    String xUserAddress = TrustResult.getUserAddress(intent);
                    encryptPreferenceUtils.setXUserAddress(xUserAddress);
                    doConnectSuccess();
                    break;
            }
        } else {
            String action = ChainverseResult.getAction(intent);
            String data = ChainverseResult.handleConnect(intent);
            String id = ChainverseResult.getId(intent);

            if ("account_sign_message".equals(action)) {
                String xUserAddress = ChainverseResult.getUserAddress(intent);
                String time = ChainverseResult.getTime(intent);
                String nonce = ChainverseResult.getNonce(intent);
                List<String> signatures = ChainverseResult.getMultiUserSignature(intent);

                MessageNonce messageNonce = new MessageNonce();
                messageNonce.setMessage(signatures.get(0));
                messageNonce.setNonce(Integer.parseInt(nonce));
                messageNonce.setTime(Integer.parseInt(time));

                encryptPreferenceUtils.setXUserAddress(xUserAddress);
                encryptPreferenceUtils.setXUserSignature(signatures.get(1));
                encryptPreferenceUtils.setXUserMessageNonce(messageNonce);
                encryptPreferenceUtils.setConnectWallet(Constants.TYPE_IMPORT_WALLET.CHAINVERSE);
                doConnectSuccess();
            } else if (Constants.EFunction.approveToken.toString().equals(action) ||
                    Constants.EFunction.buyNFT.toString().equals(action) ||
                    Constants.EFunction.bidNFT.toString().equals(action) ||
                    Constants.EFunction.approveNFT.toString().equals(action) ||
                    Constants.EFunction.cancelSell.toString().equals(action) ||
                    Constants.EFunction.moveService.toString().equals(action) ||
                    Constants.EFunction.sell.toString().equals(action) ||
                    Constants.EFunction.transferItem.toString().equals(action) ||
                    Constants.EFunction.withdrawItem.toString().equals(action)) {
                if (id.equals("1")) {
                    CallbackToGame.onSignTransaction(Enum.valueOf(Constants.EFunction.class, action), data);
                } else {
                    CallbackToGame.onTransact(Enum.valueOf(Constants.EFunction.class, action), data);
                }
            } else if ("sdk_sign_message".equals(action)) {
                String signedMessage = ChainverseResult.getSignature(intent);
                CallbackToGame.onSignMessage(signedMessage);
            }

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
        encryptPreferenceUtils.setConnectWallet(Constants.TYPE_IMPORT_WALLET.TRUST);
        TrustConnect trust = new TrustConnect.Builder().build();
        trust.connect(mContext);
    }

    @Override
    public void connectWithChainverse() throws Exception {
        if (!isInitSDKSuccess()) {
            throw new Exception("SDK initial failed");
        }

        if (isUserConnected()) {
            throw new Exception("Wallet already connected");
        }

        if (Utils.isChainverseInstalled(mContext)) {
            encryptPreferenceUtils.clearXUserMessageNonce();
            ChainverseConnect chainverse = new ChainverseConnect.Builder().build(mContext);
            chainverse.signMessageAndAccount(true, "", "ChainVerse");
        }
    }

    @Override
    public void logout() {
        if (!isInitSDKSuccess()) {
            return;
        }

        WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);
        CallbackToGame.onLogout(walletUtils.getAddress());
        walletUtils.deleteStoredKey();
        encryptPreferenceUtils.clearXUserAddress();
        encryptPreferenceUtils.clearXUserSignature();
        encryptPreferenceUtils.clearMnemonic();
        encryptPreferenceUtils.clearConnectWallet();
        encryptPreferenceUtils.clearPathStoredKey();
        if (transferItemManager != null) {
            transferItemManager.disConnect();
        }
    }

    @Override
    public Boolean isUserConnected() {
        if (!WalletUtils.getInstance().init(mContext).getAddress().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public ChainverseUser getUser() {
        if (isUserConnected()) {
            ChainverseUser info = new ChainverseUser();
            WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);
            info.setAddress(walletUtils.getAddress());
            if (encryptPreferenceUtils.getXUserMessageNonce() != null) {
                info.setSignature(encryptPreferenceUtils.getXUserMessageNonce().getMessage());
            }
            return info;
        }
        return null;
    }

    @Override
    public BigDecimal getBalance() {
        try {
            WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);
            return BaseWeb3.getInstance().init(mContext).getBalance(walletUtils.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal getBalanceToken(String contractAddress) {
        try {
            WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);
            ContractManager contract = ContractManager.getInstance().init(mContext);
            return contract.balanceOf(contractAddress, walletUtils.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getAddress() {
        return WalletUtils.getInstance().init(mContext).getAddress();
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
    public void signMessage(String message, boolean isPersonal) {
        if (encryptPreferenceUtils.getConnectWallet().equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
            Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.LOADING);
            mContext.startActivity(intent);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    TransactionData transactionData = new TransactionData();
                    transactionData.setType(Constants.EFunction.signMessage);
                    transactionData.setMessage(message);
                    transactionData.setFrom(getAddress());

                    BroadcastUtil.send(mContext, Constants.ACTION.DIMISS_LOADING);

                    Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
                    intent.putExtra("isPersonal", isPersonal);
                    intent.putExtra("transactionData", transactionData);
                    intent.putExtra("screen", Constants.SCREEN.CONFIRM_TRANSACTION);
                    mContext.startActivity(intent);
                }
            }, 500);


        } else if (encryptPreferenceUtils.getConnectWallet().equals(Constants.TYPE_IMPORT_WALLET.CHAINVERSE)) {
            ChainverseConnect chainverseConnect = new ChainverseConnect.Builder().build(mContext);
            chainverseConnect.signMessage(isPersonal, message);
        }

    }

    @Override
    public void signTransaction(String chainId, String gasPrice, String gasLimit, String
            toAddress, String amount) {
        if (encryptPreferenceUtils.getConnectWallet().equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
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
        } else if (encryptPreferenceUtils.getConnectWallet().equals(Constants.TYPE_IMPORT_WALLET.CHAINVERSE)) {
//            ChainverseConnect chainverseConnect = new ChainverseConnect.Builder().build(mContext);
//            chainverseConnect.signTransaction(Constants.EFunction.);
        }

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
        if (!isUserConnected()) {
            WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);
            boolean checkPermission = walletUtils.checkPermissionStorage();
            if (checkPermission) {
                Intent intent = new Intent(mContext, ChainverseSDKActivity.class);
                intent.putExtra("screen", Constants.SCREEN.WALLET);
                intent.putExtra("type", "normal");
                mContext.startActivity(intent);
            }
        } else {
            Toast.makeText(mContext, "Wallet is already created", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * @param strength
     * @return
     */
    @Override
    public String genMnemonic(int strength) {
        HDWallet wallet = new HDWallet(strength, "");
        return wallet.mnemonic();
    }

    @Override
    public boolean isValidMnemonic(String phrase) {
        String passphrase = "";

        StoredKey storedKey = StoredKey.importHDWallet(phrase, "", passphrase.getBytes(), CoinType.ETHEREUM);
        return storedKey.isMnemonic();
    }

    @Override
    public boolean isValidAddress(String address) {
        boolean isValid = AnyAddress.isValid(address, CoinType.ETHEREUM);
        return isValid;
    }

    @Override
    public String importWalletByMnemonic(String phrase) throws Exception {
        String seedPhrase = phrase;
        String passphrase = "";
        String address = null;

        ArrayList<CoinType> coins = new ArrayList<>();
        coins.add(CoinType.ETHEREUM);

        StoredKey storedKey = WalletUtils.getInstance().init(mContext).importWallet(seedPhrase, "", passphrase, coins);
        if (storedKey != null && storedKey.isMnemonic()) {
            HDWallet wallet = storedKey.wallet(passphrase.getBytes());
            address = wallet.getAddressForCoin(CoinType.ETHEREUM);
            doConnectSuccess();
        }
        return address;
    }

    @Override
    public String importWalletByPrivateKey(String privateKey) throws Exception {
        byte[] bytes = Hex.decode(privateKey.trim());
        String address = null;
        PrivateKey importPrivateKey = new PrivateKey(bytes);

        StoredKey storedKey = WalletUtils.getInstance().init(mContext).importWallet(importPrivateKey, "", "", CoinType.ETHEREUM);

        if (storedKey != null) {
            address = storedKey.account(0).address();
            doConnectSuccess();
        }
        return address;
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

    public String getAbi(String contractAddress) {
        ServiceManager serviceManager = ServiceManager.getInstance().init(mContext, contractAddress);
        return HandleContract.formatAbi(serviceManager.getService().getAbi());
    }

    public String formatAbi(String abi) {
        return HandleContract.formatAbi(abi);
    }

    public ChainverseService getServices() {
        encryptPreferenceUtils = EncryptPreferenceUtils.getInstance().init(mContext);
        ChainverseService chainverseService = encryptPreferenceUtils.getService();
        return chainverseService;
    }

    public double isApproved(String token, String owner, String spender) {
        BigInteger allowence;
        ContractManager contractManager = ContractManager.getInstance().init(mContext);

        allowence = contractManager.allowance(token, owner, spender);

        BigDecimal amountApporoved = org.web3j.utils.Convert.fromWei(new BigDecimal(allowence), contractManager.getWei(token));
        double priceDouble = amountApporoved != null ? Double.parseDouble(amountApporoved.toString()) : 0.0;

        return priceDouble;
    }

    public void approveToken(String token, String spender, double amount) {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        contractManager.approved(token, spender, amount);
    }

    public void buyNFT(String currency, BigInteger listingId, double price) {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        contractManager.buyNFT(currency, listingId, price);
    }

    public void bidNFT(String currency, BigInteger listingId, double price) {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        contractManager.bidNFT(currency, listingId, price);
    }

    public void sellNFT(String nft, BigInteger tokenId, double price, String currency) {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        contractManager.list(nft, tokenId, price, currency);
    }

    public void cancelSellNFT(BigInteger listingId) {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        contractManager.unlist(listingId);
    }

    public void approveNFT(String nft, BigInteger tokenId) {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        contractManager.approveNFT(nft, tokenId);
    }

    public void approveNFTForGame(String nft, BigInteger tokenId) {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        contractManager.approveNFTForGame(nft, tokenId);
    }

    public boolean isApproved(String nft, BigInteger tokenId) {
        boolean isChecked = false;
        ContractManager contractManager = ContractManager.getInstance().init(mContext);

        String allowence = contractManager.allowenceNFT(nft, tokenId);

        if (allowence.toLowerCase().equals(Constants.CONTRACT.MarketService.toLowerCase())) {
            isChecked = true;
        }

        return isChecked;
    }

    public void approveNFTForService(String nft, String service, BigInteger tokenId) {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        contractManager.approveNFTForService(nft, service, tokenId);
    }

    public boolean isApprovedForService(String nft, String service, BigInteger tokenId) {
        boolean isChecked = false;
        ContractManager contractManager = ContractManager.getInstance().init(mContext);

        String allowence = contractManager.allowenceNFT(nft, tokenId);

        if (allowence.toLowerCase().equals(service.toLowerCase())) {
            isChecked = true;
        }

        return isChecked;
    }


    public void withdrawNFT(String nft, BigInteger tokenId) {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        contractManager.withdrawNFT(nft, tokenId);
    }

    public void moveItemToGame(String nft, BigInteger tokenId) {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        contractManager.moveItemToGame(nft, tokenId);
    }

    public void moveItemToService(String nft, String service, BigInteger tokenId) {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        contractManager.moveService(nft, service, tokenId);
    }

    public void transferItem(String to, String nft, BigInteger tokenId) {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        WalletUtils walletUtils = WalletUtils.getInstance().init(mContext);
        String account = walletUtils.getAddress();
        if (account != null || !account.isEmpty()) {
            contractManager.transferItem(account, to, nft, tokenId);
        } else {
            CallbackToGame.onErrorTransaction(Constants.EFunction.transferItem, "Can not find user's address");
        }
    }

    public boolean checkAddress(String address, String chainId) {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        return contractManager.checkAddress(address, chainId);
    }

    public double estimateGasDefault(Constants.EFunction function, List inputs) throws
            Exception {
        double fee;
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        try {
            fee = contractManager.estimateGasDefault(function, inputs);
        } catch (Exception e) {
            throw e;
        }
        return fee;
    }

    public List callContract(String contractAddress, String nameFunction, Object[] args) throws Exception {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        return contractManager.callContract(contractAddress, nameFunction, args);
    }

    public List callContract(String contractAddress, String nameFunction, Object[] args, BigInteger value) throws Exception {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        return contractManager.callContract(contractAddress, nameFunction, args, value);
    }

    public List callContract(String contractAddress, String nameFunction, String typeInputs, Object[] args) throws Exception {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        return contractManager.callContract(contractAddress, nameFunction, typeInputs, args);
    }

    public List callContract(String contractAddress, String nameFunction, String typeInputs, Object[] args, BigInteger value) throws Exception {
        ContractManager contractManager = ContractManager.getInstance().init(mContext);
        return contractManager.callContract(contractAddress, nameFunction, typeInputs, args, value);
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
