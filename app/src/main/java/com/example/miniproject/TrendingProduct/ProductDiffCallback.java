package com.example.miniproject.TrendingProduct;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class ProductDiffCallback extends DiffUtil.Callback {

    private final List<ProductModel> oldList;
    private final List<ProductModel> newList;

    public ProductDiffCallback(List<ProductModel> oldList, List<ProductModel> newList) {
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
        return oldList.get(oldItemPosition).getProduct_id()
                .equals(newList.get(newItemPosition).getProduct_id());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ProductModel oldItem = oldList.get(oldItemPosition);
        ProductModel newItem = newList.get(newItemPosition);

        return oldItem.getProduct_name().equals(newItem.getProduct_name())
                && oldItem.getProduct_description().equals(newItem.getProduct_description())
                && oldItem.getProduct_image().equals(newItem.getProduct_image())
                && oldItem.getProduct_price() == newItem.getProduct_price()
                && oldItem.getProduct_quantity() == newItem.getProduct_quantity()
                && oldItem.getProduct_unit().equals(newItem.getProduct_unit())
                && oldItem.isTrending_item() == newItem.isTrending_item();
    }
}