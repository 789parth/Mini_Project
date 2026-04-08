package com.example.miniproject.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.miniproject.DetailsBottomSheetFragment;
import com.example.miniproject.R;
import com.example.miniproject.StartActivity;
import com.example.miniproject.ViewModels.CategoryViewModel;
import com.example.miniproject.adapter.categoryadapter;
import com.example.miniproject.adapter.productadapter;
import com.example.miniproject.domain.categorys;
import com.example.miniproject.domain.products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomeFragment1 extends Fragment {

    TextView viewAll;
    ImageSlider imageSlider;
    ImageView logoutBtn;
    RecyclerView recViewTrending, recViewCategory, recViewList1;
    productadapter adapter, listAdapter1;
    categoryadapter adapter1;
    ProgressBar trendingProgress,list1Progress,categoryProgress;
    CategoryViewModel categoryViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home1, container, false);

        //Progess bar
        list1Progress = view.findViewById(R.id.progressBarList1);
        trendingProgress = view.findViewById(R.id.progressBarTrending);
        categoryProgress = view.findViewById(R.id.progressBarCategory);



        viewAll = view.findViewById(R.id.viewAll);
        viewAll.setOnClickListener(v -> {
            DetailsBottomSheetFragment detailsBottomSheetFragment = new DetailsBottomSheetFragment();

            // Passing sample data so it doesn't appear empty
            Bundle bundle = new Bundle();
            bundle.putString("name", "Trending Items");
            bundle.putString("image", "android.resource://com.example.miniproject/" + R.drawable.banner1);
            bundle.putString("description", "Explore our top trending items of the day. Fresh and high-quality groceries delivered to your doorstep.");
            bundle.putString("ingredients", "Fresh Fruits, Vegetables, Dairy Products");

            detailsBottomSheetFragment.setArguments(bundle);
            detailsBottomSheetFragment.show(getParentFragmentManager(), "DetailsBottomSheetFragment");
        });

        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.banner2, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.banner3, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.banner4, ScaleTypes.FIT));
        imageList.add(new SlideModel(R.drawable.banner5, ScaleTypes.FIT));

        imageSlider = view.findViewById(R.id.imageSlider);
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

        //Trending recycler view.
        recViewTrending = view.findViewById(R.id.recViewTrending);
        trendingItems();

        //Category Recycler view.
        recViewCategory = view.findViewById(R.id.recViewCategory);
        categoryItems();

        //List-1 section.
        recViewList1 = view.findViewById(R.id.recViewList1);
        listItems();


        logoutBtn = view.findViewById(R.id.logoutBtn);
        logout();


        return view;
    }

    private void logout() {
        logoutBtn.setOnClickListener(v -> {

            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        // 🔴 Firebase logout
                        FirebaseAuth.getInstance().signOut();

                        // 🔴 Login screen open + back stack clear
                        Intent intent = new Intent(requireContext(), StartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();

        });
    }

    //======================
    // Recycler view methods.
    //=======================
    private void listItems() {
        list1Progress.setVisibility(View.VISIBLE);
        recViewList1.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        FirebaseRecyclerOptions<products> options2 =
                new FirebaseRecyclerOptions.Builder<products>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("products").orderByChild("category_id")
                                .equalTo("-On_Yc9z2K1dzU4979c8"), products.class)
                        .build();

        recViewList1.setHasFixedSize(false);
        recViewList1.setSaveEnabled(false);

        listAdapter1 = new productadapter(options2, list1Progress);
        recViewList1.setAdapter(listAdapter1);

    }

    private void categoryItems() {

        categoryProgress.setVisibility(View.VISIBLE);

        recViewCategory.setHasFixedSize(false);
        recViewCategory.setNestedScrollingEnabled(false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        recViewCategory.setLayoutManager(gridLayoutManager);

        categoryViewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);
        adapter1 = new categoryadapter(categoryViewModel.list);
        recViewCategory.setAdapter(adapter1);

        categoryViewModel.init(() -> {
            adapter1.notifyDataSetChanged();
            categoryProgress.setVisibility(View.GONE);
        });

    }

    private void trendingItems() {
        trendingProgress.setVisibility(View.VISIBLE);

        recViewTrending.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        FirebaseRecyclerOptions<products> options =
                new FirebaseRecyclerOptions.Builder<products>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("products").orderByChild("trending_item")
                                .equalTo(true), products.class)
                        .build();

        recViewTrending.setHasFixedSize(false);
        recViewTrending.setSaveEnabled(false);

        adapter = new productadapter(options, trendingProgress);
        recViewTrending.setAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
        if (listAdapter1 != null) listAdapter1.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
            recViewTrending.setAdapter(null);
        }

        if (listAdapter1 != null) {
            listAdapter1.stopListening();
            recViewList1.setAdapter(null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (recViewTrending != null && adapter != null) {
            recViewTrending.setAdapter(null);
            recViewTrending.setAdapter(adapter);
        }

        if (recViewList1 != null && listAdapter1 != null) {
            recViewList1.setAdapter(null);
            recViewList1.setAdapter(listAdapter1);
        }
    }



}
