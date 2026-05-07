package com.example.miniproject;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject.TrendingProduct.ProductAdapter;
import com.example.miniproject.TrendingProduct.ProductModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    private RecyclerView productRecyclerView;
    private TextView title;
    private ImageView backBtn;

    private ProductAdapter productAdapter;

    private final List<ProductModel> productList = new ArrayList<>();

    private DatabaseReference productRef;
    private Query query;
    private ChildEventListener childEventListener;

    private String type;
    private String searchQuery;
    private String categoryId;
    private String categoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        productRecyclerView = findViewById(R.id.productRecyclerView);
        title = findViewById(R.id.title);
        backBtn = findViewById(R.id.backBtn);

        type = getIntent().getStringExtra("type");
        searchQuery = getIntent().getStringExtra("search_query");

        productAdapter = new ProductAdapter();

        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productRecyclerView.setAdapter(productAdapter);
        productRecyclerView.setItemAnimator(null);

        backBtn.setOnClickListener(v -> finish());

        startListeningProducts();
    }

    private void startListeningProducts() {

        productRef = FirebaseDatabase.getInstance().getReference("products");

        if ("search".equals(type)) {

            String titleText = getIntent().getStringExtra("title");
            title.setText(titleText != null ? titleText : "Search Products");

            // Search all products
            query = productRef;

        } else if ("trending".equals(type)) {

            String titleText = getIntent().getStringExtra("title");
            title.setText(titleText != null ? titleText : "Trending Products");

            // Only trending products
            query = productRef
                    .orderByChild("trending_item")
                    .equalTo(true);

        } else {

            categoryId = getIntent().getStringExtra("category_id");
            categoryTitle = getIntent().getStringExtra("category_title");

            title.setText(categoryTitle != null ? categoryTitle : "Products");

            if (categoryId == null) {
                Toast.makeText(this, "Category not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Category products
            query = productRef
                    .orderByChild("category_id")
                    .equalTo(categoryId);
        }

        productList.clear();
        productAdapter.updateList(new ArrayList<>());

        childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {

                ProductModel product = snapshot.getValue(ProductModel.class);
                if (product == null) return;

                product.setProduct_id(snapshot.getKey());

                if (!isProductAllowed(product)) return;

                if (findIndex(product.getProduct_id()) == -1) {
                    productList.add(product);
                    productAdapter.updateList(new ArrayList<>(productList));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {

                ProductModel updated = snapshot.getValue(ProductModel.class);
                if (updated == null) return;

                updated.setProduct_id(snapshot.getKey());

                int index = findIndex(updated.getProduct_id());
                boolean allowed = isProductAllowed(updated);

                if (index != -1 && allowed) {
                    productList.set(index, updated);
                } else if (index != -1) {
                    productList.remove(index);
                } else if (allowed) {
                    productList.add(updated);
                }

                productAdapter.updateList(new ArrayList<>(productList));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                String productId = snapshot.getKey();
                int index = findIndex(productId);

                if (index != -1) {
                    productList.remove(index);
                    productAdapter.updateList(new ArrayList<>(productList));
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductActivity.this,
                        error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        };

        query.addChildEventListener(childEventListener);
    }

    private boolean isProductAllowed(ProductModel product) {

        if ("search".equals(type)) {
            if (searchQuery == null || searchQuery.trim().isEmpty()) {
                return false;
            }

            String query = searchQuery.toLowerCase().trim();

            String name = product.getProduct_name();
            String categoryId = product.getCategory_id();
            String categoryTitle = product.getCategory_name(); // only if exists in ProductModel

            boolean matchName = name != null &&
                    name.toLowerCase().contains(query);

            boolean matchCategoryId = categoryId != null &&
                    categoryId.toLowerCase().contains(query);

            boolean matchCategoryTitle = categoryTitle != null &&
                    categoryTitle.toLowerCase().contains(query);

            return matchName || matchCategoryId || matchCategoryTitle;
        }

        return true;
    }

    private int findIndex(String productId) {
        if (productId == null) return -1;

        for (int i = 0; i < productList.size(); i++) {
            if (productId.equals(productList.get(i).getProduct_id())) {
                return i;
            }
        }

        return -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (query != null && childEventListener != null) {
            query.removeEventListener(childEventListener);
        }
    }
}