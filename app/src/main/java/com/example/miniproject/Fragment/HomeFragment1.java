package com.example.miniproject.Fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.example.miniproject.CategoryActivity;
import com.example.miniproject.DetailsBottomSheetFragment;
import com.example.miniproject.ManagerClass.SessionManager;
import com.example.miniproject.ProductActivity;
import com.example.miniproject.R;
import com.example.miniproject.SearchActivity;
import com.example.miniproject.StartActivity;
import com.example.miniproject.TrendingProduct.ProductAdapter;
import com.example.miniproject.TrendingProduct.ProductViewModel;
import com.example.miniproject.Category.CategoryViewModel;

import com.google.firebase.auth.FirebaseAuth;

import com.example.miniproject.Category.CategoryModel;
import com.example.miniproject.TrendingProduct.ProductModel;
import com.example.miniproject.NestedProduct.NestedCategoryPagingAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class HomeFragment1 extends Fragment {

    private LinearLayoutManager layoutManager;
    private SessionManager sessionManager;
    TextView viewAllBtn,textView2;
    EditText searchEditText;
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

        EditText searchEditText = view.findViewById(R.id.searchText);

        searchEditText.setFocusable(false);
        searchEditText.setClickable(true);

        searchEditText.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            startActivity(intent);
        });


        viewAllBtn = view.findViewById(R.id.viewAllBtn);
        viewAllBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProductActivity.class);
            intent.putExtra("type", "trending");
            intent.putExtra("title", "Trending Products");
            startActivity(intent);
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

        TextView exploreMore = view.findViewById(R.id.textView10);

        exploreMore.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CategoryActivity.class);
            startActivity(intent);
        });

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
        CategoryAdapter categoryAdapter = new CategoryAdapter(category -> {
            Intent intent = new Intent(getContext(), ProductActivity.class);
            intent.putExtra("category_id", category.getCategory_id());
            intent.putExtra("category_title", category.getCategory_title());
            startActivity(intent);
        });

        GridLayoutManager gridLayoutManager =
                new GridLayoutManager(requireContext(), 4);

        recViewCategory.setLayoutManager(gridLayoutManager);
        recViewCategory.setAdapter(categoryAdapter);
        recViewCategory.setHasFixedSize(false);
        recViewCategory.setItemAnimator(null);

        CategoryViewModel categoryViewModel = new ViewModelProvider(requireActivity())
                .get(CategoryViewModel.class);

        categoryViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            filterCategoriesWithProducts(new ArrayList<>(categories), categoryAdapter);
        });
    }

    private void filterCategoriesWithProducts(
            ArrayList<CategoryModel> categories,
            CategoryAdapter categoryAdapter
    ) {
        FirebaseDatabase.getInstance()
                .getReference("products")
                .get()
                .addOnSuccessListener(snapshot -> {

                    ArrayList<String> categoryIdsWithProducts = new ArrayList<>();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ProductModel product = ds.getValue(ProductModel.class);

                        if (product != null && product.getCategory_id() != null) {
                            categoryIdsWithProducts.add(product.getCategory_id().trim());
                        }
                    }

                    ArrayList<CategoryModel> filteredCategories = new ArrayList<>();

                    for (CategoryModel category : categories) {
                        if (category.getCategory_id() != null &&
                                categoryIdsWithProducts.contains(category.getCategory_id().trim())) {

                            CategoryModel newCategory = new CategoryModel();
                            newCategory.setCategory_id(category.getCategory_id());
                            newCategory.setCategory_title(category.getCategory_title());
                            newCategory.setCategory_image(category.getCategory_image());
                            newCategory.setStore_id(category.getStore_id());

                            // IMPORTANT
                            newCategory.setHasProducts(true);

                            filteredCategories.add(newCategory);
                        }
                    }

                    categoryAdapter.updateList(filteredCategories);
                    categoryProgress.setVisibility(View.GONE);
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
