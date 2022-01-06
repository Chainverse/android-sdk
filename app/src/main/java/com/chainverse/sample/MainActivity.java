package com.chainverse.sample;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chainverse.sample.marketplace.MarketPlaceActivity;
import com.chainverse.sdk.Chainverse;
import com.chainverse.sdk.ChainverseCallback;
import com.chainverse.sdk.ChainverseError;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.model.MarketItem.ChainverseItemMarket;
import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.ChainverseUser;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.ChainverseItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String developerAddress = "0x6A6c53a166DDDbE7049982864d21C75AB18fc50C";
    String gameAddress = "0x13f1A9097A7Cd7BeBC5Ad5c79160db3067FEf20E";

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


        ChainverseSDK.getInstance().init(developerAddress, gameAddress, this, new ChainverseCallback() {

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
            }

            @Override
            public void onGetItemMarket(ArrayList<ChainverseItemMarket> items) {
            }

            @Override
            public void onGetMyAssets(ArrayList<ChainverseItemMarket> items) { }

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
            public void onSignTransaction(String signed) {
                Log.i("onSignTransaction", signed);
            }

            @Override
            public void onBuy(String tx) {

            }
        });
        ChainverseSDK.getInstance().setScheme("trust-rn-example1://");
        ChainverseSDK.getInstance().setHost("accounts_callback");
        ChainverseSDK.getInstance().setKeepConnect(true);


        if (ChainverseSDK.getInstance().isUserConnected()) {
            //Connected
            ChainverseUser info = ChainverseSDK.getInstance().getUser();
            btnConnectTrust.setVisibility(View.VISIBLE);
            LogUtil.log("info_sig", info.getSignature());
        } else {
            //No connect
            btnConnectTrust.setVisibility(View.GONE);
        }
        btnChooseWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChainverseSDK.getInstance().showConnectView();
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
                ChainverseSDK.getInstance().testBuy();
            }
        });

        btnMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ChainverseSDK.getInstance().callContract();
                Intent intent = new Intent(MainActivity.this, MarketPlaceActivity.class);
                intent.putExtra("developerAddress", developerAddress);
                intent.putExtra("gameAddress", gameAddress);
                startActivity(intent);
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