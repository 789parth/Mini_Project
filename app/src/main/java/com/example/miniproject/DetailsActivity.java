package com.example.miniproject;

import static android.view.View.GONE;

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
import com.example.miniproject.TrendingProduct.ProductAdapter;
import com.example.miniproject.TrendingProduct.ProductModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {

    SessionManager sessionManager;

    ConstraintLayout elegantNumber;
    ImageView productImage;
    Button addBtn;
    TextView productTitle, productUnit, productPrice, productDescription, totalPrice, similarTitle;
    RecyclerView similarRecycler;
    ProductAdapter similarAdapter;

    ImageView addQuantity, removeQuantity;
    TextView quantity;

    int totalQuantity = 1;
    double totalPriceAmt = 0;

    ProductModel product;

    String name, image, unit, description, categoryId, productId;
    double price = 0;

    private DatabaseReference liveStockRef;
    private ValueEventListener liveStockListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);

        product = (ProductModel) getIntent().getSerializableExtra("product_data");

        if (product == null) {
            Toast.makeText(this, "Product data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        productId = product.getProduct_id();
        name = product.getProduct_name();
        price = product.getProduct_price();
        image = product.getProduct_image();
        unit = product.getProduct_unit();
        description = product.getProduct_description();
        categoryId = product.getCategory_id();

        totalPriceAmt = price;

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
        similarTitle = findViewById(R.id.similarProducts);

        quantity = findViewById(R.id.quantity);
        addQuantity = findViewById(R.id.addQuantity);
        removeQuantity = findViewById(R.id.removeQuantity);
        totalPrice = findViewById(R.id.totalPrice);

        addBtn = findViewById(R.id.addBtn);
        elegantNumber = findViewById(R.id.elegantNumbers);

        Glide.with(this)
                .load(image)
                .centerCrop()
                .into(productImage);

        productTitle.setText(name);
        productPrice.setText("₹" + price);
        productUnit.setText(unit);
        productDescription.setText(description);
        quantity.setText(String.valueOf(totalQuantity));
        totalPrice.setText("Total: ₹" + totalPriceAmt);

        similarViewLoad(categoryId);
        setupQuantityButtons();
        setupAddButton();
        checkLiveProductStock();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // When coming back from CartActivity, show Add to Cart again
        if (product != null && product.getProduct_quantity() > 0) {
            addBtn.setText("Add to Cart");
            addBtn.setEnabled(true);
            addBtn.setAlpha(1f);

            elegantNumber.setVisibility(View.VISIBLE);
            elegantNumber.setEnabled(true);
        }
    }

    private void setupQuantityButtons() {

        addQuantity.setOnClickListener(v -> {

            int stock = product.getProduct_quantity();

            if (stock <= 0) {
                Toast.makeText(this, "Product is sold out", Toast.LENGTH_SHORT).show();
                return;
            }

            if (totalQuantity >= 10) {
                Toast.makeText(this, "Maximum 10 items allowed", Toast.LENGTH_SHORT).show();
                return;
            }

            if (totalQuantity >= stock) {
                Toast.makeText(this, "Only " + stock + " items available", Toast.LENGTH_SHORT).show();
                return;
            }

            totalQuantity++;
            quantity.setText(String.valueOf(totalQuantity));

            totalPriceAmt = totalQuantity * price;
            totalPrice.setText("Total: ₹" + totalPriceAmt);
        });

        removeQuantity.setOnClickListener(v -> {
            if (totalQuantity > 1) {
                totalQuantity--;
                quantity.setText(String.valueOf(totalQuantity));

                totalPriceAmt = totalQuantity * price;
                totalPrice.setText("Total: ₹" + totalPriceAmt);
            }
        });
    }

    private void setupAddButton() {

        addBtn.setOnClickListener(view -> {

            if (addBtn.getText().toString().equals("Go to Cart")) {
                startActivity(new Intent(DetailsActivity.this, CartActivity.class));
                return;
            }

            addedToCart();

            addBtn.setText("Go to Cart");
            addBtn.setAlpha(1f);
            addBtn.setEnabled(true);

            elegantNumber.setVisibility(GONE);
            elegantNumber.setEnabled(false);
        });
    }

    private void addedToCart() {

        if (product.getProduct_quantity() <= 0) {
            Toast.makeText(this, "Product is sold out", Toast.LENGTH_SHORT).show();
            return;
        }

        if (totalQuantity > 10) {
            Toast.makeText(this, "Maximum 10 items allowed", Toast.LENGTH_SHORT).show();
            return;
        }

        if (totalQuantity > product.getProduct_quantity()) {
            Toast.makeText(this,
                    "Only " + product.getProduct_quantity() + " items available",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Please Login First", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();

        DatabaseReference cartRef = FirebaseDatabase.getInstance()
                .getReference("carts")
                .child(uid)
                .child(productId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("product_name", name);
        map.put("product_price", price);
        map.put("product_image", image);
        map.put("quantity", totalQuantity);

        cartRef.setValue(map).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Added to Cart", Toast.LENGTH_SHORT).show();
        });
    }

    private void similarViewLoad(String categoryId) {

        similarRecycler = findViewById(R.id.similarRecycler);
        similarAdapter = new ProductAdapter();

        similarRecycler.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        similarRecycler.setAdapter(similarAdapter);
        similarRecycler.setHasFixedSize(true);
        similarRecycler.setItemAnimator(null);

        FirebaseDatabase.getInstance()
                .getReference("products")
                .orderByChild("category_id")
                .equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        List<ProductModel> similarList = new ArrayList<>();

                        for (DataSnapshot child : snapshot.getChildren()) {
                            ProductModel similarProduct = child.getValue(ProductModel.class);

                            if (similarProduct != null) {
                                similarProduct.setProduct_id(child.getKey());

                                if (!similarProduct.getProduct_id().equals(productId)) {
                                    similarList.add(similarProduct);
                                }
                            }
                        }

                        if (similarList.isEmpty()) {
                            similarRecycler.setVisibility(View.GONE);
                            similarTitle.setVisibility(View.GONE);
                        } else {
                            similarRecycler.setVisibility(View.VISIBLE);
                            similarTitle.setVisibility(View.VISIBLE);
                            similarAdapter.updateList(similarList);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
    }

    private void checkLiveProductStock() {

        liveStockRef = FirebaseDatabase.getInstance()
                .getReference("products")
                .child(productId);

        liveStockListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                ProductModel latestProduct = snapshot.getValue(ProductModel.class);

                if (latestProduct == null) return;

                latestProduct.setProduct_id(productId);
                product = latestProduct;

                int latestQuantity = latestProduct.getProduct_quantity();

                if (latestQuantity <= 0) {

                    addBtn.setText("Sold Out");
                    addBtn.setEnabled(false);
                    addBtn.setAlpha(0.5f);

                    elegantNumber.setVisibility(GONE);
                    elegantNumber.setEnabled(false);

                } else {

                    if (!addBtn.getText().toString().equals("Go to Cart")) {
                        addBtn.setText("Add to Cart");
                        addBtn.setEnabled(true);
                        addBtn.setAlpha(1f);

                        elegantNumber.setVisibility(View.VISIBLE);
                        elegantNumber.setEnabled(true);
                    }

                    if (totalQuantity > latestQuantity) {
                        totalQuantity = latestQuantity;
                        quantity.setText(String.valueOf(totalQuantity));

                        totalPriceAmt = totalQuantity * price;
                        totalPrice.setText("Total: ₹" + totalPriceAmt);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };

        liveStockRef.addValueEventListener(liveStockListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (liveStockRef != null && liveStockListener != null) {
            liveStockRef.removeEventListener(liveStockListener);
        }
    }
}