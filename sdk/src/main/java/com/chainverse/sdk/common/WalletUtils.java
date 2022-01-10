package com.chainverse.sdk.common;

import android.content.Context;

import com.chainverse.sdk.listener.OnWalletListener;
import com.google.protobuf.ByteString;

import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Sign;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import wallet.core.java.AnySigner;
import wallet.core.jni.CoinType;
import wallet.core.jni.HDWallet;
import wallet.core.jni.Hash;
import wallet.core.jni.PrivateKey;
import wallet.core.jni.StoredKey;
import wallet.core.jni.proto.Ethereum;

public class WalletUtils {
    private static WalletUtils instance;
    private Context context;
    private EncryptPreferenceUtils encryptPreferenceUtils;

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
        return this;
    }

    public void createWallet(int strength, String passphrase, OnWalletListener onWalletListener) {
        HDWallet wallet = new HDWallet(strength, passphrase);
        encryptPreferenceUtils.setMnemonic(wallet.mnemonic());
        onWalletListener.onCreated();
    }

    public void importWallet(String phrase, OnWalletListener onWalletListener) {
        String seedPhrase = phrase;
        String passphrase = "";
        StoredKey storedKey = StoredKey.importHDWallet(seedPhrase, "", passphrase.getBytes(), CoinType.ETHEREUM);
        if (storedKey != null && storedKey.isMnemonic()) {
            HDWallet wallet = storedKey.wallet(passphrase.getBytes());
            encryptPreferenceUtils.setMnemonic(wallet.mnemonic());
            onWalletListener.onImported();
        } else {
            onWalletListener.onImportedFailed();
        }
    }

    public String getMnemonic() {
        return encryptPreferenceUtils.getMnemonic();
    }

    public String getAddress() {
        String seedPhrase = getMnemonic();
        String passphrase = "";
        HDWallet wallet = new HDWallet(seedPhrase, passphrase);
        CoinType coinType = CoinType.ETHEREUM;
        String address = wallet.getAddressForCoin(coinType);
        return address;
    }

    public String getPrivateKey() {
        String seedPhrase = getMnemonic();
        String passphrase = "";
        HDWallet wallet = new HDWallet(seedPhrase, passphrase);
        CoinType coinType = CoinType.ETHEREUM;
        PrivateKey secretPrivateKey = wallet.getKeyForCoin(coinType);
        return Utils.byteToHexString(secretPrivateKey.data());
    }

    public Credentials getCredential() {
        String seedPhrase = getMnemonic();
        String passphrase = "";
        HDWallet wallet = new HDWallet(seedPhrase, passphrase);
        PrivateKey secretPrivateKey = wallet.getKeyForCoin(CoinType.ETHEREUM);
        Credentials credentials = Credentials.create(Utils.byteToHexString(secretPrivateKey.data()));
        return credentials;
    }

    public String signMessage(String message) throws Exception {
        String seedPhrase = getMnemonic();
        String passphrase = "";
        HDWallet wallet = new HDWallet(seedPhrase, passphrase);
        CoinType coinType = CoinType.ETHEREUM;
        PrivateKey secretPrivateKey = wallet.getKeyForCoin(coinType);

        Credentials credentials = Credentials.create(Utils.byteToHexString(secretPrivateKey.data()));
        String rawMessage = message;
        byte[] hexMessage = message.getBytes();
        Sign.SignatureData sigData = Sign.signMessage(hexMessage, credentials.getEcKeyPair());

        byte[] sig = new byte[65];

        System.arraycopy(sigData.getR(), 0, sig, 0, 32);
        System.arraycopy(sigData.getS(), 0, sig, 32, 32);
        System.arraycopy(sigData.getV(), 0, sig, 64, 1);

        String signature = String.format("0x%s", Utils.byteToHexString(sig));
        return signature;
    }

    public String signPersonalMessage(String message) {
        String signature = "";
        String seedPhrase = getMnemonic();
        String passphrase = "";
        HDWallet wallet = new HDWallet(seedPhrase, passphrase);
        CoinType coinType = CoinType.ETHEREUM;
        PrivateKey privateKey = wallet.getKeyForCoin(coinType);

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
        String seedPhrase = getMnemonic();
        String passphrase = "";
        HDWallet wallet = new HDWallet(seedPhrase, passphrase);
        PrivateKey secretPrivateKey = wallet.getKeyForCoin(CoinType.ETHEREUM);

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
}
