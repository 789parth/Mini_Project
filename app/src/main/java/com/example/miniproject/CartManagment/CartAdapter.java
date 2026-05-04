package com.example.miniproject.CartManagment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.miniproject.R;

public class CartAdapter extends ListAdapter<CartModel, CartAdapter.CartViewHolder> {

    public interface CartClickListener {
        void onPlusClick(CartModel item);
        void onMinusClick(CartModel item);
        void onDeleteClick(CartModel item);
    }

    private final CartClickListener listener;

    public CartAdapter(CartClickListener listener) {
        super(new CartDiffUtil());
        this.listener = listener;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView cartImage, plus, minus, delete;
        TextView productName, productPrice, quantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cartImage = itemView.findViewById(R.id.cartImage);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            delete = itemView.findViewById(R.id.delete);

            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item, parent, false);

        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartModel item = getItem(position);

        holder.productName.setText(item.getProduct_name());
        holder.productPrice.setText("₹" + item.getProduct_price());
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        Glide.with(holder.cartImage.getContext())
                .load(item.getProduct_image())
                .placeholder(R.drawable.menu1)
                .into(holder.cartImage);

        holder.plus.setOnClickListener(v -> listener.onPlusClick(item));
        holder.minus.setOnClickListener(v -> listener.onMinusClick(item));
        holder.delete.setOnClickListener(v -> listener.onDeleteClick(item));
    }
}
