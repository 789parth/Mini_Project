package com.example.miniproject.CartManagment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class CartDiffUtil extends DiffUtil.ItemCallback<CartModel> {

    @Override
    public boolean areItemsTheSame(@NonNull CartModel oldItem, @NonNull CartModel newItem) {
        return oldItem.getProductId() != null
                && oldItem.getProductId().equals(newItem.getProductId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull CartModel oldItem, @NonNull CartModel newItem) {
        return safeEquals(oldItem.getProduct_image(), newItem.getProduct_image())
                && safeEquals(oldItem.getProduct_name(), newItem.getProduct_name())
                && oldItem.getProduct_price() == newItem.getProduct_price()
                && oldItem.getQuantity() == newItem.getQuantity();
    }

    private boolean safeEquals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}
