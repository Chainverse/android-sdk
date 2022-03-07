package com.chainverse.sdk.common;

import android.content.Context;
import android.os.Environment;

import com.chainverse.sdk.listener.OnWalletListener;
import com.google.protobuf.ByteString;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Sign;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import wallet.core.java.AnySigner;
import wallet.core.jni.CoinType;
import wallet.core.jni.HDWallet;
import wallet.core.jni.PrivateKey;
import wallet.core.jni.StoredKey;
import wallet.core.jni.proto.Ethereum;

public class WalletUtils {
    private static WalletUtils instance;
    private Context context;
    private EncryptPreferenceUtils encryptPreferenceUtils;
    private StoredKey storedKey;

    public static WalletUtils getInstance() {
        if (instance == null) {
            synchronized (WalletUtils.class) {
                if (instance == null) {
                    instance = new WalletUtils();
                }
            }
        }
        return instance;
    }

    public WalletUtils init(Context context) {
        this.context = context;
        encryptPreferenceUtils = EncryptPreferenceUtils.getInstance().init(context);
        String path_keystore = encryptPreferenceUtils.getPathStoredKey();
        storedKey = StoredKey.load(path_keystore);

        return this;
    }

    public StoredKey getStoredKey() {
        return this.storedKey;
    }

    public void createWallet(int strength, String passphrase, OnWalletListener onWalletListener) {
        HDWallet wallet = new HDWallet(strength, passphrase);
        String seedPhrase = wallet.mnemonic();

        ArrayList<CoinType> coins = new ArrayList<>();
        coins.add(CoinType.ETHEREUM);

        this.importWallet(seedPhrase, "", passphrase, coins);
        encryptPreferenceUtils.setMnemonic(wallet.mnemonic());
        onWalletListener.onCreated();
    }

    public void importWallet(String phrase, OnWalletListener onWalletListener) {
        String seedPhrase = phrase;
        String passphrase = "";

        ArrayList<CoinType> coins = new ArrayList<>();
        coins.add(CoinType.ETHEREUM);

        StoredKey storedKey = this.importWallet(seedPhrase, "", passphrase, coins);
        if (storedKey != null && storedKey.isMnemonic()) {
            HDWallet wallet = storedKey.wallet(passphrase.getBytes());
            encryptPreferenceUtils.setMnemonic(wallet.mnemonic());
            onWalletListener.onImported();
        } else {
            onWalletListener.onImportedFailed();
        }
    }

    public String getMnemonic() {
        if (storedKey == null) {
            return null;
        }
        return storedKey.decryptMnemonic("".getBytes());
    }

    public String getAddress() {
        String address = "";
        if (encryptPreferenceUtils.getConnectWallet().equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
            if (storedKey != null && storedKey.accountCount() > 0) {
                address = storedKey.account(0).address();
            }
        } else {
            address = encryptPreferenceUtils.getXUserAddress();
        }
        return address;
    }

    public String getPrivateKey() {
        if (!encryptPreferenceUtils.getConnectWallet().equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
            return null;
        }
        if (storedKey == null) {
            return null;
        }
        String passphrase = "";
        CoinType coinType = CoinType.ETHEREUM;
        PrivateKey secretPrivateKey = storedKey.privateKey(coinType, passphrase.getBytes());
        return Utils.byteToHexString(secretPrivateKey.data());
    }

    public Credentials getCredential() {
        if (!encryptPreferenceUtils.getConnectWallet().equals(Constants.TYPE_IMPORT_WALLET.IMPORTED)) {
            return null;
        }
        if (storedKey == null) {
            return null;
        }
        String passphrase = "";
        PrivateKey secretPrivateKey = storedKey.privateKey(CoinType.ETHEREUM, passphrase.getBytes());
        Credentials credentials = Credentials.create(Utils.byteToHexString(secretPrivateKey.data()));
        return credentials;
    }

