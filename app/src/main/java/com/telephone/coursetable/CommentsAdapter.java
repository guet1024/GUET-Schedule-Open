package com.telephone.coursetable;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.telephone.coursetable.Clock.Clock;
import com.telephone.coursetable.Gson.Comment.Comment;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private List<Comment> data;

    public CommentsAdapter(@NonNull List<Comment> data){
        this.data = data;
    }

    public void setData(@NonNull List<Comment> data){
        this.data = data;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MaterialCardView mcv = (MaterialCardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_card, parent, false);
        return new CommentsViewHolder(mcv);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        Comment cmt = data.get(position);
        if (!cmt.getMask().isEmpty()){
            ((TextView) holder.mcv.findViewById(R.id.comment_card_sid)).setText("[匿名] " + cmt.getMask());
        }else {
            ((TextView) holder.mcv.findViewById(R.id.comment_card_sid)).setText(cmt.getSno() + cmt.getName());
        }
        ((TextView)holder.mcv.findViewById(R.id.comment_card_comment)).setText(cmt.getCmt());
        ((TextView)holder.mcv.findViewById(R.id.comment_card_dt)).setText(cmt.getDt() + "（" + Clock.comment_past_time(cmt.getTimeStamp()) + "）");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public MaterialCardView mcv;
        public CommentsViewHolder(MaterialCardView mcv) {
            super(mcv);
            this.mcv = mcv;
        }
    }

}

