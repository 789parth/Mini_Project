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
import com.example.miniproject.adapter.BuyAgainAdapter;
import com.example.miniproject.databinding.FragmentHistoryBinding;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private BuyAgainAdapter buyAgainAdapter;

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
    }

    private void setupRecyclerView() {

        ArrayList<String> buyAgainFoodName = new ArrayList<>();
        buyAgainFoodName.add("Tomato (1kg)");
        buyAgainFoodName.add("TATA Salt (1kg)");
        buyAgainFoodName.add("Kissan Mixed Fruit Jam (500g)");

        ArrayList<String> buyAgainFoodPrice = new ArrayList<>();
        buyAgainFoodPrice.add("₹40");
        buyAgainFoodPrice.add("₹28");
        buyAgainFoodPrice.add("₹110");

        ArrayList<Integer> buyAgainFoodImage = new ArrayList<>();
        buyAgainFoodImage.add(R.drawable.menu1);
        buyAgainFoodImage.add(R.drawable.menu2);
        buyAgainFoodImage.add(R.drawable.menu3);

        buyAgainAdapter = new BuyAgainAdapter(
                buyAgainFoodName,
                buyAgainFoodPrice,
                buyAgainFoodImage
        );

        binding.buyAgainRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        binding.buyAgainRecyclerView.setAdapter(buyAgainAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
