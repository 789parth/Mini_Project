package com.example.miniproject;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.miniproject.adapter.productadapter;
import com.example.miniproject.domain.products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class DetailsActivity extends AppCompatActivity {
    ImageView productImage;
    TextView productTitle,productUnit,productPrice,productDescription;
    RecyclerView similarRecycler;
    productadapter similarAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);


        String name = getIntent().getStringExtra("product_name");
        int price = getIntent().getIntExtra("product_price", 0);
        String image = getIntent().getStringExtra("product_image");
        String unit = getIntent().getStringExtra("product_unit");
        String description = getIntent().getStringExtra("product_description");
        String categoryId = getIntent().getStringExtra("category_id");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        productImage = findViewById(R.id.productImage);
        productTitle = findViewById(R.id.productTitle);
        productPrice = findViewById(R.id.productPrice);
        productUnit = findViewById(R.id.productUnit);
        productDescription = findViewById(R.id.productDescription);

        if(image != null){
            Glide.with(this).load(image).into(productImage);
        }
        productTitle.setText(name);
        productPrice.setText("₹"+ String.valueOf(price));
        productUnit.setText(unit);
        productDescription.setText(description);

        similarViewLoad(categoryId);

    }

    private void similarViewLoad(String categoryId) {
        similarRecycler = findViewById(R.id.similarRecycler);
        similarRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        FirebaseRecyclerOptions<products> options =
                new FirebaseRecyclerOptions.Builder<products>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("products").orderByChild("category_id")
                                .equalTo(categoryId), products.class)
                        .build();

        similarAdapter = new productadapter(options, null);
        similarRecycler.setAdapter(similarAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (similarAdapter != null) similarAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (similarAdapter != null) similarAdapter.stopListening();

    }
}