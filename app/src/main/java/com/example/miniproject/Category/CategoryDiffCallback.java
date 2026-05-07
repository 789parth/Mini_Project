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
        String oldId = oldList.get(oldItemPosition).getCategory_id();
        String newId = newList.get(newItemPosition).getCategory_id();

        if (oldId == null && newId == null) return true;
        if (oldId == null || newId == null) return false;

        return oldId.equals(newId);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        CategoryModel oldItem = oldList.get(oldItemPosition);
        CategoryModel newItem = newList.get(newItemPosition);

        return safeEquals(oldItem.getCategory_title(), newItem.getCategory_title())
                && safeEquals(oldItem.getCategory_image(), newItem.getCategory_image())
                && safeEquals(oldItem.getStore_id(), newItem.getStore_id())
                && oldItem.isHasProducts() == newItem.isHasProducts();
    }

    private boolean safeEquals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}