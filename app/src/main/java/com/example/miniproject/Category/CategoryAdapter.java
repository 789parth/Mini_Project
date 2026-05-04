package com.example.miniproject.Category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.miniproject.R;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<CategoryModel> categoryList = new ArrayList<>();

    public void updateList(List<CategoryModel> newList) {
        CategoryDiffCallback diffCallback =
                new CategoryDiffCallback(categoryList, newList);

        DiffUtil.DiffResult diffResult =
                DiffUtil.calculateDiff(diffCallback);

        categoryList.clear();
        categoryList.addAll(newList);

        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.categoryitems, parent, false);

        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryModel category = categoryList.get(position);

        holder.categoryTitle.setText(category.getCategory_title());

        Glide.with(holder.itemView.getContext())
                .load(category.getCategory_image())
                .centerCrop()
                .into(holder.categoryImage);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView categoryImage;
        TextView categoryTitle;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryImage = itemView.findViewById(R.id.imageView9);
            categoryTitle = itemView.findViewById(R.id.textView20);
        }
    }
}
