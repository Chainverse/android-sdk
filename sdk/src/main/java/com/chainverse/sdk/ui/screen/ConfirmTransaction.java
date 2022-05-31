package com.chainverse.sdk.ui.screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chainverse.sdk.R;
import com.chainverse.sdk.common.BroadcastUtil;
import com.chainverse.sdk.common.CallbackToGame;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.LogUtil;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.manager.ServiceManager;
import com.chainverse.sdk.model.TransactionData;
import com.chainverse.sdk.ui.ChainverseSDKActivity;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ConfirmTransaction extends Fragment implements View.OnClickListener {
    private View mParent, mGroupButton, mBackStep;
    private ImageButton btnClose;
    private TextView txtStep, txtFrom, txtTo, txtAsset, txtFee, txtValue;
    private LinearLayout btnBackStep, btnCancel, btnConfirm, viewAsset, viewValue, viewNetworkFee, viewArrow, viewTo, viewFrom;
    private RelativeLayout container;

    private TransactionData transactionData;
    private boolean isPersonal = false; // Type sign message

    public static ConfirmTransaction NewInstance(TransactionData transactionData, boolean isPersonal) {
        Bundle args = new Bundle();
        args.putParcelable("transactionData", transactionData);
        args.putBoolean("isPersonal", isPersonal);
        ConfirmTransaction fragment = new ConfirmTransaction();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        transactionData = args.getParcelable("transactionData");
        isPersonal = args.getBoolean("isPersonal");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mParent = inflater.inflate(R.layout.confirm_transaction, container, false);
        mGroupButton = inflater.inflate(R.layout.groupt_button, container, false);
        mBackStep = inflater.inflate(R.layout.back_step, container, false);

        findView();

        btnConfirm.setOnClickListener(this);
        btnBackStep.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnClose.setOnClickListener(this);

        resizeView();
        return mParent;
    }

    private void findView() {
        container = mParent.findViewById(R.id.container_confirm);
        txtAsset = mParent.findViewById(R.id.transaction_asset);
        txtFrom = mParent.findViewById(R.id.transaction_from);
        txtTo = mParent.findViewById(R.id.transaction_to);
        txtFee = mParent.findViewById(R.id.transaction_fee);
        txtValue = mParent.findViewById(R.id.transaction_value);
        viewAsset = mParent.findViewById(R.id.view_asset);
        viewValue = mParent.findViewById(R.id.view_value);
        viewNetworkFee = mParent.findViewById(R.id.view_network_fee);
        viewArrow = mParent.findViewById(R.id.view_arrow);
        viewTo = mParent.findViewById(R.id.view_to);
        viewFrom = mParent.findViewById(R.id.view_from);
        btnCancel = mParent.findViewById(R.id.button_cancel_confirm);
        btnConfirm = mParent.findViewById(R.id.button_confirm_sign_next);

        btnClose = mGroupButton.findViewById(R.id.chainverse_button_close);

        btnBackStep = mBackStep.findViewById(R.id.button_back_step);
        txtStep = mBackStep.findViewById(R.id.text_step);

        setValue();
    }

    private void setValue() {
        Long gasLimit = transactionData.getGasLimit();
        Long gasPrice = transactionData.getGasPrice();

        txtStep.setText(getTitle(transactionData.getType()));

//        DisplayMetrics metrics = getResources().getDisplayMetrics();

        if (transactionData.getType().equals(Constants.EFunction.signMessage)) {
            txtFrom.setText(transactionData.getFrom());
//            viewFrom.getLayoutParams().width = metrics.widthPixels - 200;
        } else {
            txtFrom.setText(Utils.shortAddress(transactionData.getFrom()));
        }

        if (transactionData.getAsset() != null && !transactionData.getAsset().isEmpty()) {
            viewAsset.setVisibility(View.VISIBLE);
            txtAsset.setText(transactionData.getAsset());
        }
        if (transactionData.getReceiver() != null) {
            viewTo.setVisibility(View.VISIBLE);
            viewArrow.setVisibility(View.VISIBLE);
            txtTo.setText(Utils.shortAddress(transactionData.getReceiver()));
        } else {
//            viewFrom.getLayoutParams().width = metrics.widthPixels - 200;
            txtFrom.setText(transactionData.getFrom());
        }

        if (transactionData.getPrice() != null) {
            viewValue.setVisibility(View.VISIBLE);
            txtValue.setText(transactionData.getPrice().toString() + " " + transactionData.getSymbol());
        }

        if (transactionData.getMessage() != null) {
            viewValue.setVisibility(View.VISIBLE);
            txtValue.setText(transactionData.getMessage());
        }

        if (gasLimit != null && gasPrice != null) {
            int decimal = transactionData.getDecimals() == null ? 18 : transactionData.getDecimals();
            BigDecimal decimals = BigDecimal.valueOf(Math.pow(10, -decimal));
            BigDecimal gas = Convert.fromWei(gasLimit.toString(), Convert.Unit.WEI).multiply(Convert.fromWei(gasPrice.toString(), Convert.Unit.WEI));
            BigDecimal fee = gas.multiply(decimals);

            viewNetworkFee.setVisibility(View.VISIBLE);
            txtFee.setText(fee.setScale(6, BigDecimal.ROUND_HALF_UP).toString() + " BNB");
        }

    }

    private String getTitle(Constants.EFunction function) {
        if (function == null) return "Confirm Transaction";
        switch (function) {
            case approveNFT:
            case approveToken:
                return "Approve";
            case signMessage:
                return "Sign Message";
            case transferItem:
            case transferToken:
                return "Transfer";
            default:
                return "Confirm Transaction";

        }
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

    private void sendTransaction() {
        Web3j web3 = Web3j.build(new HttpService(ServiceManager.getInstance().init(getContext()).getRPC()));
        WalletUtils walletUtils = WalletUtils.getInstance().init(getContext());
        Credentials credentials = walletUtils.getCredential();

        RawTransactionManager rawTransactionManager = new RawTransactionManager(web3, credentials);
        RawTransaction rawTransaction = RawTransaction.createTransaction(
                BigInteger.valueOf(transactionData.getNonce()),
                BigInteger.valueOf(transactionData.getGasPrice()),
                BigInteger.valueOf(transactionData.getGasLimit()),
                transactionData.getTo(),
                BigInteger.ZERO,
                transactionData.getData());

        String signedTransaction = rawTransactionManager.sign(rawTransaction);
        EthSendTransaction sendRawTransaction = null;
        try {
            sendRawTransaction = web3.ethSendRawTransaction(signedTransaction).sendAsync().get();
        } catch (ExecutionException e) {
            BroadcastUtil.send(getContext(), Constants.ACTION.DIMISS_LOADING);
            CallbackToGame.onErrorTransaction(transactionData.getType(), e.getLocalizedMessage());
        } catch (InterruptedException e) {
            BroadcastUtil.send(getContext(), Constants.ACTION.DIMISS_LOADING);
            CallbackToGame.onErrorTransaction(transactionData.getType(), e.getLocalizedMessage());
        }

        if (sendRawTransaction.hasError()) {
            CallbackToGame.onErrorTransaction(transactionData.getType(), sendRawTransaction.getError().getMessage());
        }

        String tx = sendRawTransaction.getTransactionHash();

        CallbackToGame.onTransact(transactionData.getType(), tx);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_cancel_confirm) {
            CallbackToGame.onErrorTransaction(transactionData.getType(), "Cancel");
            getActivity().finish();
        } else if (view.getId() == R.id.button_confirm_sign_next) {
            Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.LOADING);
            getContext().startActivity(intent);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!transactionData.getType().equals(Constants.EFunction.signMessage)) {
                        sendTransaction();
                    } else {
                        String signed = "";
                        WalletUtils walletUtils = WalletUtils.getInstance().init(getContext());
                        if (isPersonal) {
                            signed = walletUtils.signPersonalMessage(transactionData.getMessage());
                        } else {
                            try {
                                signed = walletUtils.signMessage(transactionData.getMessage());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        CallbackToGame.onSignMessage(signed);
                    }
                    BroadcastUtil.send(getContext(), Constants.ACTION.DIMISS_LOADING);
                    getActivity().finish();
                }
            }, 500);

        } else if (view.getId() == R.id.chainverse_button_close) {
            getActivity().finish();
        } else if (view.getId() == R.id.button_back_step) {
            getActivity().finish();
        }
    }
}
