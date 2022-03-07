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
import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.model.service.ChainverseService;
import com.chainverse.sdk.model.service.Token;
import com.chainverse.sdk.ui.ChainverseSDKActivity;

import java.math.BigDecimal;

import wallet.core.jni.StoredKey;


public class WalletInfoScreen extends Fragment implements View.OnClickListener {
    Button btnRecovery, btnExport;
    Button btnClose;
    TextView tvAddress, txtBalance;
    private LinearLayout viewCopied;

    private String balance;

    EncryptPreferenceUtils encryptPreferenceUtils;

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

        encryptPreferenceUtils = EncryptPreferenceUtils.getInstance().init(getContext());
        StoredKey storedKey = WalletUtils.getInstance().init(getContext()).getStoredKey();
        if (storedKey == null || !storedKey.isMnemonic()) {
            btnRecovery.setVisibility(View.GONE);
        }

        setBalance();

        return mParent;
    }

    private void setBalance() {
        ChainverseService chainverseService = encryptPreferenceUtils.getService();
        for (int i = 0; i < chainverseService.getTokens().size(); i++) {
            Token token = chainverseService.getTokens().get(i);
            TokenProgress tokenProgress = new TokenProgress(token, i);
            tokenProgress.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        balance = "BNB: " + ChainverseSDK.getInstance().getBalance() + "\n";
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
        private Token token;
        private int i;

        public TokenProgress(Token token, int i) {
            this.token = token;
            this.i = i;
        }

        @Override
        protected BigDecimal doInBackground(Void... voids) {
            BigDecimal balance = ChainverseSDK.getInstance().getBalanceToken(token.getAddress());
            return balance;
        }

        @Override
        protected void onPostExecute(BigDecimal bigDecimal) {
            balance += token.getSymbol() + ": " + bigDecimal + "\n";
            txtBalance.setText(balance);
        }
    }
}