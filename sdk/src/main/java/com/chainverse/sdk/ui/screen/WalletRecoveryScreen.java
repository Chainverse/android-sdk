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
import com.chainverse.sdk.ui.ChainverseSDKActivity;


public class WalletRecoveryScreen extends Fragment implements View.OnClickListener{
    private Button btnClose, btnSubmit;
    private CheckBox checkBoxTerm;
    public WalletRecoveryScreen() {
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
        View mParent =  inflater.inflate(R.layout.chainverse_screen_wallet_recovery, container, false);
        btnClose = mParent.findViewById(R.id.chainverse_button_close);
        btnSubmit = mParent.findViewById(R.id.chainverse_button_submit);
        checkBoxTerm = mParent.findViewById(R.id.chainverse_checkbox_term);
        checkBoxTerm.setOnClickListener(checkBoxTermOnClickListener);
        btnClose.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnSubmit.setEnabled(false);
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            btnSubmit.getLayoutParams().width = metrics.widthPixels / 2;
        }
        return mParent;
    }

    private View.OnClickListener checkBoxTermOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final boolean isChecked = checkBoxTerm.isChecked();
            if(isChecked){
                btnSubmit.setBackgroundResource(R.drawable.chainverse_background_button_wallet_create);
                btnSubmit.setEnabled(true);
            }else{
                btnSubmit.setBackgroundResource(R.drawable.chainverse_background_button_wallet_create_default);
                btnSubmit.setEnabled(false);
            }
        }
    };


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.chainverse_button_close){
            getActivity().finish();
        }else if(v.getId() == R.id.chainverse_button_submit){
            Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.BACKUP_WALLET);
            intent.putExtra("type", "view");
            getActivity().startActivity(intent);
            getActivity().finish();
        }
    }
}