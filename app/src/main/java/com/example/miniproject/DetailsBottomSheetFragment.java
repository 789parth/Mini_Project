package com.example.miniproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.miniproject.databinding.FragmentDetailsBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DetailsBottomSheetFragment extends BottomSheetDialogFragment {

    private FragmentDetailsBottomSheetBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailsBottomSheetBinding.inflate(inflater, container, false);
        binding.close.setOnClickListener(v -> dismiss());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String name = bundle.getString("name");
            String image = bundle.getString("image");
            String description = bundle.getString("description");
            String ingredients = bundle.getString("ingredients");

            binding.detailsFoodName.setText(name);
            binding.descriptionTextView.setText(description);
            binding.IngredientTextView.setText(ingredients);
            Glide.with(requireContext()).load(image).into(binding.detailFoodImage);
        }

        binding.close.setOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
