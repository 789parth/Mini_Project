package com.example.miniproject.NestedProduct;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject.Category.CategoryModel;
import com.example.miniproject.R;
import com.example.miniproject.TrendingProduct.ProductAdapter;
import com.example.miniproject.TrendingProduct.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class NestedCategoryPagingAdapter
        extends RecyclerView.Adapter<NestedCategoryPagingAdapter.NestedViewHolder> {

    private final List<CategoryModel> categoryList = new ArrayList<>();

    public void updateList(List<CategoryModel> newList) {
        categoryList.clear();
        categoryList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nested_category_product, parent, false);

        return new NestedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NestedViewHolder holder, int position) {
        holder.bind(categoryList.get(position));
    }

    @Override
    public void onViewRecycled(@NonNull NestedViewHolder holder) {
        super.onViewRecycled(holder);
        holder.detachPager();
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class NestedViewHolder extends RecyclerView.ViewHolder {

        TextView categoryTitle;
        RecyclerView productRecyclerView;

        ProductAdapter productAdapter;
        CategoryProductPager pager;

        NestedViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryTitle = itemView.findViewById(R.id.categoryTitle);
            productRecyclerView = itemView.findViewById(R.id.productRecyclerView);

            productAdapter = new ProductAdapter();

            productRecyclerView.setLayoutManager(
                    new LinearLayoutManager(
                            itemView.getContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                    )
            );

            productRecyclerView.setAdapter(productAdapter);
            productRecyclerView.setHasFixedSize(true);
            productRecyclerView.setItemAnimator(null);
            productRecyclerView.setNestedScrollingEnabled(false);
        }

        void bind(CategoryModel category) {

            detachPager();

            categoryTitle.setText(category.getCategory_title());

            productAdapter.updateList(new ArrayList<>());

            pager = new CategoryProductPager(
                    category.getCategory_id(),
                    new CategoryProductPager.ProductPageCallback() {

                        @Override
                        public void onProductsChanged(List<ProductModel> products) {

                            itemView.setVisibility(View.VISIBLE);

                            ViewGroup.LayoutParams params = itemView.getLayoutParams();
                            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                            itemView.setLayoutParams(params);

                            productAdapter.updateList(products);
                        }

                        @Override
                        public void onEmpty() {

                            itemView.setVisibility(View.GONE);

                            ViewGroup.LayoutParams params = itemView.getLayoutParams();
                            params.width = 0;
                            params.height = 0;
                            itemView.setLayoutParams(params);
                        }
                    }
            );

            pager.startListening();
        }

        void detachPager() {
            if (pager != null) {
                pager.detach();
                pager = null;
            }
        }
    }
}