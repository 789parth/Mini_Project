package com.example.miniproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.miniproject.adapter.MenuAdapter;
import com.example.miniproject.databinding.FragmentMenuBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuBottomSheetFragment extends BottomSheetDialogFragment {

    private FragmentMenuBottomSheetBinding binding;

    public MenuBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMenuBottomSheetBinding.inflate(inflater, container, false);
        binding.btnBack.setOnClickListener(v -> dismiss());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<String> menuFoodName = Arrays.asList(
                "Tomato (1kg)",
                "Kishan Jam (1kg)",
                "TATA Salt (1kg)",
                "Cooking Oil (1L)",
                "Bread (400g)",
                "Eggs (6pcs)",
                "Milk (1L)",
                "Butter (100g)"
        );

        List<String> menuPrice = Arrays.asList(
                "₹60",
                "₹45",
                "₹50",
                "₹120",
                "₹40",
                "₹42",
                "₹64",
                "₹55"
        );

        List<Integer> menuImage = Arrays.asList(
                R.drawable.menu1,
                R.drawable.menu2,
                R.drawable.menu3,
                R.drawable.menu4,
                R.drawable.menu1,
                R.drawable.menu2,
                R.drawable.menu3,
                R.drawable.menu4
        );

        MenuAdapter adapter = new MenuAdapter(new ArrayList<>(menuFoodName), new ArrayList<>(menuPrice), new ArrayList<>(menuImage));
        binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.menuRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
