package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.miniproject.ManagerClass.SessionManager;
import com.example.miniproject.adapter.productadapter;
import com.example.miniproject.domain.products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailsActivity extends AppCompatActivity {
    SessionManager sessionManager;

    ConstraintLayout elegantNumber;
    ImageView productImage;
    Button addBtn;
    TextView productTitle,productUnit,productPrice,productDescription,totalPrice;
    RecyclerView similarRecycler;
    productadapter similarAdapter;

    //For Quantity:
    ImageView addQuantity,removeQuantity;
    TextView quantity;
    int totalQuantity = 1;
    int totalPriceAmt = 0;



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
        String productId = getIntent().getStringExtra("product_id");


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);

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

        //For Quantity:
        quantity = findViewById(R.id.quantity);
        addQuantity = findViewById(R.id.addQuantity);
        removeQuantity = findViewById(R.id.removeQuantity);
        totalPrice = findViewById(R.id.totalPrice);

        addQuantity.setOnClickListener(v -> {
            if(totalQuantity < 10){
                totalQuantity++;
                quantity.setText(String.valueOf(totalQuantity));
                totalPriceAmt = totalQuantity * price;
                totalPrice.setText("Total: ₹"+ String.valueOf(totalPriceAmt));
            }
        });

        removeQuantity.setOnClickListener(v -> {
            if(totalQuantity > 1){
                totalQuantity--;
                quantity.setText(String.valueOf(totalQuantity));
                totalPriceAmt = totalQuantity * price;
                totalPrice.setText("Total: ₹"+ String.valueOf(totalPriceAmt));
            }
        });

        //For Add to cart.
        addBtn = findViewById(R.id.addBtn);
        elegantNumber = findViewById(R.id.elegantNumbers);
        addBtn.setOnClickListener(view -> {
            if (addBtn.getText().toString().equals("Go to Cart")) {

                //Open Cart
                startActivity(new Intent(DetailsActivity.this, CartActivity.class));

            } else {

                //Add to Cart
                addedToCart();

                // UI change
                addBtn.setText("Go to Cart");
                addBtn.setAlpha(1f);
                addBtn.setEnabled(true);

                elegantNumber.setVisibility(View.GONE);
                elegantNumber.setEnabled(false);
            }
        });

        checkIfAlreadyInCart();

    }

    private void addedToCart() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Please Login First", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();

        String productId = getIntent().getStringExtra("product_id");
        String name = getIntent().getStringExtra("product_name");
        int price = getIntent().getIntExtra("product_price", 0);
        String image = getIntent().getStringExtra("product_image");

        String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference("carts")
                .child(uid)
                .child(productId);

        // 🔥 Direct add (no update logic)
        HashMap<String, Object> map = new HashMap<>();
        map.put("product_name", name);
        map.put("product_price", price);
        map.put("product_image", image);
        map.put("quantity", totalQuantity);
        map.put("date", saveCurrentDate);
        map.put("time", saveCurrentTime);

        cartRef.setValue(map).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkIfAlreadyInCart() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) return;

        String uid = user.getUid();
        String productId = getIntent().getStringExtra("product_id");

        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference("carts")
                .child(uid)
                .child(productId);

        cartRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {



                // 🔥 Already in cart → Go to Cart
                addBtn.setText("Go to Cart");
                addBtn.setEnabled(true);
                addBtn.setAlpha(1f);

                elegantNumber.setVisibility(View.GONE);
                elegantNumber.setEnabled(false);


            }
        });
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

    @Override
    public void onResume() {
        super.onResume();

        if (similarRecycler != null && similarAdapter != null) {
            similarRecycler.setAdapter(null);
            similarRecycler.setAdapter(similarAdapter);
        }

    }
}