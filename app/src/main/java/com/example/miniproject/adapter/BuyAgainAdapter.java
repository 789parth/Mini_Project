package com.example.miniproject.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject.databinding.BuyAgainItemBinding;

import java.util.ArrayList;

public class BuyAgainAdapter extends RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder> {

    private final ArrayList<String> buyAgainFoodName;
    private final ArrayList<String> buyAgainFoodPrice;
    private final ArrayList<Integer> buyAgainFoodImage;

    public BuyAgainAdapter(ArrayList<String> buyAgainFoodName,
                           ArrayList<String> buyAgainFoodPrice,
                           ArrayList<Integer> buyAgainFoodImage) {

        this.buyAgainFoodName = buyAgainFoodName;
        this.buyAgainFoodPrice = buyAgainFoodPrice;
        this.buyAgainFoodImage = buyAgainFoodImage;
    }

    @NonNull
    @Override
    public BuyAgainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        BuyAgainItemBinding binding = BuyAgainItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new BuyAgainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyAgainViewHolder holder, int position) {

        holder.bind(
                buyAgainFoodName.get(position),
                buyAgainFoodPrice.get(position),
                buyAgainFoodImage.get(position)
        );
    }

    @Override
    public int getItemCount() {
        return buyAgainFoodName.size();
    }

    public static class BuyAgainViewHolder extends RecyclerView.ViewHolder {

        private final BuyAgainItemBinding binding;

        public BuyAgainViewHolder(@NonNull BuyAgainItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String name, String price, int image) {

            // ⚠️ Make sure these IDs exist in buy_again_item.xml
            binding.foodNameBuyAgain.setText(name);
            binding.priceBuyAgain.setText(price);
            binding.imageBuyAgain.setImageResource(image);
        }
    }
}
