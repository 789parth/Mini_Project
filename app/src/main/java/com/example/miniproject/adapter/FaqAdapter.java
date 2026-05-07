package com.example.miniproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject.R;
import com.example.miniproject.domain.FaqModel;

import java.util.ArrayList;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {

    private final ArrayList<FaqModel> faqList;
    // Track which positions are expanded
    private final ArrayList<Boolean> expandedStates;

    public FaqAdapter(ArrayList<FaqModel> faqList) {
        this.faqList = faqList;
        this.expandedStates = new ArrayList<>();
        for (int i = 0; i < faqList.size(); i++) {
            expandedStates.add(false);
        }
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faq_item, parent, false);
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        FaqModel faq = faqList.get(position);
        boolean isExpanded = expandedStates.get(position);

        holder.tvQuestion.setText(faq.getQuestion());
        holder.tvAnswer.setText(faq.getAnswer());

        // Set expanded/collapsed state
        holder.tvAnswer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.ivArrow.setImageResource(isExpanded
                ? android.R.drawable.arrow_up_float
                : android.R.drawable.arrow_down_float);

        holder.layoutQuestion.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_ID) return;
            boolean expanded = expandedStates.get(pos);
            expandedStates.set(pos, !expanded);
            notifyItemChanged(pos);
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public void updateList(ArrayList<FaqModel> newList) {
        faqList.clear();
        faqList.addAll(newList);
        expandedStates.clear();
        for (int i = 0; i < newList.size(); i++) {
            expandedStates.add(false);
        }
        notifyDataSetChanged();
    }

    static class FaqViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutQuestion;
        TextView tvQuestion, tvAnswer;
        ImageView ivArrow;

        FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutQuestion = itemView.findViewById(R.id.layoutFaqQuestion);
            tvQuestion     = itemView.findViewById(R.id.tvFaqQuestion);
            tvAnswer       = itemView.findViewById(R.id.tvFaqAnswer);
            ivArrow        = itemView.findViewById(R.id.ivFaqArrow);
        }
    }
}
