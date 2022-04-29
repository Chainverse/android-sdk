package com.chainverse.sample;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chainverse.sdk.ChainverseCallback;
import com.chainverse.sdk.ChainverseError;
import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.ChainverseUser;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.ChainverseItem;
import com.chainverse.sdk.listener.Action;
import com.chainverse.sdk.model.NFT.NFT;
import com.chainverse.sdk.model.Params.FilterMarket;
import com.chainverse.sdk.ui.ChainverseSDKActivity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import wallet.core.jni.StoredKey;


public class MainActivity extends AppCompatActivity {
    public interface CONTRACT {
        public static final String developerAddress = "0x6A6c53a166DDDbE7049982864d21C75AB18fc50C";
        public static final String gameAddress = "0x13f1A9097A7Cd7BeBC5Ad5c79160db3067FEf20E";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnChooseWallet = (Button) findViewById(R.id.btnChooseWallet);
        Button btnConnectTrust = (Button) findViewById(R.id.btnWalletInfo);
        Button btnSend = (Button) findViewById(R.id.btnSendTransaction);
        Button btnLogout = (Button) findViewById(R.id.btnLogout);
        Button btnMarket = (Button) findViewById(R.id.btnMarket);
        Button btnMyAsset = (Button) findViewById(R.id.btnMyAsset);
        TextView tvAddress = (TextView) findViewById(R.id.tvAddress);
        TextView tvBalance = (TextView) findViewById(R.id.tvBalance);


        ChainverseSDK.getInstance().init(CONTRACT.developerAddress, CONTRACT.gameAddress, this, new ChainverseCallback() {

            @Override
            public void onInitSDKSuccess() {

            }

            @Override
            public void onError(int error) {
                Log.e("onError", "" + error);
                switch (error) {
                    case ChainverseError.ERROR_INIT_SDK:
                        break;
                }

            }

            @Override
            public void onItemUpdate(ChainverseItem item, int type) {
                LogUtil.log("onItemUpdate", item);
                switch (type) {
                    case ChainverseItem.TRANSFER_ITEM_TO_USER:
                        break;
                    case ChainverseItem.TRANSFER_ITEM_FROM_USER:
                        break;
                }
            }

            @Override
            public void onGetItems(ArrayList<ChainverseItem> items) {
                LogUtil.log("on get item ", items);
            }

            @Override
            public void onGetListItemMarket(ArrayList<NFT> items, int count) {

                LogUtil.log("nft ", items);
            }

            @Override
            public void onGetMyAssets(ArrayList<NFT> items) {
                LogUtil.log("item ", items);
            }

            @Override
            public void onGetDetailItem(NFT nft) {
                LogUtil.log("tag ", nft);
            }

            @Override
            public void onConnectSuccess(String address) {
                tvAddress.setText("Wellcome: " + address);
                tvBalance.setText("Balance: " + ChainverseSDK.getInstance().getBalance());

//                System.out.println("Balance of token " + ChainverseSDK.getInstance().getBalanceToken("0x672021e3c741910896cad6D6121446a328ba5634"));

                Log.i("onConnectSuccess", "" + address);
//                ChainverseSDK.getInstance().getItems();
//                ChainverseSDK.getInstance().getItemOnMarket(0, 20, "");
//                ChainverseSDK.getInstance().getBalance();
                btnConnectTrust.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLogout(String address) {
                tvAddress.setText("No Connect Wallet!");
                btnConnectTrust.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "User Address" + address + " Logout", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSignMessage(String signed) {
                Log.i("onSignMessage", signed);
            }

            @Override
            public void onSignTransaction(Constants.EFunction function, String signed) {
                Log.i("onSignTransaction", signed);
            }

            @Override
            public void onTransact(Constants.EFunction function, String tx) {
                System.out.println("function " + function + " transaction hash " + tx);
            }

            @Override
            public void onErrorTransaction(Constants.EFunction function, String error) {
                System.out.println("function " + function + " error " + error);
            }
        });
        ChainverseSDK.getInstance().setScheme("trust-rn-example1://");
        ChainverseSDK.getInstance().setHost("accounts_callback");
        ChainverseSDK.getInstance().setKeepConnect(true);


        if (ChainverseSDK.getInstance().isUserConnected()) {
            //Connected
            ChainverseUser info = ChainverseSDK.getInstance().getUser();
            btnConnectTrust.setVisibility(View.VISIBLE);
//            LogUtil.log("info_sig", info.getSignature());
        } else {
            //No connect
            btnConnectTrust.setVisibility(View.GONE);
        }
        btnChooseWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChainverseSDK.getInstance().getMyAsset();
//                ChainverseSDK.getInstance().showConnectView();
//                ChainverseSDK.getInstance().approveToken("0x672021e3c741910896cad6D6121446a328ba5634", "0x2ccA92F66BeA2A7fA2119B75F3e5CB698C252564", 3.0);
//                ChainverseSDK.getInstance().buyNFT("0x672021e3c741910896cad6D6121446a328ba5634", new BigInteger("476"), 0.1);
//                ChainverseSDK.getInstance().approveNFT("0x7eAdaF22D3a4C10E0bA1aC692654b80954084bdD", new BigInteger("279"));
//                ChainverseSDK.getInstance().bidNFT("0x672021e3c741910896cad6D6121446a328ba5634", new BigInteger("2116"), 0.25);
//                ChainverseSDK.getInstance().sellNFT("0x7eAdaF22D3a4C10E0bA1aC692654b80954084bdD", new BigInteger("279"), 2, "0x672021e3c741910896cad6D6121446a328ba5634");
//                ChainverseSDK.getInstance().cancelSellNFT(new BigInteger("2122"));
//                ChainverseSDK.getInstance().transferItem("0x265b5F34bA132D5E39D7d0dB3680Fe3D8fD39810", "0x7eAdaF22D3a4C10E0bA1aC692654b80954084bdD", new BigInteger("279"));
//                ChainverseSDK.getInstance().signMessage("Welcome to ChainVerse!\n" +
//                        "\n" +
//                        "This request will not trigger a blockchain transaction or cost any gas fees.\n" +
//                        "\n" +
//                        "Your authentication status will reset after 24 hours.\n" +
//                        "\n" +
//                        "Your timestamp: 1651133781\n" +
//                        "Nonce: 18033", false);
            }
        });

