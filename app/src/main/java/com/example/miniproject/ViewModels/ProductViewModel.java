package com.example.miniproject.ViewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.miniproject.domain.products;
import com.google.firebase.database.*;

import java.util.ArrayList;

public class ProductViewModel extends ViewModel {

    public ArrayList<products> list = new ArrayList<>();

    public String lastKey = null;
    public boolean isLoaded = false;
    public boolean isLoading = false;

    int pageSize = 10;

    // 🔥 INIT (first load)
    public void init(Runnable onDone){

        if (isLoaded) {
            if (onDone != null) onDone.run();
            return;
        }

        isLoading = true;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products");

        Query query = ref.orderByKey().limitToFirst(pageSize);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    products model = data.getValue(products.class);

                    if (model != null && model.isTrending_item()) {
                        list.add(model);
                    }

                    lastKey = data.getKey(); // 🔥 important
                }

                isLoaded = true;
                isLoading = false;

                if (onDone != null) onDone.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isLoading = false;
            }
        });
    }

    // 🔥 LOAD MORE (pagination)
    public void loadMore(Runnable onDone){

        if (isLoading) return;

        isLoading = true;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products");

        Query query = ref.orderByKey()
                .startAfter(lastKey)
                .limitToFirst(pageSize);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    isLoading = false;
                    return;
                }

                for (DataSnapshot data : snapshot.getChildren()) {

                    products model = data.getValue(products.class);

                    if (model != null && model.isTrending_item()) {
                        list.add(model);
                    }

                    lastKey = data.getKey();
                }

                isLoading = false;

                if (onDone != null) onDone.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isLoading = false;
            }
        });
    }
}