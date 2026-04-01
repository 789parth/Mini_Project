package com.example.miniproject.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.miniproject.DetailsBottomSheetFragment;
import com.example.miniproject.R;
import com.example.miniproject.adapter.categoryadapter;
import com.example.miniproject.adapter.productadapter;
import com.example.miniproject.domain.categorys;
import com.example.miniproject.domain.products;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomeFragment1 extends Fragment {

    TextView viewAll;
    ImageSlider imageSlider;
    RecyclerView recViewTrending, recViewCategory, recViewList1;
    productadapter adapter, listAdapter1;
    categoryadapter adapter1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home1, container, false);

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
        recViewTrending.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        FirebaseRecyclerOptions<products> options =
                new FirebaseRecyclerOptions.Builder<products>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Products").orderByChild("TrendingItem")
                                .equalTo(false), products.class)
                        .build();

        adapter = new productadapter(options);
        recViewTrending.setAdapter(adapter);

        //Category Recycler view.
        recViewCategory = view.findViewById(R.id.recViewCategory);
        recViewCategory.setHasFixedSize(false);
        recViewCategory.setNestedScrollingEnabled(false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        recViewCategory.setLayoutManager(gridLayoutManager);

        FirebaseRecyclerOptions<categorys> options1 =
                new FirebaseRecyclerOptions.Builder<categorys>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("category"), categorys.class)
                        .build();

        adapter1 = new categoryadapter(options1);
        recViewCategory.setAdapter(adapter1);

        recViewCategory.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (recViewCategory != null) {
                recViewCategory.requestLayout();
            }
        });

        //List-1 section.
        recViewList1 = view.findViewById(R.id.recViewList1);
        recViewList1.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        FirebaseRecyclerOptions<products> options2 =
                new FirebaseRecyclerOptions.Builder<products>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Products").orderByChild("Category")
                                .equalTo("vegetable"), products.class)
                        .build();
        listAdapter1 = new productadapter(options2);
        recViewList1.setAdapter(listAdapter1);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) adapter.startListening();
        if (adapter1 != null) adapter1.startListening();
        if (listAdapter1 != null) listAdapter1.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) adapter.stopListening();
        if (adapter1 != null) adapter1.stopListening();
        if (listAdapter1 != null) listAdapter1.stopListening();
    }
}
