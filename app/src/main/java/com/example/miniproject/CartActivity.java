package com.example.miniproject;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject.CartManagment.CartAdapter;
import com.example.miniproject.CartManagment.CartModel;
import com.example.miniproject.CartManagment.CartViewModel;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private TextView subtotalPrice, deliveryCharge, totalPrice;
    private ImageView backBtn;

    private CartAdapter cartAdapter;
    private CartViewModel cartViewModel;

    private int deliveryAmount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        subtotalPrice = findViewById(R.id.subtotalPrice);
        deliveryCharge = findViewById(R.id.deliveryCharge);
        totalPrice = findViewById(R.id.totalPrice);
        backBtn = findViewById(R.id.backBtn);

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

        // Important for no flicker/blink on live quantity update
        cartRecyclerView.setItemAnimator(null);

        backBtn.setOnClickListener(v -> finish());

        cartViewModel.getCartLiveData().observe(this, this::updateCartUi);

        cartViewModel.startListeningCart();

    }

    private void updateCartUi(List<CartModel> list) {
        cartAdapter.submitList(list);

        int subtotal = 0;

        for (CartModel item : list) {
            subtotal += item.getProduct_price() * item.getQuantity();
            deliveryAmount += subtotal*.05;
        }

        int total = list.isEmpty() ? 0 : subtotal + deliveryAmount;

        subtotalPrice.setText("₹" + subtotal);
        deliveryCharge.setText(list.isEmpty() ? "₹0" : "₹" + deliveryAmount);
        totalPrice.setText("₹" + total);
    }
}