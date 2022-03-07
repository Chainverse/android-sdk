package com.chainverse.sdk.ui.screen;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.chainverse.sdk.R;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.ui.ChainverseSDKActivity;


public class WalletScreen extends Fragment implements View.OnClickListener {
    Button btnCreate, btnImport;
    ImageButton btnClose;
    RelativeLayout container_screen_wallet;

    public WalletScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mParent = inflater.inflate(R.layout.chainverse_screen_wallet, container, false);
        btnCreate = mParent.findViewById(R.id.chainverse_button_create);
        btnImport = mParent.findViewById(R.id.chainverse_button_import);
        btnClose = mParent.findViewById(R.id.chainverse_button_close);
        container_screen_wallet = mParent.findViewById(R.id.container_screen_wallet);
        btnCreate.setOnClickListener(this);
        btnImport.setOnClickListener(this);
        btnClose.setOnClickListener(this);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        container_screen_wallet.getLayoutParams().height = metrics.heightPixels;
//        int orientation = this.getResources().getConfiguration().orientation;
//        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            btnCreate.getLayoutParams().width = metrics.widthPixels / 2;
//            btnImport.getLayoutParams().width = metrics.widthPixels / 2;
//        }

        return mParent;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chainverse_button_create) {
            Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.CREATE_WALLET);
            getActivity().startActivity(intent);
            getActivity().finish();
        } else if (v.getId() == R.id.chainverse_button_import) {
            Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.IMPORT_WALLET);
            getActivity().startActivity(intent);
            getActivity().finish();
        } else if (v.getId() == R.id.chainverse_button_close) {
            getActivity().finish();
        }
    }
}