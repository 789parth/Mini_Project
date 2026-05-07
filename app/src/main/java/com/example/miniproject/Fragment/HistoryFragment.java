package com.example.miniproject.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.miniproject.adapter.BuyAgainAdapter;
import com.example.miniproject.databinding.FragmentHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private BuyAgainAdapter buyAgainAdapter;

    private final ArrayList<String> foodNames     = new ArrayList<>();
    private final ArrayList<String> foodPrices    = new ArrayList<>();
    private final ArrayList<String> foodImageUrls = new ArrayList<>();
    private final ArrayList<String> productIds    = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        loadOrderHistory();
    }

    private void setupRecyclerView() {
        buyAgainAdapter = new BuyAgainAdapter(foodNames, foodPrices, foodImageUrls, productIds);

        // ── Add to Cart from history ───────────────────────────────────────
        buyAgainAdapter.setOnAddToCartListener((position, productId, name, priceStr, imageUrl) -> {
            String uid = FirebaseAuth.getInstance().getUid();
            if (uid == null) {
                Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }

            int price = 0;
            try {
                String numeric = priceStr.replaceAll("[^0-9]", "").trim();
                if (!numeric.isEmpty())
                    price = Integer.parseInt(numeric.substring(0, Math.min(numeric.length(), 6)));
            } catch (Exception ignored) {}

            String cartKey = (productId != null && !productId.isEmpty())
                    ? productId
                    : name.replaceAll("[^a-zA-Z0-9]", "_");

            final DatabaseReference cartRef = FirebaseDatabase.getInstance()
                    .getReference("carts").child(uid).child(cartKey);
            final int finalPrice = price;

            cartRef.get().addOnSuccessListener(snapshot -> {
                if (!isAdded()) return;
                if (snapshot.exists()) {
                    Integer currentQty = snapshot.child("quantity").getValue(Integer.class);
                    int newQty = (currentQty != null ? currentQty : 1) + 1;
                    cartRef.child("quantity").setValue(newQty)
                            .addOnSuccessListener(u ->
                                    Toast.makeText(getContext(), name + " qty updated in cart", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                } else {
                    HashMap<String, Object> cartItem = new HashMap<>();
                    cartItem.put("product_name",  name);
                    cartItem.put("product_image", imageUrl);
                    cartItem.put("product_price", finalPrice);
                    cartItem.put("quantity",      1);

                    cartRef.setValue(cartItem)
                            .addOnSuccessListener(u ->
                                    Toast.makeText(getContext(), name + " added to cart!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).addOnFailureListener(e ->
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        binding.buyAgainRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.buyAgainRecyclerView.setAdapter(buyAgainAdapter);
    }

    private void loadOrderHistory() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance().getReference("orders").child(uid)
                .limitToLast(20)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isAdded()) return;

                        foodNames.clear();
                        foodPrices.clear();
                        foodImageUrls.clear();
                        productIds.clear();

                        for (DataSnapshot orderSnap : snapshot.getChildren()) {
                            for (DataSnapshot product : orderSnap.child("products").getChildren()) {
                                String  name     = product.child("product_name").getValue(String.class);
                                String  imageUrl = product.child("product_image").getValue(String.class);
                                String  pid      = product.child("product_id").getValue(String.class);
                                Integer price    = product.child("product_price").getValue(Integer.class);
                                Integer qty      = product.child("quantity").getValue(Integer.class);

                                if (name != null) {
                                    foodNames.add(name);
                                    foodImageUrls.add(imageUrl != null ? imageUrl : "");
                                    productIds.add(pid != null ? pid : "");
                                    if (price != null && qty != null && qty > 1)
                                        foodPrices.add("₹" + price + " × " + qty);
                                    else if (price != null)
                                        foodPrices.add("₹" + price);
                                    else
                                        foodPrices.add("—");
                                }
                            }
                        }
                        buyAgainAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (isAdded())
                            Toast.makeText(getContext(), "Failed to load history", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
