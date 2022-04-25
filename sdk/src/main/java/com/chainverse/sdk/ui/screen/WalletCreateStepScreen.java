package com.chainverse.sdk.ui.screen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.chainverse.sdk.R;
import com.chainverse.sdk.common.Constants;
import com.chainverse.sdk.common.WalletUtils;
import com.chainverse.sdk.ui.ChainverseSDKActivity;

public class WalletCreateStepScreen extends Fragment implements View.OnClickListener {
    View mGroupButton, mParent, mBackStep;
    ImageButton btnClose;
    TextView txtStep;
    Button btnStep1, btnStep2, btnStep3;
    RelativeLayout container;
    LinearLayout container_step, button_next, btnBackStep;
    ImageView icon_arrow_right;

    private int step = 0;
    private boolean isCheck1 = false;
    private boolean isCheck2 = false;
    private boolean isCheck3 = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mParent = inflater.inflate(R.layout.create_wallet_step, container, false);
        mGroupButton = inflater.inflate(R.layout.groupt_button, container, false);
        mBackStep = inflater.inflate(R.layout.back_step, container, false);

        findView();

        setTextStep();

        btnClose.setOnClickListener(this);
        btnBackStep.setOnClickListener(this);
        btnStep1.setOnClickListener(this);
        btnStep2.setOnClickListener(this);
        btnStep3.setOnClickListener(this);
        button_next.setOnClickListener(this);

        resizeView();

        return mParent;
    }

    private void findView() {
        container = mParent.findViewById(R.id.create_wallet_step_container);
        container_step = mParent.findViewById(R.id.container_step);
        btnStep1 = mParent.findViewById(R.id.btn_step1);
        btnStep2 = mParent.findViewById(R.id.btn_step2);
        btnStep3 = mParent.findViewById(R.id.btn_step3);
        button_next = mParent.findViewById(R.id.button_next);
        icon_arrow_right = mParent.findViewById(R.id.icon_arrow_right);

        btnClose = mGroupButton.findViewById(R.id.chainverse_button_close);

        btnBackStep = mBackStep.findViewById(R.id.button_back_step);
        txtStep = mBackStep.findViewById(R.id.text_step);

        button_next.setEnabled(false);
    }

    private void setTextStep() {
        switch (step) {
            case 0:
                txtStep.setText("Back");
                break;
            case 1:
                txtStep.setText("Create Account");
                break;
            case 2:
                txtStep.setText("Recovery Phrase");
                break;
            default:
                break;
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

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        btnStep1.getLayoutParams().width = metrics.widthPixels / 5;
        btnStep1.getLayoutParams().height = metrics.widthPixels / 5;
        btnStep1.setTextSize(this.getSizeText(metrics));

        btnStep2.getLayoutParams().width = metrics.widthPixels / 5;
        btnStep2.getLayoutParams().height = metrics.widthPixels / 5;
        btnStep2.setTextSize(this.getSizeText(metrics));

        btnStep3.getLayoutParams().width = metrics.widthPixels / 5;
        btnStep3.getLayoutParams().height = metrics.widthPixels / 5;
        btnStep3.setTextSize(this.getSizeText(metrics));

        if (metrics.heightPixels > 1200) {
            LinearLayout.LayoutParams lp_container_step = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            lp_container_step.setMargins(0, metrics.heightPixels / 5, 0, 0);
            container_step.setLayoutParams(lp_container_step);
        }
    }

    private float getSizeText(DisplayMetrics metrics) {
        System.out.println(metrics.widthPixels);
        float size;
        if (0 < metrics.widthPixels && metrics.widthPixels <= 768) {
            size = 6;
        } else if (768 < metrics.widthPixels && metrics.widthPixels <= 1080) {
            size = 7;
        } else {
            size = 15;
        }

        return size;
    }

    public void setActionStep(int check) {
        switch (check) {
            case 1:
                this.isCheck1 = !this.isCheck1;
                if (this.isCheck1) {
                    btnStep1.setBackground(getContext().getDrawable(R.drawable.background_check));
                } else {
                    btnStep1.setBackground(getContext().getDrawable(R.drawable.background_uncheck));
                }
                break;
            case 2:
                this.isCheck2 = !this.isCheck2;
                if (this.isCheck2) {
                    btnStep2.setBackground(getContext().getDrawable(R.drawable.background_check));
                } else {
                    btnStep2.setBackground(getContext().getDrawable(R.drawable.background_uncheck));
                }
                break;
            case 3:
                this.isCheck3 = !this.isCheck3;
                if (this.isCheck3) {
                    btnStep3.setBackground(getContext().getDrawable(R.drawable.background_check));
                } else {
                    btnStep3.setBackground(getContext().getDrawable(R.drawable.background_uncheck));
                }
                break;
            default:
                break;
        }
        if (this.isCheck1 && this.isCheck2 && this.isCheck3) {
            button_next.setEnabled(true);
            button_next.setBackgroundTintList(getContext().getColorStateList(R.color.ChainverseColorPrimary));
            icon_arrow_right.setColorFilter(Color.parseColor("#0046FF"));
        } else {
            button_next.setEnabled(false);
            button_next.setBackgroundTintList(getContext().getColorStateList(R.color.ColorDisable));
            icon_arrow_right.setColorFilter(Color.parseColor("#838A97"));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.chainverse_button_close) {
            getActivity().finish();
        } else if (view.getId() == R.id.button_back_step) {
            switch (step) {
                case 0:
                    getActivity().finish();
                    break;
                case 1:
                    step = 0;
                    break;
                case 2:
                    step = 1;
                    break;
                default:
                    break;
            }
            setTextStep();
        } else if (view.getId() == R.id.btn_step1) {
            setActionStep(1);
        } else if (view.getId() == R.id.btn_step2) {
            setActionStep(2);
        } else if (view.getId() == R.id.btn_step3) {
            setActionStep(3);
        } else if (view.getId() == R.id.button_next) {
            WalletUtils.getInstance().init(getContext()).removeMnemonic();
            Intent intent = new Intent(getContext(), ChainverseSDKActivity.class);
            intent.putExtra("screen", Constants.SCREEN.BACKUP_WALLET);
            getActivity().startActivity(intent);
            getActivity().finish();
        }
    }
}
