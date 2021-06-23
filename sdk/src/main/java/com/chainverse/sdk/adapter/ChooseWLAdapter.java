package com.chainverse.sdk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chainverse.sdk.R;
import com.chainverse.sdk.listener.OnChooseWLListenter;
import com.chainverse.sdk.model.WL;

import java.util.ArrayList;

public class ChooseWLAdapter extends RecyclerView.Adapter<ChooseWLAdapter.ChooseWLHolder>{
    private Context mContext;
    private ArrayList<WL> wls = new ArrayList<>();
    private OnChooseWLListenter listenter;
    public ChooseWLAdapter(ArrayList<WL> wls, Context context){
        this.wls = wls;
        mContext = context;
    }

    public void setListenter(OnChooseWLListenter listenter){
        this.listenter = listenter;
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseWLHolder holder, int position) {
        holder.setListenter(listenter);
    }

    @Override
    public int getItemCount() {
        return wls.size();
    }

    @NonNull
    @Override
    public ChooseWLHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.com_chainverse_sdk_choose_wl_item,parent, false);
        return new ChooseWLHolder(view);
    }


    class ChooseWLHolder extends RecyclerView.ViewHolder{
        private OnChooseWLListenter listenter;
        public ChooseWLHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listenter.onClickItem(getAdapterPosition(),view);
                }
            });
        }

        public void setListenter(OnChooseWLListenter listenter){
            this.listenter = listenter;
        }
    }
}
