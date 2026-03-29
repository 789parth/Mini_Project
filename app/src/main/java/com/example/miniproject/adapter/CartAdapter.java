package com.example.miniproject.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject.databinding.CartItemsBinding;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<String> cartItems;
    private final List<String> cartPrice;
    private final List<String> cartWeight;
    private final List<Integer> cartImage;
    private final List<Integer> cartQuantity;

    public CartAdapter(List<String> cartItems, List<String> cartPrice, List<String> cartWeight, List<Integer> cartImage, List<Integer> cartQuantity) {
        this.cartItems = cartItems;
        this.cartPrice = cartPrice;
        this.cartWeight = cartWeight;
        this.cartImage = cartImage;
        this.cartQuantity = cartQuantity;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CartItemsBinding binding = CartItemsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private final CartItemsBinding binding;

        public CartViewHolder(CartItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            binding.productName.setText(cartItems.get(position));
            binding.productPrice.setText(cartPrice.get(position));
            binding.productWeight.setText(cartWeight.get(position));
            binding.cartImage.setImageResource(cartImage.get(position));
            binding.quantity.setText(String.valueOf(cartQuantity.get(position)));

            binding.plus.setOnClickListener(v -> {
                int count = cartQuantity.get(position);
                if (count < 10) {
                    count++;
                    cartQuantity.set(position, count);
                    binding.quantity.setText(String.valueOf(count));
                }
            });

            binding.minus.setOnClickListener(v -> {
                int count = cartQuantity.get(position);
                if (count > 1) {
                    count--;
                    cartQuantity.set(position, count);
                    binding.quantity.setText(String.valueOf(count));
                }
            });

            binding.delete.setOnClickListener(v -> {
                cartItems.remove(position);
                cartPrice.remove(position);
                cartWeight.remove(position);
                cartImage.remove(position);
                cartQuantity.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());
            });
        }
    }
}
