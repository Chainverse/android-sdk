package com.chainverse.sdk.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.ImageView;

import com.chainverse.sdk.ChainverseError;
import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.base.web3.BaseWeb3;
import com.chainverse.sdk.blockchain.ERC20;
import com.chainverse.sdk.blockchain.ERC721;
import com.chainverse.sdk.blockchain.HandleContract;
import com.chainverse.sdk.blockchain.MarketServiceV1;
import com.chainverse.sdk.common.CallbackToGame;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.Convert;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.model.MarketItem.ChainverseItemMarket;
import com.chainverse.sdk.model.MarketItem.Currency;
import com.chainverse.sdk.model.NFT.Auction;
import com.chainverse.sdk.model.NFT.Listing;
import com.chainverse.sdk.model.NFT.NFT;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Array;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Keys;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendRawTransaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
    public ChainverseItemMarket getNFT(String nft, BigInteger tokenId) throws Exception {
        ChainverseItemMarket item = new ChainverseItemMarket();

        try {
            Credentials dummyCredentials = Credentials.create(Keys.createEcKeyPair());
            MarketServiceV1 contract = MarketServiceV1.load(Constants.CONTRACT.MarketService, web3, dummyCredentials, new DefaultGasProvider());
            ERC721 contractERC721 = ERC721.load(nft, web3, dummyCredentials, new DefaultGasProvider());
            RemoteCall<String> remoteCallUri = contractERC721.tokenURI(tokenId);
            String uri = remoteCallUri.sendAsync().get();

            String content = handleTokenUri(uri);
//            String content = new DownloadContent().execute(uri).get();

            item.setTokenId(tokenId);
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
                item.setImage_preview((!asset.isEmpty()) ? asset : image);
                item.setName((String) json.get("name"));
                item.setAttributes(content);
            }

            RemoteFunctionCall<Tuple2<MarketServiceV1.Auction, MarketServiceV1.Listing>> data = contract.getByNFT(nft, tokenId);

            MarketServiceV1.Auction auctionInfo = data.sendAsync().get().component1();
            MarketServiceV1.Listing listingInfo = data.sendAsync().get().component2();

            int feeAuction = Integer.parseInt(auctionInfo.fee.toString(), 8);
            BigInteger bidDuration = new BigInteger(auctionInfo.bidDuration.toString(), 18);
            BigInteger bidEnd = new BigInteger(auctionInfo.end.toString(), 18);
            Auction auction = new Auction(auctionInfo.isEnded, auctionInfo.nft, auctionInfo.owner, auctionInfo.currency, auctionInfo.tokenId, feeAuction, auctionInfo.id, auctionInfo.winner, auctionInfo.bid, bidDuration, bidEnd);

            int feeListing = Integer.parseInt(listingInfo.fee.toString(), 8);
            Listing listing = new Listing(listingInfo.isEnded, listingInfo.nft, listingInfo.owner, listingInfo.currency, listingInfo.tokenId, feeListing, listingInfo.id, listingInfo.price);

            item.setAuctionInfo(auction);
            item.setListingInfo(listing);
            item.setAuction((auction.getId().equals(BigInteger.ZERO)) ? false : true);
            item.setPrice(0.0);
            item.setListingId(BigInteger.ZERO);
            if (!auction.getId().equals(BigInteger.ZERO)) {
                BigDecimal price = org.web3j.utils.Convert.fromWei(new BigDecimal(auction.getBid()), org.web3j.utils.Convert.Unit.ETHER);
                item.setListingId(auction.getId());
                item.setPrice(Double.parseDouble(price.toString()));
            }
            if (!listing.getId().equals(BigInteger.ZERO) && listing.getPrice() != null) {
                BigDecimal price = org.web3j.utils.Convert.fromWei(new BigDecimal(listing.getPrice()), org.web3j.utils.Convert.Unit.ETHER);
                item.setListingId(listing.getId());
                item.setPrice(Double.parseDouble(price.toString()));
            }

            return item;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
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

    public String approved(String token, String spender, BigInteger amout) {
        String tx = "";
        try {
            Credentials credentials = WalletUtils.getInstance().init(mContext).getCredential();
//            String address = WalletUtils.getInstance().init(mContext).getAddress();

//            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
//                    address, DefaultBlockParameterName.LATEST).sendAsync().get();
//
//            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
//
//            Transaction transaction = new Transaction(address, nonce, );

//            EthEstimateGas gas = web3.ethEstimateGas();

            System.out.println("run herer");
            ERC20 erc20 = ERC20.load(token, web3, credentials, new BigInteger("10000000000"), new BigInteger("80000"));

            RemoteCall<TransactionReceipt> receiptRemoteCall = (RemoteCall<TransactionReceipt>) erc20.approve(spender, amout);

            tx = receiptRemoteCall.sendAsync().get().getTransactionHash();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("run herer " + tx);

        return tx;
    }

    public String buyNFT(String currency, BigInteger listingId, BigInteger price) {
        String tx = null;
        try {
            String address = WalletUtils.getInstance().init(mContext).getAddress();
            Credentials credentials = WalletUtils.getInstance().init(mContext).getCredential();

            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                    address, DefaultBlockParameterName.LATEST).sendAsync().get();

            BigInteger nonce = ethGetTransactionCount.getTransactionCount();

            Function function = new Function("buy", Arrays.asList(new Uint256(listingId), new Uint256(price)), Collections.emptyList());

            String functionEncoder = FunctionEncoder.encode(function);

            RawTransactionManager rawTransactionManager = new RawTransactionManager(web3, credentials);

            BigInteger value = BigInteger.ZERO;

            if (currency.equals(Constants.CONTRACT.NativeCurrency)) {
                value = price;
            }
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, new BigInteger("10000000000"), new BigInteger("200000"), Constants.CONTRACT.MarketService, value, "0x" + functionEncoder);
            String signedTransaction = rawTransactionManager.sign(rawTransaction);

            EthSendTransaction sendRawTransaction = web3.ethSendRawTransaction(signedTransaction).sendAsync().get();

            tx = sendRawTransaction.getTransactionHash();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return tx;
    }

    public String BidNFT(String currency, BigInteger listingId, BigInteger price) {
        String tx = null;
        try {
            String address = WalletUtils.getInstance().init(mContext).getAddress();
            Credentials credentials = WalletUtils.getInstance().init(mContext).getCredential();

            EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                    address, DefaultBlockParameterName.LATEST).sendAsync().get();

            BigInteger nonce = ethGetTransactionCount.getTransactionCount();

            Function function = new Function("buy", Arrays.asList(new Uint256(listingId), new Uint256(price)), Collections.emptyList());

            String functionEncoder = FunctionEncoder.encode(function);

            RawTransactionManager rawTransactionManager = new RawTransactionManager(web3, credentials);

            BigInteger value = BigInteger.ZERO;

            if (currency.equals(Constants.CONTRACT.NativeCurrency)) {
                value = price;
            }
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, new BigInteger("10000000000"), new BigInteger("200000"), Constants.CONTRACT.MarketService, value, "0x" + functionEncoder);
            String signedTransaction = rawTransactionManager.sign(rawTransaction);

            EthSendTransaction sendRawTransaction = web3.ethSendRawTransaction(signedTransaction).sendAsync().get();

            tx = sendRawTransaction.getTransactionHash();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return tx;
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

                String content = getUrlContents(url);

                if (!content.isEmpty() && content != null) {
                    check = false;
                    return content;
                } else {
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

    private static String getUrlContents(String theUrl) {
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
}
