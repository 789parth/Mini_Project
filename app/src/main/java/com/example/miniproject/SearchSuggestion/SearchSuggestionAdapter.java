package com.example.miniproject.SearchSuggestion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.miniproject.R;

import java.util.ArrayList;
import java.util.List;

public class SearchSuggestionAdapter extends RecyclerView.Adapter<SearchSuggestionAdapter.MyViewHolder> {

    public interface OnSuggestionClick {
        void onClick(SearchSuggestionModel suggestion);
    }

    private final List<SearchSuggestionModel> list = new ArrayList<>();
    private final OnSuggestionClick listener;

    public SearchSuggestionAdapter(OnSuggestionClick listener) {
        this.listener = listener;
    }

    public void updateList(List<SearchSuggestionModel> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_suggestion, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SearchSuggestionModel item = list.get(position);

        holder.title.setText(item.getTitle());
        holder.subtitle.setText(item.getSubtitle());

        Glide.with(holder.image.getContext())
                .load(item.getImage())
                .placeholder(R.drawable.menu1)
                .centerCrop()
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, subtitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.suggestionImage);
            title = itemView.findViewById(R.id.suggestionTitle);
            subtitle = itemView.findViewById(R.id.suggestionSubtitle);
        }
    }
}