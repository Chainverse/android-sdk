package com.chainverse.sdk.ui.screen;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chainverse.sdk.R;
import com.chainverse.sdk.adapter.PhraseAdapter;
import com.chainverse.sdk.adapter.PhraseVerifyAdapter;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.EncryptPreferenceUtils;
import com.chainverse.sdk.common.EqualSpacingItemDecoration;
import com.chainverse.sdk.common.Utils;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.listener.OnItemActionListener;
import com.chainverse.sdk.model.Phrase;
import com.chainverse.sdk.ui.ChainverseSDKActivity;

import java.util.ArrayList;
import java.util.Collections;


public class WalletVerifyScreen extends Fragment implements View.OnClickListener{
    private Button btnClose, btnVerify;
    private RecyclerView phraseViewRandom, phraseViewVerify;
    private PhraseAdapter adapterRandom;
    private PhraseVerifyAdapter adapterVerify;
    private TextView tvMessageError;
    int phraseVerifyPosition = 0;
    private ArrayList<Phrase> phrasesRandom = new ArrayList<>();
    private ArrayList<Phrase> phrasesVerify = new ArrayList<>();
    private ArrayList<Phrase> phrasesChoose = new ArrayList<>();
    private String mnemonicChoose = "";
    public WalletVerifyScreen() {
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
        View mParent =  inflater.inflate(R.layout.chainverse_screen_wallet_verify, container, false);
        btnClose = mParent.findViewById(R.id.chainverse_button_close);
        btnVerify = mParent.findViewById(R.id.chainverse_button_verify);
        phraseViewRandom = mParent.findViewById(R.id.chainverse_phraseview_random);
        phraseViewVerify = mParent.findViewById(R.id.chainverse_phraseview_verify);
        tvMessageError = mParent.findViewById(R.id.chainverse_message_error);
        btnClose.setOnClickListener(this);

        setupDataPhrase();
        initPhraseViewVerify();
        initPhraseViewRandom();
        return mParent;
    }

    private void setupDataPhrase(){
        String[] phrasesArr = WalletUtils.getInstance().init(getContext()).getMnemonic().split(" ");
        if(phrasesArr.length > 0){
            for(int i = 0; i < phrasesArr.length; i++){
                Phrase phraseVerify = new Phrase();
                phraseVerify.setBody(phrasesArr[i]);
                phraseVerify.setOrder(i+1);
                phraseVerify.setShow(false);
                phrasesVerify.add(phraseVerify);

                Phrase phraseRandom = new Phrase();
                phraseRandom.setBody(phrasesArr[i]);
                phraseRandom.setOrder(i+1);
                phraseRandom.setShow(true);
                phrasesRandom.add(phraseRandom);
            }
        }
    }

