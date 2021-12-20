package com.chainverse.sdk.ui.screen;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chainverse.sdk.R;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.common.WalletUtils;


public class WalletExportScreen extends Fragment implements View.OnClickListener{
    Button btnClose;
    TextView tvPrivateKey, tvAddress;
    private LinearLayout viewCopied, viewCopiedPrivate;
    public WalletExportScreen() {
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
        View mParent =  inflater.inflate(R.layout.chainverse_screen_wallet_export, container, false);
        btnClose = mParent.findViewById(R.id.chainverse_button_close);
        tvPrivateKey = mParent.findViewById(R.id.chainverse_tv_private_key);
        tvAddress = mParent.findViewById(R.id.chainverse_tv_address);
        viewCopied = mParent.findViewById(R.id.chainverse_view_copied_address);
        viewCopiedPrivate = mParent.findViewById(R.id.chainverse_view_copied_privatekey);
        tvAddress.setText(WalletUtils.getInstance().init(getContext()).getAddress());
        tvPrivateKey.setText(WalletUtils.getInstance().init(getContext()).getPrivateKey());
        tvPrivateKey.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        return mParent;
    }


    @Override
    public void onClick(View v) {
       if(v.getId() == R.id.chainverse_button_close){
            getActivity().finish();
       }else if(v.getId() == R.id.chainverse_tv_private_key){
           Utils.copyFromClipboard(getContext(),"privatekey",WalletUtils.getInstance().init(getContext()).getPrivateKey());
           viewCopiedPrivate.setVisibility(View.VISIBLE);
           viewCopied.setVisibility(View.GONE);
       }else if(v.getId() == R.id.chainverse_tv_address){
           Utils.copyFromClipboard(getContext(),"address",WalletUtils.getInstance().init(getContext()).getAddress());
           viewCopied.setVisibility(View.VISIBLE);
           viewCopiedPrivate.setVisibility(View.GONE);
       }
    }
}