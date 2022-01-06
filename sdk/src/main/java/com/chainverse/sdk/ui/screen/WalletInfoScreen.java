package com.chainverse.sdk.ui.screen;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.R;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.ui.ChainverseSDKActivity;

import java.math.BigDecimal;
import java.util.ArrayList;


public class WalletInfoScreen extends Fragment implements View.OnClickListener {
    Button btnRecovery, btnExport;
    Button btnClose;
    TextView tvAddress, txtBalance;
    private LinearLayout viewCopied;

    private BigDecimal bnb, usdt, busd, cvt;

    public WalletInfoScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBalance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mParent = inflater.inflate(R.layout.chainverse_screen_wallet_info, container, false);
        btnRecovery = mParent.findViewById(R.id.chainverse_button_recovery_phrase);
        btnExport = mParent.findViewById(R.id.chainverse_button_export_private_key);
        tvAddress = mParent.findViewById(R.id.chainverse_tv_address);
        txtBalance = mParent.findViewById(R.id.txtBalance);
        viewCopied = mParent.findViewById(R.id.chainverse_view_copied);
        tvAddress.setText(WalletUtils.getInstance().init(getContext()).getAddress());
        btnClose = mParent.findViewById(R.id.chainverse_button_close);
        btnRecovery.setOnClickListener(this);
        btnExport.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        tvAddress.setOnClickListener(this);

        return mParent;
    }

    private void setBalance() {
        String[] tokens = Constants.TOKEN_SUPPORTED.TOKENS;
        for (int i = 0; i < tokens.length; i++) {
            TokenProgress tokenProgress = new TokenProgress(tokens[i], i);
            tokenProgress.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chainverse_button_recovery_phrase) {
            Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.RECOVERY_WALLET);
            getActivity().startActivity(intent);
            getActivity().finish();
        } else if (v.getId() == R.id.chainverse_button_export_private_key) {
            Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.EXPORT_WALLET);
            getActivity().startActivity(intent);
            getActivity().finish();
        } else if (v.getId() == R.id.chainverse_button_close) {
            getActivity().finish();
        } else if (v.getId() == R.id.chainverse_tv_address) {
            Utils.copyFromClipboard(getContext(), "address", WalletUtils.getInstance().init(getContext()).getAddress());
            viewCopied.setVisibility(View.VISIBLE);
        }
    }

    class TokenProgress extends AsyncTask<Void, TokenProgress, BigDecimal> {
        private String tokenAddress;
        private int i;

        public TokenProgress(String tokenAddress, int i) {
            this.tokenAddress = tokenAddress;
            this.i = i;
        }

        @Override
        protected BigDecimal doInBackground(Void... voids) {
            BigDecimal balance;
            if (tokenAddress == Constants.CONTRACT.NativeCurrency) {
                balance = ChainverseSDK.getInstance().getBalance();
            } else {
                balance = ChainverseSDK.getInstance().getBalanceToken(tokenAddress);
            }
            return balance;
        }

        @Override
        protected void onPostExecute(BigDecimal bigDecimal) {
            switch (tokenAddress) {
                case Constants.CONTRACT.NativeCurrency:
                    bnb = bigDecimal;
                    break;
                case Constants.TOKEN_SUPPORTED.USDT:
                    usdt = bigDecimal;
                    break;
                case Constants.TOKEN_SUPPORTED.BUSD:
                    busd = bigDecimal;
                    break;
                case Constants.TOKEN_SUPPORTED.CVT:
                    cvt = bigDecimal;
                    break;
            }
            String balance = "BNB: " + bnb + "\n" + "USDT: " + usdt + "\n" + "BUSD: " + busd + "\n" + "CVT: " + cvt;
            txtBalance.setText(balance);
        }
    }
}