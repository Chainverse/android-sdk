package com.chainverse.sdk.ui.screen;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chainverse.sdk.R;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.ui.ChainverseSDKActivity;


public class WalletInfoScreen extends Fragment implements View.OnClickListener{
    Button btnRecovery, btnExport;
    Button btnClose;
    TextView tvAddress;
    private LinearLayout viewCopied;
    public WalletInfoScreen() {
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
        View mParent =  inflater.inflate(R.layout.chainverse_screen_wallet_info, container, false);
        btnRecovery = mParent.findViewById(R.id.chainverse_button_recovery_phrase);
        btnExport = mParent.findViewById(R.id.chainverse_button_export_private_key);
        tvAddress = mParent.findViewById(R.id.chainverse_tv_address);
        viewCopied = mParent.findViewById(R.id.chainverse_view_copied);
        tvAddress.setText(WalletUtils.getInstance().init(getContext()).getAddress());
        btnClose = mParent.findViewById(R.id.chainverse_button_close);
        btnRecovery.setOnClickListener(this);
        btnExport.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
        return mParent;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chainverse_button_recovery_phrase) {
            Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.RECOVERY_WALLET);
            getActivity().startActivity(intent);
            getActivity().finish();
        }else if (v.getId() == R.id.chainverse_button_export_private_key) {
            Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.EXPORT_WALLET);
            getActivity().startActivity(intent);
            getActivity().finish();
        }else if(v.getId() == R.id.chainverse_button_close){
            getActivity().finish();
        }else if(v.getId() == R.id.chainverse_tv_address){
            Utils.copyFromClipboard(getContext(),"address",WalletUtils.getInstance().init(getContext()).getAddress());
            viewCopied.setVisibility(View.VISIBLE);
        }
    }
}