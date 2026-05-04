package com.example.miniproject.Category;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategoryViewModel extends ViewModel {

    private final MutableLiveData<List<CategoryModel>> categories =
            new MutableLiveData<>(new ArrayList<>());

    private final List<CategoryModel> categoryList = new ArrayList<>();

    private DatabaseReference categoryRef;
    private ChildEventListener childEventListener;

    private boolean listenerAttached = false;

    public LiveData<List<CategoryModel>> getCategories() {
        if (!listenerAttached) {
            loadCategories();
        }
        return categories;
    }

    private void loadCategories() {
        listenerAttached = true;

        categoryRef = FirebaseDatabase.getInstance()
                .getReference("category");

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                CategoryModel category = snapshot.getValue(CategoryModel.class);

                if (category != null && findIndexById(category.getCategory_id()) == -1) {
                    categoryList.add(category);
                    publishList();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                CategoryModel updatedCategory = snapshot.getValue(CategoryModel.class);

                if (updatedCategory == null) return;

                int index = findIndexById(updatedCategory.getCategory_id());

                if (index >= 0) {
                    categoryList.set(index, updatedCategory);
                    publishList();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                CategoryModel removedCategory = snapshot.getValue(CategoryModel.class);

                if (removedCategory == null) return;

                int index = findIndexById(removedCategory.getCategory_id());

                if (index >= 0) {
                    categoryList.remove(index);
                    publishList();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        };

        categoryRef.addChildEventListener(childEventListener);
    }

    private void publishList() {
        categories.setValue(new ArrayList<>(categoryList));
    }

    private int findIndexById(String categoryId) {
        if (categoryId == null) return -1;

        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryId.equals(categoryList.get(i).getCategory_id())) {
                return i;
            }
        }

        return -1;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        if (categoryRef != null && childEventListener != null) {
            categoryRef.removeEventListener(childEventListener);
        }
    }
}
