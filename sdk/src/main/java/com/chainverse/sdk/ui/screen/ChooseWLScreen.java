package com.chainverse.sdk.ui.screen;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chainverse.sdk.ChainverseSDK;
import com.chainverse.sdk.R;
import com.chainverse.sdk.adapter.ChooseWLAdapter;
import com.chainverse.sdk.listener.OnChooseWLListenter;
import com.chainverse.sdk.model.WL;
import com.chainverse.sdk.wl.config.SupportWL;

import java.util.ArrayList;

public class ChooseWLScreen extends Fragment {
    private RecyclerView mRecylerView;
    private ChooseWLAdapter adapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChooseWLScreen() {
        // Required empty public constructor
    }

    public static ChooseWLScreen newInstance(String param1, String param2) {
        ChooseWLScreen fragment = new ChooseWLScreen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mParent =  inflater.inflate(R.layout.com_chainverse_sdk_screen_choose_wl, container, false);
        mRecylerView = mParent.findViewById(R.id.recyclerView);
        initRecylerView();
        return mParent;
    }

    private void initRecylerView(){
        mRecylerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecylerView.setLayoutManager(layoutManager);
        adapter = new ChooseWLAdapter(SupportWL.getSupportWallets(), getActivity());
        adapter.setListenter(new OnChooseWLListenter() {
            @Override
            public void onClickItem(int position, View view) {
                ChainverseSDK.getInstance().connectTrustWL("com.chainverse.sample","accounts_callback");
            }
        });
        mRecylerView.setAdapter(adapter);

    }
}