package com.chainverse.sdk.ui;


import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;

import com.chainverse.sdk.R;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.ui.screen.ConnectWalletScreen;

public class ChainverseSDKActivity extends AppCompatActivity {
    private String screen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_chainverse_sdk_activity);
        initLayout();
        showScreen();
    }

    private void initLayout(){
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = Utils.convertDPToPixels(this,282);
        params.width =  Utils.convertDPToPixels(this,300);
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);
    }

    private void showScreen(){
        screen = getIntent().getStringExtra("screen");
        switch (screen){
            case "showConnectView":
                replaceFragment(new ConnectWalletScreen());
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.com_chainverse_sdk_container, fragment).commit();
    }
}