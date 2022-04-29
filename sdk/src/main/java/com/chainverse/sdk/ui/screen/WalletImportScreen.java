package com.chainverse.sdk.ui.screen;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chainverse.sdk.R;
import com.chainverse.sdk.common.BroadcastUtil;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.listener.OnWalletListener;
import com.chainverse.sdk.ui.ChainverseSDKActivity;

import org.bouncycastle.util.encoders.Hex;

import java.util.ArrayList;

import wallet.core.jni.CoinType;
import wallet.core.jni.PrivateKey;
import wallet.core.jni.StoredKey;

enum TypeImport {
    PHRASE,
    PRIVATE_KEY
}

public class WalletImportScreen extends Fragment implements View.OnClickListener {
    private View mGroupButton, mParent, mBackStep;
    private Button btnPhrase, btnPrivateKey, btnPaste;
    private ImageButton btnClose;
    private TextView txtStep, title, tvError;
    private LinearLayout btnBackStep, btnImport;
    private RelativeLayout container;
    private EditText edtPhrase;
    private ImageView iconArrow;

    private TypeImport type = TypeImport.PHRASE;

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
        mParent = inflater.inflate(R.layout.import_wallet, container, false);
        mGroupButton = inflater.inflate(R.layout.groupt_button, container, false);
        mBackStep = inflater.inflate(R.layout.back_step, container, false);

        findView();

        btnBackStep.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        btnPaste.setOnClickListener(this);
        btnPrivateKey.setOnClickListener(this);
        btnPhrase.setOnClickListener(this);
        btnImport.setOnClickListener(this);

        resizeView();
        edtPhrase.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    btnImport.setEnabled(false);
                    btnImport.setBackgroundTintList(getContext().getColorStateList(R.color.ColorDisable));
                    iconArrow.setImageTintList(getContext().getColorStateList(R.color.ColorDisable));
                } else {
                    btnImport.setEnabled(true);
                    btnImport.setBackgroundTintList(getContext().getColorStateList(R.color.ChainverseColorPrimary));
                    iconArrow.setImageTintList(getContext().getColorStateList(R.color.ChainverseColorPrimary));
                }
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
        return mParent;
    }

    private void findView() {
        container = mParent.findViewById(R.id.container_import_wallet);
        btnPhrase = mParent.findViewById(R.id.button_phrase);
        btnPrivateKey = mParent.findViewById(R.id.button_private_key);
        title = mParent.findViewById(R.id.title_import);
        edtPhrase = mParent.findViewById(R.id.edit_text_import);
        btnPaste = mParent.findViewById(R.id.paste_import);
        tvError = mParent.findViewById(R.id.text_error_import);
        btnImport = mParent.findViewById(R.id.button_import_next);
        iconArrow = mParent.findViewById(R.id.icon_import_arrow_right);

        btnClose = mGroupButton.findViewById(R.id.chainverse_button_close);

        btnBackStep = mBackStep.findViewById(R.id.button_back_step);
        txtStep = mBackStep.findViewById(R.id.text_step);

        txtStep.setText("Back");
        btnImport.setEnabled(false);
    }

    private void resizeView() {
        RelativeLayout.LayoutParams lp_button = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lp_back = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        lp_button.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp_button.topMargin = 20;
        lp_button.rightMargin = 20;

        lp_back.topMargin = 20;
        lp_back.leftMargin = 80;

        container.addView(mGroupButton, lp_button);
        container.addView(mBackStep, lp_back);
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getRootView().getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chainverse_button_close) {
            getActivity().finish();
        } else if (v.getId() == R.id.button_back_step) {
            Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.WALLET);
            getActivity().startActivity(intent);
            getActivity().finish();
        } else if (v.getId() == R.id.button_phrase) {
            type = TypeImport.PHRASE;
            btnPrivateKey.setBackground(getContext().getDrawable(android.R.color.transparent));
            btnPrivateKey.setTextColor(Color.parseColor("#ffffff"));
            btnPhrase.setBackground(getContext().getDrawable(R.drawable.background_border));
            btnPhrase.setTextColor(getContext().getColor(R.color.ChainverseColorPrimary));
            title.setText("Recovery Phrase");
        } else if (v.getId() == R.id.button_private_key) {
            type = TypeImport.PRIVATE_KEY;
            btnPhrase.setBackground(getContext().getDrawable(android.R.color.transparent));
            btnPhrase.setTextColor(Color.parseColor("#ffffff"));
            btnPrivateKey.setBackground(getContext().getDrawable(R.drawable.background_border));
            btnPrivateKey.setTextColor(getContext().getColor(R.color.ChainverseColorPrimary));
            title.setText("Private Key");
        } else if (v.getId() == R.id.paste_import) {
            try {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                CharSequence textToPaste = clipboard.getPrimaryClip().getItemAt(0).getText();
                edtPhrase.setText(textToPaste);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (v.getId() == R.id.button_import_next) {
            importWallet();
        }
    }

    private void importWallet() {
        WalletUtils walletUtils = WalletUtils.getInstance().init(getContext());
        Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
        intent.putExtra("screen", Constants.SCREEN.LOADING);
        getActivity().startActivity(intent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    StoredKey storedKey;
                    if (type.equals(TypeImport.PHRASE)) {
                        ArrayList<CoinType> coins = new ArrayList<>();
                        coins.add(CoinType.SMARTCHAIN);
                        storedKey = walletUtils.importWallet(edtPhrase.getText().toString().trim(), "", "", coins);
                    } else {
                        byte[] bytes = Hex.decode(edtPhrase.getText().toString().trim());
                        PrivateKey importPrivateKey = new PrivateKey(bytes);
                        storedKey = walletUtils.importWallet(importPrivateKey, "", "", CoinType.SMARTCHAIN);
                    }
                    if (storedKey != null) {
                        BroadcastUtil.send(getContext(), Constants.ACTION.DIMISS_LOADING);

                        String xUserAddress = walletUtils.getAddress();
                        EncryptPreferenceUtils.getInstance().init(getContext()).setXUserAddress(xUserAddress);
                        try {
                            EncryptPreferenceUtils.getInstance().init(getContext()).setXUserSignature(walletUtils.signMessage("ChainVerse"));
                            EncryptPreferenceUtils.getInstance().init(getContext()).setConnectWallet(Constants.TYPE_IMPORT_WALLET.IMPORTED);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        BroadcastUtil.send(getContext(), Constants.ACTION.CREATED_WALLET);

                        Intent intentAlert = new Intent(getContext(), ChainverseSDKActivity.class);
                        intentAlert.putExtra("screen", Constants.SCREEN.ALERT);
                        intentAlert.putExtra("message", "Import wallet successful");
                        getActivity().startActivity(intentAlert);

                        getActivity().finish();
                    } else {
                        BroadcastUtil.send(getContext(), Constants.ACTION.DIMISS_LOADING);
                        tvError.setVisibility(View.VISIBLE);
                        tvError.setText(type.equals(TypeImport.PHRASE) ? "Wrong Recovery Phrase. Please, check again!" : "Wrong Private Key. Please, check again!");
                    }
                } catch (Exception e) {
                    BroadcastUtil.send(getContext(), Constants.ACTION.DIMISS_LOADING);
                    tvError.setVisibility(View.VISIBLE);
                    tvError.setText(type.equals(TypeImport.PHRASE) ? "Wrong Recovery Phrase. Please, check again!" : "Wrong Private Key. Please, check again!");
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, 500);
    }
}

