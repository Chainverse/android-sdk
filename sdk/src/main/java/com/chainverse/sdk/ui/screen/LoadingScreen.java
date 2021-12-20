package com.chainverse.sdk.ui.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chainverse.sdk.R;
import com.chainverse.sdk.common.Constants;


public class LoadingScreen extends Fragment implements View.OnClickListener{
    private BroadcastReceiver receiver;
    public LoadingScreen() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mParent =  inflater.inflate(R.layout.chainverse_screen_loading, container, false);
        initReceiver();
        return mParent;
    }

    private void initReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION.DIMISS_LOADING);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.ACTION.DIMISS_LOADING)) {
                    getActivity().finish();
                }
            }
        };
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(receiver, filter);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

}