package com.example.miniproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject.CartManagment.CartAdapter;
import com.example.miniproject.CartManagment.CartModel;
import com.example.miniproject.CartManagment.CartViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private TextView subtotalPrice, deliveryCharge, totalPrice;
    private ImageView backBtn;
    private Button checkoutBtn;

    private CartAdapter cartAdapter;
    private CartViewModel cartViewModel;

    private int currentSubtotal = 0;
    private int currentDelivery = 0;
    private int currentTotal = 0;

    private DatabaseReference rootRef;
    private String userId;
    private String userLocation = "Not Available";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        subtotalPrice = findViewById(R.id.subtotalPrice);
        deliveryCharge = findViewById(R.id.deliveryCharge);
        totalPrice = findViewById(R.id.totalPrice);
        backBtn = findViewById(R.id.backBtn);
        checkoutBtn = findViewById(R.id.checkoutBtn);

        rootRef = FirebaseDatabase.getInstance().getReference();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        readUserLocation();

        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);

        cartAdapter = new CartAdapter(new CartAdapter.CartClickListener() {
            @Override
            public void onPlusClick(CartModel item) {
                cartViewModel.increaseQuantity(item);
            }

            @Override
            public void onMinusClick(CartModel item) {
                cartViewModel.decreaseQuantity(item);
            }

            @Override
            public void onDeleteClick(CartModel item) {
                cartViewModel.deleteCartItem(item);
            }
        });

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(cartAdapter);
        cartRecyclerView.setItemAnimator(null);

        backBtn.setOnClickListener(v -> finish());

        checkoutBtn.setOnClickListener(v -> placeOrder());

        cartViewModel.getCartLiveData().observe(this, this::updateCartUi);
        cartViewModel.startListeningCart();
    }

    private void readUserLocation() {
        if (userId == null) return;

        rootRef.child("users")
                .child(userId)
                .child("location")
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists() && snapshot.getValue() != null) {
                        userLocation = snapshot.getValue(String.class);
                    }
                })
                .addOnFailureListener(e -> userLocation = "Not Available");
    }

    private void updateCartUi(List<CartModel> list) {
        cartAdapter.submitList(list);

        currentSubtotal = 0;

        for (CartModel item : list) {
            currentSubtotal += item.getProduct_price() * item.getQuantity();
        }

        currentDelivery = list.isEmpty() ? 0 : (int) (currentSubtotal * 0.05);
        currentTotal = currentSubtotal + currentDelivery;

        subtotalPrice.setText("₹" + currentSubtotal);
        deliveryCharge.setText("₹" + currentDelivery);
        totalPrice.setText("₹" + currentTotal);
    }

    private void placeOrder() {
        List<CartModel> cartList = cartAdapter.getCurrentList();

        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cartList == null || cartList.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        checkoutBtn.setEnabled(false);
        checkoutBtn.setText("Placing...");

        String orderId = rootRef.child("orders").child(userId).push().getKey();

        if (orderId == null) {
            resetCheckoutButton();
            Toast.makeText(this, "Order ID not created", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String time = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

        HashMap<String, Object> orderMap = new HashMap<>();
        orderMap.put("order_id", orderId);
        orderMap.put("user_id", userId);
        orderMap.put("date", date);
        orderMap.put("time", time);
        orderMap.put("subtotal", currentSubtotal);
        orderMap.put("delivery_charge", currentDelivery);
        orderMap.put("total", currentTotal);
        orderMap.put("status", "Pending");
        orderMap.put("delivery_address", userLocation);
        orderMap.put("payment_method", "Cash on Delivery");

        HashMap<String, Object> productsMap = new HashMap<>();

        for (CartModel item : cartList) {
            HashMap<String, Object> product = new HashMap<>();

            product.put("product_id", item.getProductId());
            product.put("product_name", item.getProduct_name());
            product.put("product_image", item.getProduct_image());
            product.put("product_price", item.getProduct_price());
            product.put("quantity", item.getQuantity());
            product.put("item_total", item.getProduct_price() * item.getQuantity());

            productsMap.put(item.getProductId(), product);
        }

        orderMap.put("products", productsMap);

        rootRef.child("orders")
                .child(userId)
                .child(orderId)
                .setValue(orderMap)
                .addOnSuccessListener(unused -> {

                    updateProductStock(cartList, () -> {

                        rootRef.child("carts")
                                .child(userId)
                                .removeValue()
                                .addOnSuccessListener(unused1 -> {
                                    Toast.makeText(this,
                                            "Order placed successfully",
                                            Toast.LENGTH_SHORT).show();

                                    resetCheckoutButton();
                                })
                                .addOnFailureListener(e -> {
                                    resetCheckoutButton();
                                    Toast.makeText(this,
                                            e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    });

                })
                .addOnFailureListener(e -> {
                    resetCheckoutButton();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateProductStock(List<CartModel> cartList, Runnable onComplete) {

        final int[] completed = {0};

        for (CartModel item : cartList) {

            DatabaseReference productRef = rootRef.child("products")
                    .child(item.getProductId());

            productRef.child("product_quantity")
                    .get()
                    .addOnSuccessListener(snapshot -> {

                        Long stockLong = snapshot.getValue(Long.class);

                        if (stockLong != null) {

                            int currentStock = stockLong.intValue();
                            int updatedStock = currentStock - item.getQuantity();

                            if (updatedStock < 0) {
                                updatedStock = 0;
                            }

                            productRef.child("product_quantity")
                                    .setValue(updatedStock)
                                    .addOnCompleteListener(task -> {
                                        completed[0]++;

                                        if (completed[0] == cartList.size()) {
                                            onComplete.run();
                                        }
                                    });

                        } else {
                            completed[0]++;

                            if (completed[0] == cartList.size()) {
                                onComplete.run();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        completed[0]++;

                        if (completed[0] == cartList.size()) {
                            onComplete.run();
                        }
                    });
        }
    }

    private void resetCheckoutButton() {
        checkoutBtn.setEnabled(true);
        checkoutBtn.setText("Checkout");
    }
}