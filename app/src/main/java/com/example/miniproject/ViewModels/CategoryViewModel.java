package com.example.miniproject.ViewModels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.miniproject.domain.categorys;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryViewModel extends ViewModel {

    public ArrayList<categorys> list = new ArrayList<>();
    public boolean isLoaded = false;

    public void init(Runnable onDone){

        if (isLoaded) {
            if (onDone != null) onDone.run();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("category");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for (DataSnapshot data : snapshot.getChildren()){
                    categorys model = data.getValue(categorys.class);
                    if(model != null) {
                        list.add(model);
                    }
                }

                isLoaded = true;

                // 🔥 CALL ONLY ONCE AFTER LOOP
                if (onDone != null) onDone.run();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (onDone != null) onDone.run(); // optional
            }
        });
    }
}
