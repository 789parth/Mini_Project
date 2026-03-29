package com.example.miniproject.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.miniproject.R;
import com.example.miniproject.adapter.CartAdapter;
import com.example.miniproject.databinding.FragmentCartBinding;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<String> cartItems = new ArrayList<>();
        cartItems.add("Orange");
        cartItems.add("Apple");
        cartItems.add("Banana");

        List<String> cartPrice = new ArrayList<>();
        cartPrice.add("$5");
        cartPrice.add("$10");
        cartPrice.add("$8");

        List<String> cartWeight = new ArrayList<>();
        cartWeight.add("500g");
        cartWeight.add("1kg");
        cartWeight.add("2kg");

        List<Integer> cartImage = new ArrayList<>();
        cartImage.add(R.drawable.menu1);
        cartImage.add(R.drawable.menu1);
        cartImage.add(R.drawable.menu1);

        List<Integer> cartQuantity = new ArrayList<>();
        cartQuantity.add(1);
        cartQuantity.add(1);
        cartQuantity.add(1);

        CartAdapter adapter = new CartAdapter(cartItems, cartPrice, cartWeight, cartImage, cartQuantity);
        binding.cartRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.cartRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
