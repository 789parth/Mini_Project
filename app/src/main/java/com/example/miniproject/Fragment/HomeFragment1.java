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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.miniproject.Category.CategoryAdapter;
import com.example.miniproject.DetailsBottomSheetFragment;
import com.example.miniproject.ManagerClass.SessionManager;
import com.example.miniproject.R;
import com.example.miniproject.StartActivity;
import com.example.miniproject.TrendingProduct.ProductAdapter;
import com.example.miniproject.TrendingProduct.ProductViewModel;
import com.example.miniproject.Category.CategoryViewModel;

import com.google.firebase.auth.FirebaseAuth;

import com.example.miniproject.Category.CategoryModel;
import com.example.miniproject.TrendingProduct.ProductModel;
import com.example.miniproject.NestedProduct.NestedCategoryPagingAdapter;

import java.util.ArrayList;

public class HomeFragment1 extends Fragment {

    private LinearLayoutManager layoutManager;
    private SessionManager sessionManager;
    TextView viewAll,textView2;
    ImageSlider imageSlider;
    ImageView logoutBtn;
    RecyclerView recViewTrending, recViewCategory;
    ProgressBar trendingProgress,list1Progress,categoryProgress;

    //nested view

    RecyclerView recViewNestedProducts;
    private NestedCategoryPagingAdapter nestedCategoryPagingAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home1, container, false);

        //session manager initialize and set username.
        sessionManager = new SessionManager(requireContext());

        textView2 = view.findViewById(R.id.textView2);
        textView2.setText(sessionManager.getUsername());

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

        recViewCategory = view.findViewById(R.id.recViewCategory);
        categoryItems();

        recViewNestedProducts = view.findViewById(R.id.recViewList1);
        nestedPagingProducts();


        logoutBtn = view.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> logout());


        return view;
    }

    private void logout() {

            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        // 🔴 Firebase logout
                        FirebaseAuth.getInstance().signOut();

                        //Session manager logout
                        sessionManager.logout();

                        // 🔴 Login screen open + back stack clear
                        Intent intent = new Intent(requireContext(), StartActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
    }

    //======================
    // Recycler view methods.
    //=======================
    private void trendingItems() {
        ProductAdapter productAdapter = new ProductAdapter();

        layoutManager = new LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
        );

        recViewTrending.setLayoutManager(layoutManager);
        recViewTrending.setAdapter(productAdapter);
        recViewTrending.setHasFixedSize(true);
        recViewTrending.setItemAnimator(null);

        ProductViewModel productViewModel = new ViewModelProvider(requireActivity())
                .get(ProductViewModel.class);

        productViewModel.getTrendingProducts().observe(getViewLifecycleOwner(), products -> {
            trendingProgress.setVisibility(View.GONE);
            productAdapter.updateList(products);

        });

        recViewTrending.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (totalItemCount > 0 && lastVisibleItem >= totalItemCount - 3) {
                    productViewModel.loadNextPage();
                }
            }
        });

    }

    private void categoryItems() {
        CategoryAdapter categoryAdapter = new CategoryAdapter();

        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(requireContext(), 4);

        recViewCategory.setLayoutManager(gridLayoutManager);
        recViewCategory.setAdapter(categoryAdapter);
        recViewCategory.setHasFixedSize(false);
        recViewCategory.setItemAnimator(null);

        CategoryViewModel categoryViewModel = new ViewModelProvider(requireActivity())
                .get(CategoryViewModel.class);

        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            categoryProgress.setVisibility(View.GONE);
            categoryAdapter.updateList(categories);
        });
    }

    private void nestedPagingProducts() {

        nestedCategoryPagingAdapter = new NestedCategoryPagingAdapter();

        recViewNestedProducts.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        );

        recViewNestedProducts.setAdapter(nestedCategoryPagingAdapter);
        recViewNestedProducts.setHasFixedSize(false);
        recViewNestedProducts.setItemAnimator(null);

        CategoryViewModel categoryViewModel = new ViewModelProvider(requireActivity())
                .get(CategoryViewModel.class);

        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            nestedCategoryPagingAdapter.updateList(categories);
        });
    }
}
