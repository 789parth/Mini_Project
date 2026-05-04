package com.example.miniproject.Category;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class CategoryDiffCallback extends DiffUtil.Callback {

    private final List<CategoryModel> oldList;
    private final List<CategoryModel> newList;

    public CategoryDiffCallback(List<CategoryModel> oldList, List<CategoryModel> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getCategory_id()
                .equals(newList.get(newItemPosition).getCategory_id());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        CategoryModel oldItem = oldList.get(oldItemPosition);
        CategoryModel newItem = newList.get(newItemPosition);

        return oldItem.getCategory_title().equals(newItem.getCategory_title())
                && oldItem.getCategory_image().equals(newItem.getCategory_image())
                && oldItem.getStore_id().equals(newItem.getStore_id());
    }
}
