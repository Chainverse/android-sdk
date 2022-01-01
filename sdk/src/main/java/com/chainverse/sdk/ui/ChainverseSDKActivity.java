package com.chainverse.sdk.ui;


import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import com.chainverse.sdk.R;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.ui.screen.AlertScreen;
import com.chainverse.sdk.ui.screen.BuyNftScreen;
import com.chainverse.sdk.ui.screen.LoadingScreen;
import com.chainverse.sdk.ui.screen.SignerScreen;
import com.chainverse.sdk.ui.screen.ConnectWalletScreen;
import com.chainverse.sdk.ui.screen.WalletBackupScreen;
import com.chainverse.sdk.ui.screen.WalletCreateScreen;
import com.chainverse.sdk.ui.screen.WalletExportScreen;
import com.chainverse.sdk.ui.screen.WalletImportScreen;
import com.chainverse.sdk.ui.screen.WalletInfoScreen;
import com.chainverse.sdk.ui.screen.WalletRecoveryScreen;
import com.chainverse.sdk.ui.screen.WalletScreen;
import com.chainverse.sdk.ui.screen.WalletVerifyScreen;

public class ChainverseSDKActivity extends AppCompatActivity {
    private String screen;
    private int screenW, screenH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_chainverse_sdk_activity);
        getScreenSize();
        initLayout();
        showScreen();
    }

    private void getScreenSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        screenW = metrics.widthPixels;
        screenH = metrics.heightPixels;
    }

    private void initLayout() {
        screen = getIntent().getStringExtra("screen");
        if (screen.equals(Constants.SCREEN.WALLET) || screen.equals(Constants.SCREEN.WALLET_INFO) || screen.equals(Constants.SCREEN.CREATE_WALLET) || screen.equals(Constants.SCREEN.EXPORT_WALLET) || screen.equals(Constants.SCREEN.IMPORT_WALLET) || screen.equals(Constants.SCREEN.BACKUP_WALLET) || screen.equals(Constants.SCREEN.VERIFY_WALLET) || screen.equals(Constants.SCREEN.RECOVERY_WALLET)) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.height = screenH;
            params.width = screenW;
            params.gravity = Gravity.CENTER;
            getWindow().setAttributes(params);
        } else if (screen.equals(Constants.SCREEN.ALERT)) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.height = Utils.convertDPToPixels(this, 200);//220
            params.width = Utils.convertDPToPixels(this, 300);
            params.gravity = Gravity.CENTER;
            getWindow().setAttributes(params);
        } else if (screen.equals(Constants.SCREEN.LOADING)) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.height = Utils.convertDPToPixels(this, 100);//220
            params.width = Utils.convertDPToPixels(this, 100);
            params.gravity = Gravity.CENTER;
            getWindow().setAttributes(params);
        } else {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.height = Utils.convertDPToPixels(this, 280);//220
            params.width = Utils.convertDPToPixels(this, 300);
            params.gravity = Gravity.CENTER;
            getWindow().setAttributes(params);
        }

    }

    private void showScreen() {
        screen = getIntent().getStringExtra("screen");
        switch (screen) {
            case Constants.SCREEN.CONNECT_VIEW:
                replaceFragment(new ConnectWalletScreen());
                break;
            case Constants.SCREEN.CONFIRM_SIGN:
                replaceFragment(SignerScreen.NewInstance(getIntent().getStringExtra("type"), getIntent().getParcelableExtra("data")));
                break;
            case Constants.SCREEN.BUY_NFT:
                replaceFragment(BuyNftScreen.NewInstance(getIntent().getStringExtra("type"), getIntent().getStringExtra("currency"), getIntent().getLongExtra("listing_id", 0), getIntent().getDoubleExtra("price", 0),getIntent().getBooleanExtra("isAuction", false)));
                break;
            case Constants.SCREEN.WALLET:
                replaceFragment(new WalletScreen());
                break;
            case Constants.SCREEN.WALLET_INFO:
                replaceFragment(new WalletInfoScreen());
                break;
            case Constants.SCREEN.EXPORT_WALLET:
                replaceFragment(new WalletExportScreen());
                break;
            case Constants.SCREEN.CREATE_WALLET:
                replaceFragment(new WalletCreateScreen());
                break;
            case Constants.SCREEN.RECOVERY_WALLET:
                replaceFragment(new WalletRecoveryScreen());
                break;
            case Constants.SCREEN.IMPORT_WALLET:
                replaceFragment(new WalletImportScreen());
                break;
            case Constants.SCREEN.BACKUP_WALLET:
                replaceFragment(WalletBackupScreen.NewInstance(getIntent().getStringExtra("type")));
                break;
            case Constants.SCREEN.VERIFY_WALLET:
                replaceFragment(new WalletVerifyScreen());
                break;
            case Constants.SCREEN.ALERT:
                replaceFragment(new AlertScreen());
                break;
            case Constants.SCREEN.LOADING:
                replaceFragment(new LoadingScreen());
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.com_chainverse_sdk_container, fragment).commit();
    }
}