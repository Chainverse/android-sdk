package com.chainverse.sdk.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;

import com.chainverse.sdk.R;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.ui.screen.ChooseWLScreen;

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
        params.height = Utils.convertDPToPixels(this,300);
        params.width =  Utils.convertDPToPixels(this,300);
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);
    }

    private void showScreen(){
        screen = getIntent().getStringExtra("screen");
        switch (screen){
            case "choosewl":
                replaceFragment(new ChooseWLScreen());
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.com_chainverse_sdk_container, fragment).commit();
    }
}