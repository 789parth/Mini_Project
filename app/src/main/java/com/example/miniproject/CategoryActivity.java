package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject.Category.CategoryAdapter;
import com.example.miniproject.Category.CategoryModel;
import com.example.miniproject.TrendingProduct.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private ImageView backBtn;

    private CategoryAdapter categoryAdapter;

    private Query categoryQuery, productQuery;
    private ValueEventListener categoryListener, productListener;

    private final ArrayList<CategoryModel> allCategories = new ArrayList<>();
    private final Set<String> categoryIdsWithProducts = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> finish());

        categoryAdapter = new CategoryAdapter(category -> {

            if (category.isHasProducts()) {

                Intent intent = new Intent(
                        CategoryActivity.this,
                        ProductActivity.class
                );

                intent.putExtra(
                        "category_id",
                        category.getCategory_id()
                );

                intent.putExtra(
                        "category_title",
                        category.getCategory_title()
                );

                startActivity(intent);
            }
        });

        categoryRecyclerView.setLayoutManager(
                new GridLayoutManager(this, 4)
        );

        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setItemAnimator(null);

        loadProducts();
        loadCategories();
    }

    private void loadProducts() {

        productQuery = FirebaseDatabase.getInstance()
                .getReference("products");

        productListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                categoryIdsWithProducts.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    ProductModel product =
                            ds.getValue(ProductModel.class);

                    if (product != null &&
                            product.getCategory_id() != null) {

                        categoryIdsWithProducts.add(
                                product.getCategory_id().trim()
                        );
                    }
                }

                showAllCategories();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        productQuery.addValueEventListener(productListener);
    }

    private void loadCategories() {

        categoryQuery = FirebaseDatabase.getInstance()
                .getReference("category");

        categoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                allCategories.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    CategoryModel category =
                            ds.getValue(CategoryModel.class);

                    if (category != null) {

                        if (category.getCategory_id() == null ||
                                category.getCategory_id().isEmpty()) {

                            category.setCategory_id(ds.getKey());
                        }

                        allCategories.add(category);
                    }
                }

                showAllCategories();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        categoryQuery.addValueEventListener(categoryListener);
    }

    private void showAllCategories() {

        ArrayList<CategoryModel> finalList = new ArrayList<>();

        for (CategoryModel category : allCategories) {

            CategoryModel newCategory = new CategoryModel();

            newCategory.setCategory_id(category.getCategory_id());
            newCategory.setCategory_title(category.getCategory_title());
            newCategory.setCategory_image(category.getCategory_image());
            newCategory.setStore_id(category.getStore_id());

            boolean hasProducts =
                    categoryIdsWithProducts.contains(
                            category.getCategory_id()
                    );

            newCategory.setHasProducts(hasProducts);

            finalList.add(newCategory);
        }

        categoryAdapter.updateList(finalList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (categoryQuery != null && categoryListener != null) {
            categoryQuery.removeEventListener(categoryListener);
        }

        if (productQuery != null && productListener != null) {
            productQuery.removeEventListener(productListener);
        }
    }
}