package com.chainverse.sdk.ui.screen;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chainverse.sdk.R;
import com.chainverse.sdk.common.BroadcastUtil;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.listener.OnWalletListener;
import com.chainverse.sdk.ui.ChainverseSDKActivity;


public class WalletImportScreen extends Fragment implements View.OnClickListener {
    private Button btnClose, btnImport, btnPaste;
    private EditText edtPhrase;
    private TextView tvError;

    public WalletImportScreen() {
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
        View mParent = inflater.inflate(R.layout.chainverse_screen_wallet_import, container, false);
        btnClose = mParent.findViewById(R.id.chainverse_button_close);
        btnImport = mParent.findViewById(R.id.chainverse_button_import);
        btnPaste = mParent.findViewById(R.id.chainverse_button_paste);
        tvError = mParent.findViewById(R.id.chainverse_tv_error);
        edtPhrase = mParent.findViewById(R.id.chainverse_edittext_phrase);
        edtPhrase.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                btnImport.setEnabled(true);
                btnImport.setBackgroundResource(R.drawable.chainverse_background_button_wallet_create);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });
        edtPhrase.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard();
                }
            }
        });
        btnClose.setOnClickListener(this);
        btnImport.setOnClickListener(this);
        btnImport.setEnabled(false);
        btnPaste.setOnClickListener(this);
        return mParent;
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chainverse_button_close) {
            getActivity().finish();
        } else if (v.getId() == R.id.chainverse_button_import) {
            Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.LOADING);
            getActivity().startActivity(intent);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    WalletUtils.getInstance().init(getContext()).importWallet(edtPhrase.getText().toString(), new OnWalletListener() {
                        @Override
                        public void onCreated() {

                        }

                        @Override
                        public void onImported() {
                            BroadcastUtil.send(getContext(), Constants.ACTION.DIMISS_LOADING);

                            String xUserAddress = WalletUtils.getInstance().init(getContext()).getAddress();
                            EncryptPreferenceUtils.getInstance().init(getContext()).setXUserAddress(xUserAddress);
                            try {
                                EncryptPreferenceUtils.getInstance().init(getContext()).setXUserSignature(WalletUtils.getInstance().init(getContext()).signMessage("ChainVerse"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            BroadcastUtil.send(getContext(), Constants.ACTION.CREATED_WALLET);

                            Intent intentAlert = new Intent(getContext(), ChainverseSDKActivity.class);
                            intentAlert.putExtra("screen", Constants.SCREEN.ALERT);
                            getActivity().startActivity(intentAlert);

                            getActivity().finish();
                        }

                        @Override
                        public void onImportedFailed() {
                            BroadcastUtil.send(getContext(), Constants.ACTION.DIMISS_LOADING);
                            tvError.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }, 500);


        } else if (v.getId() == R.id.chainverse_button_paste) {
            try {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                CharSequence textToPaste = clipboard.getPrimaryClip().getItemAt(0).getText();
                edtPhrase.setText(textToPaste);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}