    public String signMessage(String message) throws Exception {
        if (storedKey == null) {
            return null;
        }
        String passphrase = "";
        CoinType coinType = CoinType.ETHEREUM;
        PrivateKey secretPrivateKey = storedKey.privateKey(CoinType.ETHEREUM, passphrase.getBytes());

        if (secretPrivateKey == null) {
            return null;
        }

        Credentials credentials = Credentials.create(Utils.byteToHexString(secretPrivateKey.data()));
        String rawMessage = message;
        byte[] hexMessage = message.getBytes();
        Sign.SignatureData sigData = Sign.signMessage(hexMessage, credentials.getEcKeyPair());

        byte[] sig = new byte[65];

        System.arraycopy(sigData.getR(), 0, sig, 0, 32);
        System.arraycopy(sigData.getS(), 0, sig, 32, 32);
        System.arraycopy(sigData.getV(), 0, sig, 64, 1);

        String signature = String.format("0x%s", Utils.byteToHexString(sig));
        System.out.println("signature " + signature);
        return signature;
    }

    public String signPersonalMessage(String message) {
        if (storedKey == null) {
            return null;
        }
        String signature = "";
        String passphrase = "";
        PrivateKey privateKey = storedKey.privateKey(CoinType.ETHEREUM, passphrase.getBytes());
        if (privateKey == null) {
            return null;
        }

        String signMessage = "\u0019Ethereum Signed Message:\n" + message.length() + message;

        byte[] hash = signMessage.getBytes();

        Credentials credentials = Credentials.create(Utils.byteToHexString(privateKey.data()));
        Sign.SignatureData sigData = Sign.signMessage(hash, credentials.getEcKeyPair());

        byte[] sig = new byte[65];

        System.arraycopy(sigData.getR(), 0, sig, 0, 32);
        System.arraycopy(sigData.getS(), 0, sig, 32, 32);
        System.arraycopy(sigData.getV(), 0, sig, 64, 1);

        signature = String.format("0x%s", Utils.byteToHexString(sig));

        return signature;
    }

    public String signTransaction(String chainId, String gasPrice, String gasLimit, String toAddress, String amount) throws Exception {
        if (storedKey == null) {
            return null;
        }
        String passphrase = "";
        PrivateKey secretPrivateKey = storedKey.privateKey(CoinType.ETHEREUM, passphrase.getBytes());

        if (secretPrivateKey == null) {
            return null;
        }

        Ethereum.Transaction.Transfer transfer = Ethereum.Transaction.Transfer.newBuilder()
                .setAmount(ByteString.copyFrom(new BigDecimal(amount).toBigInteger().toByteArray()))
                .build();
        Ethereum.Transaction transaction = Ethereum.Transaction.newBuilder()
                .setTransfer(transfer)
                .build();
        Ethereum.SigningInput signerInput = Ethereum.SigningInput.newBuilder()
                .setChainId(ByteString.copyFrom(new BigInteger(chainId).toByteArray()))
                .setGasPrice(ByteString.copyFrom(new BigInteger(gasPrice).toByteArray()))
                .setGasLimit(ByteString.copyFrom(new BigInteger(gasLimit).toByteArray()))
                .setToAddress(toAddress)
                .setTransaction(transaction)
                .setPrivateKey(ByteString.copyFrom(secretPrivateKey.data()))
                .build();

        Ethereum.SigningOutput output = AnySigner.sign(signerInput, CoinType.ETHEREUM, Ethereum.SigningOutput.parser());
        String signature = Utils.byteToHexString(output.getEncoded().toByteArray());
        return signature;
    }

    public StoredKey importWallet(String phrase, String name, String encryptPassword, ArrayList<CoinType> coins) {
        CoinType coin = coins.get(0) != null ? coins.get(0) : CoinType.ETHEREUM;
        StoredKey storedKey = StoredKey.importHDWallet(phrase, name, encryptPassword.getBytes(), coin);
        storedKey.store(getPath());
        encryptPreferenceUtils.setPathStoredKey(getPath());
        return storedKey;
    }

    public StoredKey importWallet(PrivateKey privateKey, String name, String password, CoinType coin) {
        StoredKey storedKey = StoredKey.importPrivateKey(privateKey.data(), name, password.getBytes(), coin);
        storedKey.store(getPath());
        encryptPreferenceUtils.setPathStoredKey(getPath());
        return storedKey;
    }

    private String getPath() {
        File dir = new File(this.context.getApplicationInfo().dataDir + "/" + Constants.PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File store_key = new File(dir, "stored-key");
        if (!store_key.exists()) {
            try {
                store_key.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return store_key.getPath();
    }
}
