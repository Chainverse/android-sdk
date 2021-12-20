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
import android.widget.LinearLayout;

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


public class WalletBackupScreen extends Fragment implements View.OnClickListener{
    private Button btnClose, btnNext, btnCopy;
    private RecyclerView phraseView;
    private PhraseAdapter adapter;
    private LinearLayout viewCopied;
    private String type;
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
        View mParent =  inflater.inflate(R.layout.chainverse_screen_wallet_backup, container, false);
        btnClose = mParent.findViewById(R.id.chainverse_button_close);
        btnNext = mParent.findViewById(R.id.chainverse_button_next);
        btnCopy = mParent.findViewById(R.id.chainverse_button_copy);
        phraseView = mParent.findViewById(R.id.chainverse_phraseview);
        viewCopied = mParent.findViewById(R.id.chainverse_view_copied);
        btnClose.setOnClickListener(this);
        btnCopy.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        btnNext.setVisibility(View.VISIBLE);
        if(type.equals("view")){
            btnNext.setVisibility(View.GONE);
        }
        initPhraseView();
        return mParent;
    }

    private void initPhraseView(){
        String[] phrases = WalletUtils.getInstance().init(getContext()).getMnemonic().split(" ");
        if(phrases.length > 0){
            ArrayList<Phrase> phrasesList = new ArrayList<>();
            for(int i = 0; i < phrases.length; i++){
                Phrase phrase = new Phrase();
                phrase.setBody(phrases[i]);
                phrase.setOrder(i+1);
                phrase.setShow(true);
                phrasesList.add(phrase);
            }

            adapter = new PhraseAdapter(phrasesList, getContext(),"display");
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
                btnNext.getLayoutParams().width = metrics.widthPixels / 2;
            }


            GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),column);
            phraseView.setLayoutManager(mLayoutManager);
            phraseView.addItemDecoration(new EqualSpacingItemDecoration(Utils.convertDPToPixels(getContext(),10), EqualSpacingItemDecoration.GRID));
            phraseView.setAdapter(adapter);
            phraseView.setVisibility(View.VISIBLE);

        }
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.chainverse_button_close){
            getActivity().finish();
        }else if(v.getId() == R.id.chainverse_button_copy){
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("phase", WalletUtils.getInstance().init(getContext()).getMnemonic());
            clipboard.setPrimaryClip(clip);
            btnCopy.setVisibility(View.GONE);
            viewCopied.setVisibility(View.VISIBLE);
        }else if(v.getId() == R.id.chainverse_button_next){
            Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.VERIFY_WALLET);
            getActivity().startActivity(intent);
            getActivity().finish();

        }
    }
}