package com.example.miniproject.adapter;   // ⚠️ change to your real package

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject.databinding.PopularItemBinding;

import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.PopularViewHolder> {

    private final List<String> items;
    private final List<String> price;
    private final List<Integer> image;

    public PopularAdapter(List<String> items, List<String> price, List<Integer> image) {
        this.items = items;
        this.price = price;
        this.image = image;
    }

    @NonNull
    @Override
    public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        PopularItemBinding binding = PopularItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new PopularViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularViewHolder holder, int position) {

        String item = items.get(position);
        String itemPrice = price.get(position);
        int itemImage = image.get(position);

        holder.bind(item, itemPrice, itemImage);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    
    public static class PopularViewHolder extends RecyclerView.ViewHolder {

        private final PopularItemBinding binding;

        public PopularViewHolder(@NonNull PopularItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String item, String price, int image) {

            binding.foodNamePopuler.setText(item);
            binding.PricePopuar.setText(price);
            binding.imageView5.setImageResource(image);
        }
    }
}
