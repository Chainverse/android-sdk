package com.chainverse.sdk.ui.screen;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chainverse.sdk.R;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.ui.ChainverseSDKActivity;


public class WalletScreen extends Fragment implements View.OnClickListener {
    ImageButton btnClose;
    RelativeLayout wallet_container;
    LinearLayout container_screen_wallet, layout_logo, btnCreate, btnImport;
    ImageView image_logo;
    TextView textCreate, textImport;

    View mGroupButton, mParent;

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
        mParent = inflater.inflate(R.layout.chainverse_screen_wallet, container, false);
        mGroupButton = inflater.inflate(R.layout.groupt_button, container, false);

        findView();

        btnCreate.setOnClickListener(this);
        btnImport.setOnClickListener(this);
        btnClose.setOnClickListener(this);

        resizeView();

        return mParent;
    }

    private void findView() {
        wallet_container = mParent.findViewById(R.id.wallet_container);
        layout_logo = mParent.findViewById(R.id.layout_logo);
        container_screen_wallet = mParent.findViewById(R.id.container_screen_wallet);
        textCreate = mParent.findViewById(R.id.text_create);
        textImport = mParent.findViewById(R.id.text_import);

        btnCreate = mParent.findViewById(R.id.chainverse_button_create);
        btnImport = mParent.findViewById(R.id.chainverse_button_import);
        btnClose = mGroupButton.findViewById(R.id.chainverse_button_close);
        image_logo = mParent.findViewById(R.id.image_logo);
    }

    private void resizeView() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.topMargin = 20;
        params.rightMargin = 20;

        wallet_container.addView(mGroupButton, params);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        container_screen_wallet.getLayoutParams().width = metrics.widthPixels - (48 * 2);
        image_logo.getLayoutParams().height = metrics.heightPixels / 3;
        image_logo.getLayoutParams().width = metrics.heightPixels / 3;

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            btnCreate.getLayoutParams().width = container_screen_wallet.getLayoutParams().width / 4;
            btnImport.getLayoutParams().width = container_screen_wallet.getLayoutParams().width / 4;
        }
        textCreate.setTextSize(getSizeText(metrics));
        textImport.setTextSize(getSizeText(metrics));
    }

    private float getSizeText(DisplayMetrics metrics) {
        System.out.println(metrics.widthPixels);
        float size;
        if (0 < metrics.widthPixels && metrics.widthPixels <= 768) {
            size = 10;
        } else if (768 < metrics.widthPixels && metrics.widthPixels <= 1080) {
            size = 13;
        } else {
            size = 15;
        }

        return size;
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