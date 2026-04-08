package com.example.miniproject.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.miniproject.DetailsActivity;
import com.example.miniproject.R;
import com.example.miniproject.domain.products;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;

public class productadapter extends FirebaseRecyclerAdapter<products, productadapter.myviewholder> {

    ProgressBar progressBar;
    public productadapter(@NonNull FirebaseRecyclerOptions<products> options, ProgressBar progressBar) {
        super(options);
        this.progressBar = progressBar;
        setStateRestorationPolicy(StateRestorationPolicy.PREVENT_WHEN_EMPTY);
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull products model) {

        if (model == null) return;

        // 🔹 Safe data
        String name = model.getProduct_name();
        String unit = model.getProduct_unit();
        int price = model.getProduct_price();
        String image = model.getProduct_image();

        // 🔹 Set data safely
        holder.name.setText(name != null ? name : "No Name");
        holder.unit.setText(unit != null ? unit : "");
        holder.price.setText("₹ " + price);

        // 🔹 Safe image load
        if (image != null && !image.isEmpty()) {
            Glide.with(holder.img.getContext())
                    .load(image)
                    .into(holder.img);
        } else {
            holder.img.setImageResource(R.drawable.banner1);
        }

        // 🔹 Click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailsActivity.class);

            intent.putExtra("product_name", name);
            intent.putExtra("product_price", price);
            intent.putExtra("product_image", image);
            intent.putExtra("product_description", model.getProduct_description());
            intent.putExtra("product_unit", unit);
            intent.putExtra("category_id", model.getCategory_id());

            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    // 🔥 Handle error case
    @Override
    public void onError(@NonNull DatabaseError error) {
        super.onError(error);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }


    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.productitems, parent, false);
        return new myviewholder(view);
    }

    public class myviewholder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, unit, price;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.productImage);
            name = itemView.findViewById(R.id.productName);
            unit = itemView.findViewById(R.id.productWeight);
            price = itemView.findViewById(R.id.productPrice);
        }
    }
}