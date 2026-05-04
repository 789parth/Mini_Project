package com.example.miniproject.TrendingProduct;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.miniproject.DetailsActivity;
import com.example.miniproject.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<ProductModel> productList = new ArrayList<>();

    public void updateList(List<ProductModel> newList) {
        ProductDiffCallback diffCallback =
                new ProductDiffCallback(productList, newList);

        DiffUtil.DiffResult diffResult =
                DiffUtil.calculateDiff(diffCallback);

        productList.clear();
        productList.addAll(newList);

        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.productitems, parent, false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        ProductModel product = productList.get(position);

        holder.productName.setText(product.getProduct_name());
        holder.productPrice.setText("₹" + product.getProduct_price());
        holder.productUnit.setText(product.getProduct_unit());

        Glide.with(holder.itemView.getContext())
                .load(product.getProduct_image())
                .centerCrop()
                .into(holder.productImage);

        boolean isSoldOut = product.getProduct_quantity() <= 0;

        if (isSoldOut) {

            // UI
            holder.itemView.setAlpha(0.5f);

            // Disable clicks (VERY IMPORTANT)
            holder.itemView.setEnabled(false);
            holder.itemView.setClickable(false);
            holder.itemView.setOnClickListener(null);

        } else {

            // UI reset
            holder.itemView.setAlpha(1f);

            holder.itemView.setEnabled(true);
            holder.itemView.setClickable(true);


            // Click
            holder.itemView.setOnClickListener(v -> {

                Intent intent = new Intent(
                        holder.itemView.getContext(),
                        DetailsActivity.class
                );

                intent.putExtra("product_data", product);
                holder.itemView.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage;
        TextView productName, productPrice, productUnit;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productUnit = itemView.findViewById(R.id.productWeight);

        }
    }
}