    private void initPhraseViewVerify(){
        adapterVerify = new PhraseVerifyAdapter(phrasesVerify, getContext());
        adapterVerify.setOnItemActionListener(new OnItemActionListener() {
            @Override
            public void onItemClicked(int position, View v) {
                if(!phrasesVerify.get(position).isShow()){
                    return;
                }
                //remove
                if(phraseVerifyPosition > 0){
                    int pos = phrasesVerify.get(position).getPosition();
                    phrasesVerify.get(position).setShow(false);
                    handleDataVerifyAfterRemove(position);
                    adapterVerify.notifyDataSetChanged();
                    phrasesRandom.get(pos).setShow(true);
                    adapterRandom.notifyItemChanged(pos);

                    checkPhraseOrderAfterRemove(position);
                    phraseVerifyPosition--;
                    if(phraseVerifyPosition < phrasesVerify.size()){
                        btnVerify.setBackgroundResource(R.drawable.chainverse_background_button_wallet_create_default);
                        btnVerify.setEnabled(false);
                    }
                }

            }

            @Override
            public void onItemLongClicked(int position, View v) {

            }
        });

        int column = 3;
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            column = 6;
        }

        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),column);
        phraseViewVerify.setLayoutManager(mLayoutManager);
        phraseViewVerify.addItemDecoration(new EqualSpacingItemDecoration(Utils.convertDPToPixels(getContext(),10), EqualSpacingItemDecoration.GRID));
        phraseViewVerify.setAdapter(adapterVerify);
        phraseViewVerify.setVisibility(View.VISIBLE);
    }

    private void checkPhraseOrderAfterRemove(int position){
        phrasesChoose.remove(position);
        checkPhraseOrder();
    }

    private void checkPhraseOrder(){
        if(phrasesChoose.size() > 0){
            boolean isRight = false;
            for(int a = 0; a < phrasesChoose.size(); a++){
                if(phrasesChoose.get(a).getOrder() == phrasesVerify.get(a).getOrder()){
                    isRight = true;
                }else{
                    isRight = false;
                    break;
                }
            }

            if(isRight){
                tvMessageError.setVisibility(View.GONE);
            }else{
                tvMessageError.setVisibility(View.VISIBLE);
            }
        }else{
            tvMessageError.setVisibility(View.GONE);
        }


    }

    private void initPhraseViewRandom(){
        Collections.shuffle(phrasesRandom);
        adapterRandom = new PhraseAdapter(phrasesRandom, getContext(),"choose");
        adapterRandom.setOnItemActionListener(new OnItemActionListener() {
            @Override
            public void onItemClicked(int position, View v) {
                if(!phrasesRandom.get(position).isShow()){
                    return;
                }
                //Move to verify
                if(phraseVerifyPosition < phrasesVerify.size()){
                    String p = phrasesRandom.get(position).getBody();
                    phrasesVerify.get(phraseVerifyPosition).setBody(p);
                    phrasesVerify.get(phraseVerifyPosition).setShow(true);
                    phrasesVerify.get(phraseVerifyPosition).setPosition(position);
                    phrasesRandom.get(position).setShow(false);
                    adapterRandom.notifyItemChanged(position);
                    adapterVerify.notifyItemChanged(phraseVerifyPosition);

                    if(!phrasesChoose.contains(phrasesRandom.get(position))){
                        phrasesChoose.add(phrasesRandom.get(position));
                    }

                    checkPhraseOrder();
                    phraseVerifyPosition++;
                    if(phraseVerifyPosition == phrasesVerify.size()){
                        if(isVerifyMnemonic()){
                            btnVerify.setBackgroundResource(R.drawable.chainverse_background_button_wallet_create);
                            btnVerify.setEnabled(true);
                        }
                        btnVerify.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String xUserAddress = WalletUtils.getInstance().init(getContext()).getAddress();
                                EncryptPreferenceUtils.getInstance().init(getContext()).setXUserAddress(xUserAddress);
                                try {
                                    EncryptPreferenceUtils.getInstance().init(getContext()).setXUserSignature(WalletUtils.getInstance().init(getContext()).signMessage("chainverse"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent();
                                intent.setAction(Constants.ACTION.CREATED_WALLET);
                                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);

                                Intent intentAlert = new Intent(getContext(), ChainverseSDKActivity.class);
                                intentAlert.putExtra("screen", Constants.SCREEN.ALERT);
                                getActivity().startActivity(intentAlert);

                                getActivity().finish();

                            }
                        });
                    }
                }

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
            btnVerify.getLayoutParams().width = metrics.widthPixels / 2;
        }

        GridLayoutManager mLayoutManager = new GridLayoutManager(getContext(),column);
        phraseViewRandom.setLayoutManager(mLayoutManager);
        phraseViewRandom.addItemDecoration(new EqualSpacingItemDecoration(Utils.convertDPToPixels(getContext(),10), EqualSpacingItemDecoration.GRID));
        phraseViewRandom.setAdapter(adapterRandom);
        phraseViewRandom.setVisibility(View.VISIBLE);
    }

    private boolean isVerifyMnemonic(){
        mnemonicChoose = "";
        if(phrasesChoose.size() == phrasesVerify.size()){
            for(int i = 0; i < phrasesChoose.size();i++){
                if(i == 0){
                    mnemonicChoose = mnemonicChoose.concat(phrasesChoose.get(i).getBody());
                }else{
                    mnemonicChoose = mnemonicChoose.concat(" " + phrasesChoose.get(i).getBody());
                }
            }

            if(WalletUtils.getInstance().init(getContext()).getMnemonic().equals(mnemonicChoose)){
                return true;
            }

            return false;
        }
        return false;
    }

    private void handleDataVerifyAfterRemove(int position){
        if(phrasesVerify.size() > 0){
            for(int i = 0; i < phrasesVerify.size(); i++){
                if(i >= position){
                    if(i < phrasesVerify.size() - 1){
                        int pos = i + 1;
                        phrasesVerify.get(i).setBody(phrasesVerify.get(pos).getBody());
                        phrasesVerify.get(i).setPosition(phrasesVerify.get(pos).getPosition());
                        phrasesVerify.get(i).setShow(phrasesVerify.get(pos).isShow());
                    }else{
                        phrasesVerify.get(i).setBody("");
                        phrasesVerify.get(i).setPosition(0);
                        phrasesVerify.get(i).setShow(false);
                    }

                }

            }
        }
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.chainverse_button_close){
            getActivity().finish();
        }
    }
}