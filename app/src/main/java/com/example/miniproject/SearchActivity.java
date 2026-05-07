package com.example.miniproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject.Category.CategoryAdapter;
import com.example.miniproject.Category.CategoryModel;
import com.example.miniproject.SearchSuggestion.SearchSuggestionAdapter;
import com.example.miniproject.SearchSuggestion.SearchSuggestionModel;
import com.example.miniproject.TrendingProduct.ProductAdapter;
import com.example.miniproject.TrendingProduct.ProductModel;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;

    private RecyclerView suggestionRecyclerView;
    private RecyclerView categoryRecyclerView;
    private RecyclerView productRecyclerView;

    private View defaultContentLayout;

    private SearchSuggestionAdapter suggestionAdapter;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;

    private final List<ProductModel> allProducts = new ArrayList<>();
    private final List<CategoryModel> allCategories = new ArrayList<>();

    private final Set<String> categoryIdsWithProducts = new HashSet<>();

    private Query productQuery, categoryQuery;
    private ValueEventListener productListener, categoryListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.searchText);

        suggestionRecyclerView = findViewById(R.id.suggestionRecyclerView);
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        productRecyclerView = findViewById(R.id.productRecyclerView);
        defaultContentLayout = findViewById(R.id.defaultContentLayout);

        setupAdapters();
        setupSearch();

        loadProducts();
        loadCategories();
    }

    private void setupAdapters() {

        suggestionAdapter = new SearchSuggestionAdapter(suggestion -> {

            if (SearchSuggestionModel.TYPE_PRODUCT.equals(suggestion.getType())) {

                ProductModel product = (ProductModel) suggestion.getData();

                Intent intent = new Intent(SearchActivity.this, DetailsActivity.class);
                intent.putExtra("product_data", product);
                startActivity(intent);

            } else if (SearchSuggestionModel.TYPE_CATEGORY.equals(suggestion.getType())) {

                Intent intent = new Intent(SearchActivity.this, ProductActivity.class);
                intent.putExtra("category_id", suggestion.getId());
                intent.putExtra("category_title", suggestion.getTitle());
                startActivity(intent);
            }
        });

        suggestionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        suggestionRecyclerView.setAdapter(suggestionAdapter);

        categoryAdapter = new CategoryAdapter(category -> {
            if (category.isHasProducts()) {
                Intent intent = new Intent(SearchActivity.this, ProductActivity.class);
                intent.putExtra("category_id", category.getCategory_id());
                intent.putExtra("category_title", category.getCategory_title());
                startActivity(intent);
            }
        });

        categoryRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        categoryRecyclerView.setAdapter(categoryAdapter);
        categoryRecyclerView.setItemAnimator(null);
        categoryRecyclerView.setNestedScrollingEnabled(false);

        productAdapter = new ProductAdapter();
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productRecyclerView.setAdapter(productAdapter);
        productRecyclerView.setItemAnimator(null);
        productRecyclerView.setNestedScrollingEnabled(false);
    }

    private void setupSearch() {

        searchEditText.requestFocus();

        searchEditText.postDelayed(() -> {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 200);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();

                if (text.isEmpty()) {
                    showDefaultContent();
                } else {
                    showSuggestionContent();
                    showSuggestions(text);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchEditText.getText().toString().trim();

            if (query.isEmpty()) return true;

            Intent intent = new Intent(SearchActivity.this, ProductActivity.class);
            intent.putExtra("type", "search");
            intent.putExtra("search_query", query);
            intent.putExtra("title", "Search: " + query);
            startActivity(intent);

            return true;
        });
    }

    private void showDefaultContent() {
        defaultContentLayout.setVisibility(View.VISIBLE);
        suggestionRecyclerView.setVisibility(View.GONE);
    }

    private void showSuggestionContent() {
        defaultContentLayout.setVisibility(View.GONE);
        suggestionRecyclerView.setVisibility(View.VISIBLE);
    }

    private void loadProducts() {
        productQuery = FirebaseDatabase.getInstance()
                .getReference("products")
                .limitToFirst(50);

        productListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                allProducts.clear();
                categoryIdsWithProducts.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ProductModel product = ds.getValue(ProductModel.class);

                    if (product != null) {
                        product.setProduct_id(ds.getKey());
                        allProducts.add(product);

                        if (product.getCategory_id() != null) {
                            categoryIdsWithProducts.add(product.getCategory_id().trim());
                        }
                    }
                }

                productAdapter.updateList(new ArrayList<>(allProducts));
                filterAndShowCategories();

                if (!searchEditText.getText().toString().trim().isEmpty()) {
                    showSuggestions(searchEditText.getText().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        productQuery.addValueEventListener(productListener);
    }

    private void loadCategories() {
        categoryQuery = FirebaseDatabase.getInstance()
                .getReference("category")
                .limitToFirst(30);

        categoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                allCategories.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    CategoryModel category = ds.getValue(CategoryModel.class);

                    if (category != null) {
                        if (category.getCategory_id() == null ||
                                category.getCategory_id().trim().isEmpty()) {
                            category.setCategory_id(ds.getKey());
                        } else {
                            category.setCategory_id(category.getCategory_id().trim());
                        }

                        allCategories.add(category);
                    }
                }

                filterAndShowCategories();

                if (!searchEditText.getText().toString().trim().isEmpty()) {
                    showSuggestions(searchEditText.getText().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        categoryQuery.addValueEventListener(categoryListener);
    }

    private void filterAndShowCategories() {
        List<CategoryModel> filteredCategories = new ArrayList<>();

        for (CategoryModel category : allCategories) {
            if (category.getCategory_id() != null &&
                    categoryIdsWithProducts.contains(category.getCategory_id().trim())) {

                CategoryModel newCategory = new CategoryModel();
                newCategory.setCategory_id(category.getCategory_id().trim());
                newCategory.setCategory_title(category.getCategory_title());
                newCategory.setCategory_image(category.getCategory_image());
                newCategory.setStore_id(category.getStore_id());
                newCategory.setHasProducts(true);

                filteredCategories.add(newCategory);
            }
        }

        categoryAdapter.updateList(filteredCategories);
    }

    private void showSuggestions(String text) {
        String query = text.trim().toLowerCase();

        List<SearchSuggestionModel> suggestions = new ArrayList<>();

        for (CategoryModel category : allCategories) {
            if (category.getCategory_id() != null &&
                    categoryIdsWithProducts.contains(category.getCategory_id().trim()) &&
                    category.getCategory_title() != null &&
                    category.getCategory_title().toLowerCase().contains(query)) {

                CategoryModel newCategory = new CategoryModel();
                newCategory.setCategory_id(category.getCategory_id().trim());
                newCategory.setCategory_title(category.getCategory_title());
                newCategory.setCategory_image(category.getCategory_image());
                newCategory.setStore_id(category.getStore_id());
                newCategory.setHasProducts(true);

                suggestions.add(new SearchSuggestionModel(
                        newCategory.getCategory_id(),
                        newCategory.getCategory_title(),
                        "Category",
                        newCategory.getCategory_image(),
                        SearchSuggestionModel.TYPE_CATEGORY,
                        newCategory
                ));
            }

            if (suggestions.size() >= 3) break;
        }

        for (ProductModel product : allProducts) {
            if (product.getProduct_name() != null &&
                    product.getProduct_name().toLowerCase().contains(query)) {

                suggestions.add(new SearchSuggestionModel(
                        product.getProduct_id(),
                        product.getProduct_name(),
                        "Product • ₹" + product.getProduct_price(),
                        product.getProduct_image(),
                        SearchSuggestionModel.TYPE_PRODUCT,
                        product
                ));
            }

            if (suggestions.size() >= 10) break;
        }

        suggestionAdapter.updateList(suggestions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (productQuery != null && productListener != null) {
            productQuery.removeEventListener(productListener);
        }

        if (categoryQuery != null && categoryListener != null) {
            categoryQuery.removeEventListener(categoryListener);
        }
    }
}