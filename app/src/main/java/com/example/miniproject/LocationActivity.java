package com.example.miniproject;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LocationActivity extends AppCompatActivity {

    MaterialAutoCompleteTextView location;
    ImageView dropDownBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.backlocation), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String[] locations = {"Delhi", "Mumbai", "Pune", "Bangalore", "Rajkot", "Rajasthan", "Mahesana"};

        location = findViewById(R.id.locationDropdown);
        dropDownBtn = findViewById(R.id.downbtn);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                locations
        );

        location.setAdapter(adapter);

        dropDownBtn.setOnClickListener(v -> location.showDropDown());

    }
}




