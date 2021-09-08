package com.chainverse.sdk.ui.screen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.R;


public class ConnectWalletScreen extends Fragment implements View.OnClickListener{
    Button btnConnectTrust, btnConnectChainverse;
    ImageButton btnClose;
    public ConnectWalletScreen() {
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
        View mParent =  inflater.inflate(R.layout.chainverse_screen_connect_wallet, container, false);
        btnConnectTrust = mParent.findViewById(R.id.chainverse_button_connect_trust);
        btnConnectChainverse = mParent.findViewById(R.id.chainverse_button_connect);
        btnClose = mParent.findViewById(R.id.chainverse_button_close);
        btnConnectTrust.setOnClickListener(this);
        btnConnectChainverse.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        return mParent;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chainverse_button_connect_trust) {
            ChainverseSDK.getInstance().connectWithTrust();
        }else if (v.getId() == R.id.chainverse_button_connect) {
            ChainverseSDK.getInstance().connectWithChainverse();
        }else if(v.getId() == R.id.chainverse_button_close){
            getActivity().finish();
        }
    }
}