package com.chainverse.sdk.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chainverse.sdk.R;
import com.chainverse.sdk.listener.OnItemActionListener;
import com.chainverse.sdk.model.Phrase;

import java.util.ArrayList;

public class PhraseAdapter extends RecyclerView.Adapter<PhraseAdapter.PhraseHolder> {
    private Context mContext;
    private ArrayList<Phrase> phrases = new ArrayList<>();
    private String type;
    private OnItemActionListener clickListener;

    public PhraseAdapter(ArrayList<Phrase> phrases, Context mContext, String type) {
        this.phrases = phrases;
        this.mContext = mContext;
        this.type = type;
    }


    public void setOnItemActionListener(OnItemActionListener clickListener) {
        this.clickListener = clickListener;
    }

    class PhraseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvPhrase,tvNum;
        private LinearLayout viewPhrase;
        private String type;
        private OnItemActionListener clickListener;
        PhraseHolder(@NonNull View itemView, String type) {
            super(itemView);
            this.type = type;
            itemView.setOnClickListener(this);
            tvPhrase = itemView.findViewById(R.id.chainverse_tvphrase);
            tvNum = itemView.findViewById(R.id.chainverse_tvnum);
            viewPhrase = itemView.findViewById(R.id.chainverse_view_phrase);
            tvNum.setVisibility(View.GONE);
            if(type.equals("display")){
                tvNum.setVisibility(View.VISIBLE);
            }

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
    public PhraseAdapter.PhraseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chainverse_phrase_item, parent, false);
        PhraseAdapter.PhraseHolder holder = new PhraseHolder(view,type);
        holder.setOnItemActionListener(clickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhraseAdapter.PhraseHolder holder, int position) {
        Phrase item = phrases.get(position);
        holder.tvPhrase.setText(item.getBody());
        holder.tvNum.setText(""+ item.getOrder());
        holder.tvPhrase.setVisibility(View.VISIBLE);
        holder.viewPhrase.setBackgroundResource(R.drawable.chainverse_background_phrase_item);
        if(!item.isShow()){
            holder.tvPhrase.setVisibility(View.INVISIBLE);
            holder.viewPhrase.setBackgroundResource(R.drawable.chainverse_background_phrase_choose_item);
        }
    }


    @Override
    public int getItemCount() {
        return phrases.size();
    }

}
