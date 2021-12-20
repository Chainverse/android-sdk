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
import android.widget.CheckBox;

import com.chainverse.sdk.R;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.listener.OnWalletListener;
import com.chainverse.sdk.ui.ChainverseSDKActivity;


public class WalletCreateScreen extends Fragment implements View.OnClickListener{
    private Button btnClose, btnCreate;
    private CheckBox checkBoxTerm, checkBox12Word, checkBox24Word;
    public WalletCreateScreen() {
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
        View mParent =  inflater.inflate(R.layout.chainverse_screen_wallet_create, container, false);
        btnClose = mParent.findViewById(R.id.chainverse_button_close);
        btnCreate = mParent.findViewById(R.id.chainverse_button_create);
        checkBoxTerm = mParent.findViewById(R.id.chainverse_checkbox_term);
        checkBox12Word = mParent.findViewById(R.id.chainverse_checkbox_12word);
        checkBox24Word = mParent.findViewById(R.id.chainverse_checkbox_24word);
        checkBoxTerm = mParent.findViewById(R.id.chainverse_checkbox_term);
        checkBoxTerm.setOnClickListener(checkBoxTermOnClickListener);
        checkBox12Word.setOnClickListener(checkBox12WordOnClickListener);
        checkBox24Word.setOnClickListener(checkBox24WordOnClickListener);

        btnClose.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        btnCreate.setEnabled(false);

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            btnCreate.getLayoutParams().width = metrics.widthPixels / 2;
        }
        return mParent;
    }

    private View.OnClickListener checkBox12WordOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final boolean isChecked = checkBox12Word.isChecked();
            if(isChecked){
                checkBox24Word.setChecked(false);
            }else{
                checkBox24Word.setChecked(true);
            }
        }
    };

    private View.OnClickListener checkBox24WordOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final boolean isChecked = checkBox24Word.isChecked();
            if(isChecked){
                checkBox12Word.setChecked(false);
            }else{
                checkBox12Word.setChecked(true);
            }
        }
    };

    private View.OnClickListener checkBoxTermOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final boolean isChecked = checkBoxTerm.isChecked();
            if(isChecked){
                btnCreate.setBackgroundResource(R.drawable.chainverse_background_button_wallet_create);
                btnCreate.setEnabled(true);
            }else{
                btnCreate.setBackgroundResource(R.drawable.chainverse_background_button_wallet_create_default);
                btnCreate.setEnabled(false);
            }
        }
    };


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.chainverse_button_close){
            getActivity().finish();
        }else if(v.getId() == R.id.chainverse_button_create){
            int strength = 128;
            if(checkBox24Word.isChecked()){
                strength = 256;
            }
            String passphrase = "";
            WalletUtils.getInstance().init(getContext()).createWallet(strength, passphrase, new OnWalletListener() {
                @Override
                public void onCreated() {
                    Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
                    intent.putExtra("screen", Constants.SCREEN.BACKUP_WALLET);
                    intent.putExtra("type", "normal");
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }

                @Override
                public void onImported() {

                }

                @Override
                public void onImportedFailed() {

                }
            });
        }
    }
}