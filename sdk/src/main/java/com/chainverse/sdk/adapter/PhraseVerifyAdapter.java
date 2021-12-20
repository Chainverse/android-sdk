package com.chainverse.sdk.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chainverse.sdk.R;
import com.chainverse.sdk.listener.OnItemActionListener;
import com.chainverse.sdk.model.Phrase;

import java.util.ArrayList;

public class PhraseVerifyAdapter extends RecyclerView.Adapter<PhraseVerifyAdapter.PhraseVerifyHolder> {
    private Context mContext;
    private ArrayList<Phrase> phrases = new ArrayList<>();
    private OnItemActionListener clickListener;

    public PhraseVerifyAdapter(ArrayList<Phrase> phrases, Context mContext) {
        this.phrases = phrases;
        this.mContext = mContext;
    }

    public void setOnItemActionListener(OnItemActionListener clickListener) {
        this.clickListener = clickListener;
    }

    class PhraseVerifyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvPhrase,tvNum;
        private OnItemActionListener clickListener;
        PhraseVerifyHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvPhrase = itemView.findViewById(R.id.chainverse_tvphrase);
            tvNum = itemView.findViewById(R.id.chainverse_tvnum);
        }

        void setOnItemActionListener(OnItemActionListener clickListener) {
            this.clickListener = clickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onItemClicked(getAdapterPosition(), view);
        }
    }

    @NonNull
    @Override
    public PhraseVerifyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chainverse_phrase_verify_item, parent, false);
        PhraseVerifyHolder holder = new PhraseVerifyHolder(view);
        holder.setOnItemActionListener(clickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhraseVerifyHolder holder, int position) {
        Phrase item = phrases.get(position);
        holder.tvPhrase.setText(item.getBody());
        holder.tvNum.setText(""+ item.getOrder());

        holder.tvPhrase.setVisibility(View.INVISIBLE);
        if(item.isShow()){
            holder.tvPhrase.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return phrases.size();
    }

}
