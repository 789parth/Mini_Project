package com.example.miniproject.NestedProduct;

import com.example.miniproject.TrendingProduct.ProductModel;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class CategoryProductPager {

    public interface ProductPageCallback {
        void onProductsChanged(List<ProductModel> products);
        void onEmpty();
    }

    private static final int LIMIT = 10;

    private final ProductPageCallback callback;
    private final List<ProductModel> productList = new ArrayList<>();

    private final Query query;
    private ChildEventListener childEventListener;

    public CategoryProductPager(String categoryId, ProductPageCallback callback) {
        this.callback = callback;

        query = FirebaseDatabase.getInstance()
                .getReference("products")
                .orderByChild("category_id")
                .equalTo(categoryId)
                .limitToFirst(LIMIT);
    }

    public void startListening() {

        // First check if product exists
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    callback.onEmpty();
                    return;
                }

                attachChildListener();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onEmpty();
            }
        });
    }

    private void attachChildListener() {
        if (childEventListener != null) return;

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                ProductModel product = snapshot.getValue(ProductModel.class);

                if (product != null && findIndex(product.getProduct_id()) == -1) {
                    productList.add(product);
                    publish();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                ProductModel updated = snapshot.getValue(ProductModel.class);
                if (updated == null) return;

                int index = findIndex(updated.getProduct_id());

                if (index >= 0) {
                    productList.set(index, updated);
                    publish();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                ProductModel removed = snapshot.getValue(ProductModel.class);
                if (removed == null) return;

                int index = findIndex(removed.getProduct_id());

                if (index >= 0) {
                    productList.remove(index);

                    if (productList.isEmpty()) {
                        callback.onEmpty();
                    } else {
                        publish();
                    }
                }
            }

            @Override public void onChildMoved(DataSnapshot snapshot, String previousChildName) {}
            @Override public void onCancelled(DatabaseError error) {}
        };

        query.addChildEventListener(childEventListener);
    }

    private void publish() {
        callback.onProductsChanged(new ArrayList<>(productList));
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

    public void detach() {
        if (childEventListener != null) {
            query.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }
}