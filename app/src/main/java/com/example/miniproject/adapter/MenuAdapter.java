package com.example.miniproject.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject.databinding.MenuItemBinding;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private final List<String> menuItems;
    private final List<String> menuPrices;
    private final List<Integer> menuImages;

    public MenuAdapter(List<String> menuItems, List<String> menuPrices, List<Integer> menuImages) {
        this.menuItems = menuItems;
        this.menuPrices = menuPrices;
        this.menuImages = menuImages;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MenuItemBinding binding = MenuItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MenuViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        private final MenuItemBinding binding;

        public MenuViewHolder(MenuItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int position) {
            binding.menuItemName.setText(menuItems.get(position));
            binding.menuItemPrice.setText(menuPrices.get(position));
            binding.menuImage.setImageResource(menuImages.get(position));

            // Set onClickListener for the Add to Cart button or any other action
            binding.btnMenu.setOnClickListener(v -> {
                // Handle Add to Cart action here
            });
        }
    }
}
