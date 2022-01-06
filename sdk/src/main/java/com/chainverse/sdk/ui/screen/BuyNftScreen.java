package com.chainverse.sdk.ui.screen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.R;
import com.chainverse.sdk.base.web3.BaseWeb3;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.manager.ContractManager;

import java.math.BigDecimal;
import java.math.BigInteger;

public class BuyNftScreen extends Fragment implements View.OnClickListener {
    Button btnAgree, btnCancel;
    ImageButton btnClose;

    TextView tvData, txtLoading, txtError;

    boolean isApproved = false;
    boolean isEnough = false;
    boolean isAuction = false;
    String type;
    String currency;
    Long listingId;
    Double price;
    String address;
    ProgressDialog progress;
    EncryptPreferenceUtils encryptPreferenceUtils;

    public BuyNftScreen() {
        // Required empty public constructor
    }

    public static BuyNftScreen NewInstance(String type, String currency, Long listingId, Double price, boolean isAuction) {
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putString("currency", currency);
        args.putLong("listing_id", listingId);
        args.putDouble("price", price);
        args.putBoolean("isAuction", isAuction);
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
        isAuction = args.getBoolean("isAuction");
        address = WalletUtils.getInstance().init(this.getContext()).getAddress();
        encryptPreferenceUtils = EncryptPreferenceUtils.getInstance().init(this.getContext());

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

        progress = new ProgressDialog(this.getContext());
        progress.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                BuyNftScreen.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkApproved();
                        checkBalance();
                        progress.dismiss();
                    }
                });
            }
        }).start();

        btnAgree.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        return mParent;
    }

    private void checkBalance() {
        BigDecimal balance = null;
        try {
            if (currency.equals(Constants.CONTRACT.NativeCurrency)) {

                balance = BaseWeb3.getInstance().getBalance(address);
            } else {
                balance = new ContractManager(this.getContext()).balanceOf(currency, address);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (balance == null) {
            txtError.setVisibility(View.VISIBLE);
            txtError.setText("Your balance is not enough!");
        } else if (balance.doubleValue() <= price) {
            txtError.setVisibility(View.VISIBLE);
            txtError.setText("Your balance is not enough!");
        } else {
            isEnough = true;
        }
    }

    private void checkApproved() {
        System.out.println(currency);
        if (currency.equals(Constants.CONTRACT.NativeCurrency)) {
            isApproved = true;
            if (isAuction) {
                tvData.setText("Bid NFT");
                btnAgree.setText("Bid");
            } else {
                tvData.setText("Buy NFT");
                btnAgree.setText("Buy now");
            }

        } else {
            BigInteger allowance = new ContractManager(this.getContext()).allowance(currency, address, Constants.CONTRACT.MarketService);
            Double priceFormat = price * Math.pow(10, 18);

            if (allowance.doubleValue() < priceFormat) {
                isApproved = false;
                tvData.setText("Do you want to approve your token?");
            } else {
                isApproved = true;
                if (isAuction) {
                    tvData.setText("Bid NFT");
                    btnAgree.setText("Bid");
                } else {
                    tvData.setText("Buy NFT");
                    btnAgree.setText("Buy now");
                }
            }
        }
    }

    public Boolean isUserConnected() {
        if (!encryptPreferenceUtils.getXUserSignature().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.chainverse_button_agree_buy) {
            if (!isUserConnected()) {
                txtError.setText("Chưa đăng nhập");
            } else {
                if (isEnough) {
                    Double priceFormat = price * Math.pow(10, 18);
                    progress = new ProgressDialog(this.getContext());
                    progress.setTitle("Loading");
                    progress.setMessage("Wait while loading...");

                    progress.show();

                    Context context = this.getContext();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isApproved) {
                                String tx = null;
                                txtLoading.setVisibility(View.VISIBLE);
                                if (isAuction) {
                                    txtLoading.setText("Updating...");
                                } else {
                                    tx = new ContractManager(context).buyNFT(currency, BigInteger.valueOf(listingId), BigDecimal.valueOf(priceFormat).toBigInteger());
                                }
                                if (tx != null) {
                                    tvData.setText("Your transaction: " + tx);
                                    txtLoading.setText("Buy Successfully");
                                    btnAgree.setVisibility(View.GONE);
                                    if (ChainverseSDK.getInstance().mCallback != null) {
                                        ChainverseSDK.getInstance().mCallback.onBuy(tx);
                                    }
                                } else {
                                    txtLoading.setVisibility(View.GONE);
                                    txtError.setText("Transaction Error!");
                                    txtError.setVisibility(View.VISIBLE);
                                    tvData.setVisibility(View.GONE);
                                }
                                progress.dismiss();
                            } else {
                                String tx = new ContractManager(context).approved(currency, Constants.CONTRACT.MarketService, BigDecimal.valueOf(priceFormat).toBigInteger());
                                if (tx != null || !tx.isEmpty()) {
                                    btnAgree.setText("Buy now");
                                    tvData.setText("Do you want to sign to buy the NFT");
                                    isApproved = true;
                                }
                                progress.dismiss();
                            }
                        }
                    }, 500);
                }
            }
        } else if (view.getId() == R.id.chainverse_button_cancel_buy) {
            getActivity().finish();
        } else if (view.getId() == R.id.chainverse_button_close_buy) {
            getActivity().finish();
        }
    }
}
