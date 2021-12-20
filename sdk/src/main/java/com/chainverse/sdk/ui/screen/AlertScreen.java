package com.chainverse.sdk.ui.screen;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.chainverse.sdk.R;


public class AlertScreen extends Fragment implements View.OnClickListener{
    Button btnAgree, btnCancel;
    ImageButton btnClose;
    public AlertScreen() {
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
        View mParent =  inflater.inflate(R.layout.chainverse_screen_alert, container, false);
        btnAgree = mParent.findViewById(R.id.chainverse_button_agree);
        btnCancel = mParent.findViewById(R.id.chainverse_button_cancel);
        btnClose = mParent.findViewById(R.id.chainverse_button_close);
        btnAgree.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        return mParent;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chainverse_button_agree) {
            getActivity().finish();
        }else if (v.getId() == R.id.chainverse_button_cancel) {
            getActivity().finish();
        }else if(v.getId() == R.id.chainverse_button_close){
            getActivity().finish();
        }
    }
}