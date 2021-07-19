package com.chainverse.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chainverse.sdk.ChainverseCallback;
import com.chainverse.sdk.ChainverseSDK;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnChooseWallet = (Button) findViewById(R.id.btnChooseWallet);
        Button btnConnectTrust = (Button) findViewById(R.id.btnConnectTrust);
        Button btnSend = (Button) findViewById(R.id.btnSendTransaction);
        TextView tvAddress = (TextView) findViewById(R.id.tvAddress);

        String developerAddress = "0x690FDdc2a98050f924Bd7Ec5900f2D2F49b6aEC7";
        String gameAddress = "0x3F57BF31E55de54306543863E079aD234f477b88";
        ChainverseSDK sdk = ChainverseSDK.getInstance();
        sdk.init(developerAddress,gameAddress,this, new ChainverseCallback() {

            @Override
            public void onInitSuccess() {

            }

            @Override
            public void onItemUpdate() {

            }

            @Override
            public void onConnectedWallet(String address) {
                tvAddress.setText(address);
            }
        });
        sdk.setCallbackScheme("com.chainverse.sample");
        sdk.setCallbackHost("accounts_callback");

        sdk.getSupportedWallets();


        btnChooseWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sdk.chooseWallet();
            }
        });

        btnConnectTrust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdk.connectTrust();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChainverseSDK.getInstance().transferTrustWL("com.chainverse.sample", 714,"0x5efe370cfedf4d38f99cf645d38b993627937f46","1.0");
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // getIntent() should always return the most recent
        setIntent(intent);
        ChainverseSDK.getInstance().handleResult(intent);
    }
}