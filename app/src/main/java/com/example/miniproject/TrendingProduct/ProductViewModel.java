package com.example.miniproject.TrendingProduct;

import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductViewModel extends ViewModel {

    private static final int PAGE_SIZE = 10;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final MutableLiveData<List<ProductModel>> trendingProducts =
            new MutableLiveData<>(new ArrayList<>());

    private final List<ProductModel> productList = new ArrayList<>();

    private DatabaseReference productsRef;
    private ChildEventListener childEventListener;

    private boolean listenerAttached = false;
    private boolean isLoading = false;
    private boolean hasMoreData = true;

    private String lastLoadedKey = null;

    public LiveData<List<ProductModel>> getTrendingProducts() {
        if (!listenerAttached) {
            loadFirstPage();
        }
        return trendingProducts;
    }

    private void loadFirstPage() {
        if (listenerAttached || isLoading) return;

        listenerAttached = true;
        isLoading = true;

        productsRef = FirebaseDatabase.getInstance()
                .getReference("products");

        handler.postDelayed(() -> {

            Query firstPageQuery = productsRef
                    .orderByChild("trending_item")
                    .equalTo(true)
                    .limitToFirst(PAGE_SIZE);

            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                    ProductModel product = snapshot.getValue(ProductModel.class);

                    if (product != null && findIndexById(product.getProduct_id()) == -1) {
                        productList.add(product);
                        lastLoadedKey = snapshot.getKey();
                        publishList();
                    }

                    isLoading = false;
                }

                @Override
                public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                    ProductModel updatedProduct = snapshot.getValue(ProductModel.class);

                    if (updatedProduct == null) return;

                    int index = findIndexById(updatedProduct.getProduct_id());

                    if (index >= 0) {
                        productList.set(index, updatedProduct);
                        publishList();
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot snapshot) {
                    ProductModel removedProduct = snapshot.getValue(ProductModel.class);

                    if (removedProduct == null) return;

                    int index = findIndexById(removedProduct.getProduct_id());

                    if (index >= 0) {
                        productList.remove(index);
                        publishList();
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    isLoading = false;
                }
            };

            firstPageQuery.addChildEventListener(childEventListener);

        }, 800); // first page delay
    }

    public void loadNextPage() {
        if (isLoading || !hasMoreData || lastLoadedKey == null) return;

        isLoading = true;

        handler.postDelayed(() -> {

            Query nextPageQuery = productsRef
                    .orderByChild("trending_item")
                    .startAfter(true, lastLoadedKey)
                    .limitToFirst(PAGE_SIZE);

            nextPageQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    for (DataSnapshot child : snapshot.getChildren()) {
                        ProductModel product = child.getValue(ProductModel.class);

                        if (product != null && findIndexById(product.getProduct_id()) == -1) {
                            productList.add(product);
                            lastLoadedKey = child.getKey();
                        }
                    }

                    if (snapshot.getChildrenCount() < PAGE_SIZE) {
                        hasMoreData = false;
                    }

                    publishList();
                    isLoading = false;
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    isLoading = false;
                }
            });

        }, 800);
    }

    private void publishList() {
        trendingProducts.setValue(new ArrayList<>(productList));
    }

    private int findIndexById(String productId) {
        if (productId == null) return -1;

        for (int i = 0; i < productList.size(); i++) {
            if (productId.equals(productList.get(i).getProduct_id())) {
                return i;
            }
        }

        return -1;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        handler.removeCallbacksAndMessages(null);

        if (productsRef != null && childEventListener != null) {
            productsRef.removeEventListener(childEventListener);
        }
    }
}