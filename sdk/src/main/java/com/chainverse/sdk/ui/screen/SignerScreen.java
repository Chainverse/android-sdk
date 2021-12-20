package com.chainverse.sdk.ui.screen;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.R;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.model.SignerData;


public class SignerScreen extends Fragment implements View.OnClickListener{
    Button btnAgree, btnCancel;
    ImageButton btnClose;
    TextView tvData;
    String type;
    SignerData data;
    public SignerScreen() {
        // Required empty public constructor
    }

    public static SignerScreen NewInstance(String type, SignerData data) {
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putParcelable("data",data);
        SignerScreen fragment = new SignerScreen();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        type = args.getString("type");
        data = args.getParcelable("data");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mParent =  inflater.inflate(R.layout.chainverse_screen_confirm_sign, container, false);
        btnAgree = mParent.findViewById(R.id.chainverse_button_agree);
        btnCancel = mParent.findViewById(R.id.chainverse_button_cancel);
        btnClose = mParent.findViewById(R.id.chainverse_button_close);
        tvData = mParent.findViewById(R.id.chainverse_tv_data);
        tvData.setText(data.getMessage());
        btnAgree.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        return mParent;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chainverse_button_agree) {
            switch (type){
                case "message":
                    try {
                        String signedMessage = WalletUtils.getInstance().init(getContext()).signMessage(data.getMessage());
                        if (ChainverseSDK.getInstance().mCallback != null) {
                            ChainverseSDK.getInstance().mCallback.onSignMessage(signedMessage);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "transaction":
                    try {
                        String signedTransaction = WalletUtils.getInstance().init(getContext()).signTransaction(data.getChainId(),data.getGasPrice(),data.getGasLimit(),data.getToAddress(),data.getAmount());
                        if (ChainverseSDK.getInstance().mCallback != null) {
                            ChainverseSDK.getInstance().mCallback.onSignTransaction(signedTransaction);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
            getActivity().finish();
        }else if (v.getId() == R.id.chainverse_button_cancel) {
            getActivity().finish();
        }else if(v.getId() == R.id.chainverse_button_close){
            getActivity().finish();
        }
    }
}