package com.example.miniproject.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.miniproject.R;
import com.example.miniproject.adapter.MenuAdapter;
import com.example.miniproject.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;
    private MenuAdapter adapter;
    private List<String> originalFoodName;
    private List<String> originalPrice;
    private List<Integer> originalImages;

    private List<String> filteredFoodName = new ArrayList<>();
    private List<String> filteredPrice = new ArrayList<>();
    private List<Integer> filteredImages = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Data
        originalFoodName = Arrays.asList(
                "Tomato (1kg)", "Kishan Jam (1kg)", "TATA Salt (1kg)", "Cooking Oil (1L)",
                "Bread (400g)", "Eggs (6pcs)", "Milk (1L)", "Butter (100g)"
        );
        originalPrice = Arrays.asList(
                "₹60", "₹45", "₹50", "₹120", "₹40", "₹42", "₹64", "₹55"
        );
        originalImages = Arrays.asList(
                R.drawable.menu1, R.drawable.menu2, R.drawable.menu3, R.drawable.menu4,
                R.drawable.menu1, R.drawable.menu2, R.drawable.menu3, R.drawable.menu4
        );

        // Initially show all items
        showAllMenu();

        setupRecyclerView();
        setupSearchView();
    }

    private void setupRecyclerView() {
        adapter = new MenuAdapter(filteredFoodName, filteredPrice, filteredImages);
        binding.searchRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.searchRecyclerView.setAdapter(adapter);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterMenu(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterMenu(newText);
                return true;
            }
        });
    }

    private void filterMenu(String query) {
        filteredFoodName.clear();
        filteredPrice.clear();
        filteredImages.clear();

        for (int i = 0; i < originalFoodName.size(); i++) {
            if (originalFoodName.get(i).toLowerCase().contains(query.toLowerCase())) {
                filteredFoodName.add(originalFoodName.get(i));
                filteredPrice.add(originalPrice.get(i));
                filteredImages.add(originalImages.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void showAllMenu() {
        filteredFoodName.clear();
        filteredPrice.clear();
        filteredImages.clear();

        filteredFoodName.addAll(originalFoodName);
        filteredPrice.addAll(originalPrice);
        filteredImages.addAll(originalImages);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
