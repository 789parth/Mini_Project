package com.example.miniproject.CartManagment;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CartViewModel extends ViewModel {

    private final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

    private final String userId = FirebaseAuth.getInstance().getCurrentUser() != null
            ? FirebaseAuth.getInstance().getCurrentUser().getUid()
            : null;

    private DatabaseReference cartRef;
    private ChildEventListener childEventListener;

    private final ArrayList<CartModel> cartItems = new ArrayList<>();
    private final MutableLiveData<List<CartModel>> cartLiveData = new MutableLiveData<>();

    public LiveData<List<CartModel>> getCartLiveData() {
        return cartLiveData;
    }

    public void startListeningCart() {
        if (userId == null) return;

        cartRef = rootRef.child("carts").child(userId);

        childEventListener = new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                CartModel item = snapshot.getValue(CartModel.class);
                if (item == null) return;

                item.setUserId(userId);
                item.setProductId(snapshot.getKey());

                if (findIndexByProductId(item.getProductId()) == -1) {
                    cartItems.add(item);
                    cartLiveData.setValue(new ArrayList<>(cartItems));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                CartModel item = snapshot.getValue(CartModel.class);
                if (item == null) return;

                item.setUserId(userId);
                item.setProductId(snapshot.getKey());

                int index = findIndexByProductId(item.getProductId());

                if (index != -1) {
                    cartItems.set(index, item);
                    cartLiveData.setValue(new ArrayList<>(cartItems));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String productId = snapshot.getKey();
                int index = findIndexByProductId(productId);

                if (index != -1) {
                    cartItems.remove(index);
                    cartLiveData.setValue(new ArrayList<>(cartItems));
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

        cartRef.addChildEventListener(childEventListener);
    }

    private int findIndexByProductId(String productId) {
        if (productId == null) return -1;

        for (int i = 0; i < cartItems.size(); i++) {
            if (productId.equals(cartItems.get(i).getProductId())) {
                return i;
            }
        }

        return -1;
    }

    public void increaseQuantity(CartModel item) {
        if (userId == null || item.getProductId() == null) return;

        rootRef.child("carts")
                .child(userId)
                .child(item.getProductId())
                .child("quantity")
                .setValue(item.getQuantity() + 1);
    }

    public void decreaseQuantity(CartModel item) {
        if (userId == null || item.getProductId() == null) return;

        if (item.getQuantity() <= 1) {
            deleteCartItem(item);
        } else {
            rootRef.child("carts")
                    .child(userId)
                    .child(item.getProductId())
                    .child("quantity")
                    .setValue(item.getQuantity() - 1);
        }
    }

    public void deleteCartItem(CartModel item) {
        if (userId == null || item.getProductId() == null) return;

        rootRef.child("carts")
                .child(userId)
                .child(item.getProductId())
                .removeValue();
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if (cartRef != null && childEventListener != null) {
            cartRef.removeEventListener(childEventListener);
        }
    }
}
