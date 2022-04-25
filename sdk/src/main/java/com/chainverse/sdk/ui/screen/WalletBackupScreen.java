package com.chainverse.sdk.ui.screen;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chainverse.sdk.R;
import com.chainverse.sdk.adapter.PhraseAdapter;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.EqualSpacingItemDecoration;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.listener.OnItemActionListener;
import com.chainverse.sdk.model.Phrase;
import com.chainverse.sdk.ui.ChainverseSDKActivity;

import java.util.ArrayList;


public class WalletBackupScreen extends Fragment implements View.OnClickListener {
    private RecyclerView phraseView;
    private PhraseAdapter adapter;
    private LinearLayout viewCopied, btnBackStep, btnNext;
    private RelativeLayout container_backup;
    private String type;
    private View mGroupButton, mBackStep, mParent;
    private TextView text_step, text_copy;
    private ImageButton btn_close;

    private String phrase;

    public WalletBackupScreen() {
        // Required empty public constructor
    }

    public static WalletBackupScreen NewInstance(String type) {
        Bundle args = new Bundle();
        args.putString("type", type);
        WalletBackupScreen fragment = new WalletBackupScreen();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        type = args.getString("type");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mParent = inflater.inflate(R.layout.wallet_backup, container, false);
        mGroupButton = inflater.inflate(R.layout.groupt_button, container, false);
        mBackStep = inflater.inflate(R.layout.back_step, container, false);

        findView();

        btnBackStep.setOnClickListener(this);
        viewCopied.setOnClickListener(this);
        btn_close.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        WalletUtils walletUtils = WalletUtils.getInstance().init(getContext());
        if (walletUtils.getMnemonic().isEmpty()) {
            phrase = walletUtils.genMnemonic(128, "");
        } else {
            phrase = walletUtils.getMnemonic();
        }


        initPhraseView();

        resizeView();
        return mParent;
    }

    private void findView() {
        viewCopied = mParent.findViewById(R.id.button_copy);
        container_backup = mParent.findViewById(R.id.container_backup);
        phraseView = mParent.findViewById(R.id.phrase_view);
        btnBackStep = mBackStep.findViewById(R.id.button_back_step);
        btnNext = mParent.findViewById(R.id.button_backup_next);
        text_step = mBackStep.findViewById(R.id.text_step);
        text_copy = mParent.findViewById(R.id.text_copy);
        btn_close = mGroupButton.findViewById(R.id.chainverse_button_close);

        if (type.equals("view")) {
            btnNext.setVisibility(View.GONE);
            text_step.setText("Back");
        } else {
            text_step.setText("Create Account");
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

        container_backup.addView(mGroupButton, lp_button);
        container_backup.addView(mBackStep, lp_back);
    }

    private void initPhraseView() {
        String[] phrases = phrase.split(" ");
        if (phrases.length > 0) {
            ArrayList<Phrase> phrasesList = new ArrayList<>();
            for (int i = 0; i < phrases.length; i++) {
                Phrase phrase = new Phrase();
                phrase.setBody(phrases[i]);
                phrase.setOrder(i + 1);
                phrase.setShow(true);
                phrasesList.add(phrase);
            }

            adapter = new PhraseAdapter(phrasesList, getContext(), "display");
            adapter.setOnItemActionListener(new OnItemActionListener() {
                @Override
                public void onItemClicked(int position, View v) {

                }

                @Override
                public void onItemLongClicked(int position, View v) {

                }
            });

            int column = 3;
            int orientation = this.getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                column = 6;
                DisplayMetrics metrics = getResources().getDisplayMetrics();
//                btnNext.getLayoutParams().width = metrics.widthPixels / 2;
            }


            GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(), column);
            phraseView.setLayoutManager(mLayoutManager);
            phraseView.addItemDecoration(new EqualSpacingItemDecoration(Utils.convertDPToPixels(getContext(), 10), EqualSpacingItemDecoration.GRID));
            phraseView.setAdapter(adapter);
            phraseView.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_back_step) {
            if (type.equals("view")) {
                getActivity().finish();
            } else {
                Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
                intent.putExtra("screen", Constants.SCREEN.CREATE_WALLET);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        } else if (v.getId() == R.id.button_copy) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("phase", phrase);
            clipboard.setPrimaryClip(clip);
            text_copy.setText("COPIED");
        } else if (v.getId() == R.id.chainverse_button_close) {
            getActivity().finish();
        } else if (v.getId() == R.id.button_backup_next) {
            Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.VERIFY_WALLET);
            getActivity().startActivity(intent);
            getActivity().finish();
        }
    }
}