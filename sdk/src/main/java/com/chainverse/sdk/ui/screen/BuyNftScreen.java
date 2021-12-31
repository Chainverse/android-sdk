package com.chainverse.sdk.ui.screen;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.chainverse.sdk.R;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.manager.ContractManager;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BuyNftScreen extends Fragment implements View.OnClickListener {
    Button btnAgree, btnCancel;
    ImageButton btnClose;

    TextView tvData, txtLoading, txtError;

    boolean isApproved = false;
    String type;
    String currency;
    Long listingId;
    Double price;
    String address;

    public BuyNftScreen() {
        // Required empty public constructor
    }

    public static BuyNftScreen NewInstance(String type, String currency, Long listingId, Double price) {
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("currency", currency);
        args.putLong("listing_id", listingId);
        args.putDouble("price", price);
        BuyNftScreen fragment = new BuyNftScreen();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        type = args.getString("type");
        currency = args.getString("currency");
        listingId = args.getLong("listing_id");
        price = args.getDouble("price");
        address = WalletUtils.getInstance().init(this.getContext()).getAddress();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mParent = inflater.inflate(R.layout.chainverse_screen_buy_nft, container, false);
        btnAgree = mParent.findViewById(R.id.chainverse_button_agree_buy);
        btnCancel = mParent.findViewById(R.id.chainverse_button_cancel_buy);
        btnClose = mParent.findViewById(R.id.chainverse_button_close_buy);
        tvData = mParent.findViewById(R.id.chainverse_tv_data_buy);
        txtLoading = mParent.findViewById(R.id.txtLoading);
        txtError = mParent.findViewById(R.id.txtError);

        checkApproved();

        btnAgree.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        return mParent;
    }

    private void checkApproved() {
        System.out.println(currency);
        if (currency.equals(Constants.CONTRACT.NativeCurrency)) {
            isApproved = true;
            tvData.setText("Buy NFT");
            btnAgree.setText("Buy now");
        } else {
            BigInteger allowance = new ContractManager(this.getContext()).allowance(currency, address, Constants.CONTRACT.MarketService);
            Double priceFormat = price * Math.pow(10,18);

            if (allowance.doubleValue() < priceFormat) {
                isApproved = false;
                tvData.setText("Do you want to approve your token?");
            } else {
                isApproved = true;
                tvData.setText("Buy NFT");
                btnAgree.setText("Buy now");
            }
        }


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.chainverse_button_agree_buy) {
            Double priceFormat = price * Math.pow(10,18);
            if (isApproved) {
                txtLoading.setVisibility(View.VISIBLE);
                String tx = new ContractManager(this.getContext()).buyNFT(currency, BigInteger.valueOf(listingId), BigDecimal.valueOf(priceFormat).toBigInteger());
                if (tx != null) {
                    txtLoading.setText("Buy Success!");
                } else {
                    txtLoading.setVisibility(View.GONE);
                    txtError.setText("Transaction Error!");
                    txtError.setVisibility(View.VISIBLE);
                }
            } else {
                String tx = new ContractManager(this.getContext()).approved(currency, Constants.CONTRACT.MarketService, BigDecimal.valueOf(priceFormat).toBigInteger());
                System.out.println(tx);
                if (tx != null || !tx.isEmpty()) {
                    btnAgree.setText("Buy now");
                    isApproved = true;
                }
            }
        } else if (view.getId() == R.id.chainverse_button_cancel_buy) {
            getActivity().finish();
        } else if (view.getId() == R.id.chainverse_button_close_buy) {
            getActivity().finish();
        }
    }
}
