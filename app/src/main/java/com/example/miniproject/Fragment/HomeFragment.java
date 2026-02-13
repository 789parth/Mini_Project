package com.example.miniproject.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.miniproject.R;
import com.example.miniproject.adapter.PopularAdapter;
import com.example.miniproject.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 🔹 Image Slider Setup
        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.banner2, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.banner3, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.banner4, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.banner5, ScaleTypes.FIT));

        ImageSlider imageSlider = binding.imageSlider;
        imageSlider.setImageList(imageList, ScaleTypes.FIT);

        imageSlider.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemSelected(int position) {
                String itemMessage = "Selected Image " + position;
                Toast.makeText(requireContext(), itemMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doubleClick(int position) {
                // optional
            }
        });


        List<String> foodName = Arrays.asList(
                "Tomato (1kg)",
                "Kishan Jam (1kg)",
                "TATA Salt (1kg)",
                "Cooking Oil (1L)"
        );

        List<String> price = Arrays.asList(
                "₹60",
                "₹45",
                "₹50",
                "₹120"
        );

        List<Integer> populerFoodImages = Arrays.asList(
                R.drawable.menu1,
                R.drawable.menu2,
                R.drawable.menu3,
                R.drawable.menu4
        );

        PopularAdapter adapter = new PopularAdapter(foodName, price, populerFoodImages);

        binding.popluarRecycleview.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        binding.popluarRecycleview.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
