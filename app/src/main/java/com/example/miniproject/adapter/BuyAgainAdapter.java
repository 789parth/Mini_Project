package com.example.miniproject.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.miniproject.R;
import com.example.miniproject.databinding.BuyAgainItemBinding;

import java.util.ArrayList;

public class BuyAgainAdapter extends RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder> {

    // Callback so Fragment/Activity can handle add-to-cart
    public interface OnAddToCartListener {
        void onAddToCart(int position, String productId, String name, String price, String imageUrl);
    }

    private final ArrayList<String> buyAgainFoodName;
    private final ArrayList<String> buyAgainFoodPrice;
    private final ArrayList<String> buyAgainFoodImageUrl;
    private final ArrayList<String> buyAgainProductId;   // product_id from order
    private OnAddToCartListener addToCartListener;

    // Constructor WITH productIds (used from Profile/History — Firebase orders)
    public BuyAgainAdapter(ArrayList<String> buyAgainFoodName,
                           ArrayList<String> buyAgainFoodPrice,
                           ArrayList<String> buyAgainFoodImageUrl,
                           ArrayList<String> buyAgainProductId) {
        this.buyAgainFoodName    = buyAgainFoodName;
        this.buyAgainFoodPrice   = buyAgainFoodPrice;
        this.buyAgainFoodImageUrl = buyAgainFoodImageUrl;
        this.buyAgainProductId   = buyAgainProductId;
    }

    // Overloaded constructor WITHOUT productIds (backward-compat if called with 3 args)
    public BuyAgainAdapter(ArrayList<String> buyAgainFoodName,
                           ArrayList<String> buyAgainFoodPrice,
                           ArrayList<String> buyAgainFoodImageUrl) {
        this(buyAgainFoodName, buyAgainFoodPrice, buyAgainFoodImageUrl, new ArrayList<>());
    }

    public void setOnAddToCartListener(OnAddToCartListener listener) {
        this.addToCartListener = listener;
    }

    @NonNull
    @Override
    public BuyAgainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BuyAgainItemBinding binding = BuyAgainItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BuyAgainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BuyAgainViewHolder holder, int position) {
        String name     = buyAgainFoodName.get(position);
        String price    = buyAgainFoodPrice.get(position);
        String imageUrl = buyAgainFoodImageUrl.get(position);
        String pid      = (buyAgainProductId.size() > position) ? buyAgainProductId.get(position) : "";

        holder.bind(name, price, imageUrl);

        holder.binding.btnBuyAgain.setOnClickListener(v -> {
            if (addToCartListener != null) {
                addToCartListener.onAddToCart(position, pid, name, price, imageUrl);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buyAgainFoodName.size();
    }

    public static class BuyAgainViewHolder extends RecyclerView.ViewHolder {
        final BuyAgainItemBinding binding;

        public BuyAgainViewHolder(@NonNull BuyAgainItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String name, String price, String imageUrl) {
            binding.foodNameBuyAgain.setText(name);
            binding.priceBuyAgain.setText(price);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.menu1)
                        .error(R.drawable.menu1)
                        .centerCrop()
                        .into(binding.imageBuyAgain);
            } else {
                binding.imageBuyAgain.setImageResource(R.drawable.menu1);
            }
        }
    }
}
