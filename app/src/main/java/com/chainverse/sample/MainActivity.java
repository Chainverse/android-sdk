package com.chainverse.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chainverse.sdk.ChainverseCallback;
import com.chainverse.sdk.ChainverseError;
import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.ChainverseItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnChooseWallet = (Button) findViewById(R.id.btnChooseWallet);
        Button btnConnectTrust = (Button) findViewById(R.id.btnConnectTrust);
        Button btnSend = (Button) findViewById(R.id.btnSendTransaction);
        Button btnLogout = (Button) findViewById(R.id.btnLogout);
        TextView tvAddress = (TextView) findViewById(R.id.tvAddress);

        String developerAddress = "0x9aa2DC5A69eEd97d072A4168A83Cc000873321ff";
        String gameAddress = "0xD703d36e924A84D050F7b17f392F7d6D2Dd483AF";
        ChainverseSDK sdk = ChainverseSDK.getInstance();
        sdk.init(developerAddress,gameAddress,this, new ChainverseCallback() {

            @Override
            public void onInitSDKSuccess() {

            }

            @Override
            public void onError(int error) {
                Log.e("onError", "" + error);
                switch (error){
                    case ChainverseError.ERROR_INIT_SDK:
                        break;
                }

            }

            @Override
            public void onItemUpdate(ChainverseItem item, int type) {
                switch (type){
                    case ChainverseItem.TRANSFER_ITEM_TO_USER:
                        break;
                    case ChainverseItem.TRANSFER_ITEM_FROM_USER:
                        break;
                }
            }

            @Override
            public void onGetItems(ArrayList<ChainverseItem> items) {
                LogUtil.log("onGetItems",items);
            }


            @Override
            public void onConnectSuccess(String address) {
                tvAddress.setText("Wellcome: " +  address);
                Log.e("onConnectSuccess", "" + address);
                ChainverseSDK.getInstance().getItems();
            }

            @Override
            public void onLogout(String address) {
                tvAddress.setText("No Connect Wallet!");
                Toast.makeText(MainActivity.this,"User Address" + address + " Logout",Toast.LENGTH_LONG ).show();
            }
        });
        sdk.setScheme("ota-wallet-rn://");
        sdk.setHost("accounts_callback");
        sdk.setKeepConnect(true);


        btnChooseWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sdk.showConnectView();
            }
        });

        btnConnectTrust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdk.connectWithTrust();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdk.testBuy();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChainverseSDK.getInstance().logout();
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