        btnConnectTrust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChainverseSDK.getInstance().showWalletInfoView();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ChainverseSDK.getInstance().signMessage("chainverse");
                //ChainverseSDK.getInstance().signTransaction("01","100000","100000000000","0xC37054b3b48C3317082E7ba872d7753D13da4986","0.0001");
                ChainverseSDK.getInstance().showConnectWalletView();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChainverseSDK.getInstance().logout();
            }
        });

        btnMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ChainverseSDK.getInstance().getMyAsset();
//                try {
//                    ChainverseSDK.getInstance().connectWithChainverse();
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                }

//                ChainverseSDK.getInstance().importWalletByMnemonic("crush solar bread symbol laugh clutch unveil crack silent mushroom discover novel");
//                String mnemonic = ChainverseSDK.getInstance().genMnemonic(256);
//                System.out.println(mnemonic);
//                System.out.println(address);
//////                ChainverseSDK.getInstance().checkAddress("0x4115737CB80A7Dd57b4285C3c68894012275063d");
//////                ChainverseSDK.getInstance().getAbiDefination();
//////                LogUtil.log("nft ", nft);
                try {
                    NFT nft = ChainverseSDK.getInstance().getNFT("0x7D5495EE8999258f2eDC5D3ed497889410793D31", new BigInteger("55"));
                    LogUtil.log("key ", nft);
//                    FilterMarket filterMarket = new FilterMarket();
//                    filterMarket.setName("");
//                    filterMarket.setPage(0);
//                    filterMarket.setPageSize(10);
//                    ChainverseSDK.getInstance().getListItemOnMarket(filterMarket);
//                    BigInteger amount = ChainverseSDK.getInstance().isApproved(Constants.TOKEN_SUPPORTED.CVT, "0x4115737CB80A7Dd57b4285C3c68894012275063d", Constants.CONTRACT.MarketService);
//                    double mount = amount.doubleValue() * Math.pow(10, -18);
//                    System.out.println(mount);
//                    ChainverseSDK.getInstance().approveToken(Constants.TOKEN_SUPPORTED.CVT, Constants.CONTRACT.MarketService, 200.000000);
//                    String tx = ChainverseSDK.getInstance().bidNFT(Constants.TOKEN_SUPPORTED.CVT, new BigInteger("609"), 45.5);
//                    String tx = ChainverseSDK.getInstance().transferItem("0x760B9251261520478CeE8b6db0f45E22b5D18E4A","0x7eAdaF22D3a4C10E0bA1aC692654b80954084bdD", new BigInteger("221"));
//                    BigInteger allowence = ChainverseSDK.getInstance().isApproved(Constants.TOKEN_SUPPORTED.CVT, "0x4115737CB80A7Dd57b4285C3c68894012275063d", Constants.CONTRACT.MarketService);
//                    ChainverseSDK.getInstance().approveNFT("0x7eAdaF22D3a4C10E0bA1aC692654b80954084bdD", new BigInteger("221"));
//                    boolean check = ChainverseSDK.getInstance().isApproved("0x7eAdaF22D3a4C10E0bA1aC692654b80954084bdD", new BigInteger("235"));
//                    ChainverseSDK.getInstance().sellNFT("0x7eAdaF22D3a4C10E0bA1aC692654b80954084bdD", new BigInteger("221"), 10.5, Constants.TOKEN_SUPPORTED.CVT);
//                    ChainverseSDK.getInstance().publishNFT("0x7eAdaF22D3a4C10E0bA1aC692654b80954084bdD", new BigInteger("221"), new Action.publishNFT() {
//                        @Override
//                        public void onSuccess() {
//                            System.out.println("run here");
//                        }
//
//                        @Override
//                        public void onError(String message) {
//                            System.out.println("error " + message);
//                        }
//                    });
//                    System.out.println(check);
//                    String approve = ChainverseSDK.getInstance().
//                    ChainverseSDK.getInstance().buyNFT(Constants.TOKEN_SUPPORTED.CVT, new BigInteger("940"), 45);
                } catch (Exception error) {
                    System.out.println("error " + error);
                }
//                ChainverseSDK.getInstance().getMyAsset();
//                ChainverseSDK.getInstance().getItems();
//                Intent intent = new Intent(MainActivity.this, MarketPlaceActivity.class);
//                startActivity(intent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // getIntent() should always return the most recent
        setIntent(intent);
        ChainverseSDK.getInstance().onNewIntent(intent);
    }
}