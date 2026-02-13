package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LocationActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    MaterialAutoCompleteTextView location;
    ImageView dropDownBtn;
    Button continueBtn;


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

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        continueBtn = findViewById(R.id.continueBtn2);

        String[] locations = {"Nana Bazaar","Mota Bazaar","BVM","Anand City","Shaan Cinema"};

        location = findViewById(R.id.locationDropdown);
        dropDownBtn = findViewById(R.id.downbtn);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                locations
        );

        location.setAdapter(adapter);

        dropDownBtn.setOnClickListener(v -> location.showDropDown());

        continueBtn.setOnClickListener(v -> {
            if (location.getText() == null || location.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
                return;
            }
            String user = Objects.requireNonNull(auth.getCurrentUser()).getUid();
            database.child("users").child(user).child("location").setValue(location.getText().toString());
            Toast.makeText(this, "Location set successfully", Toast.LENGTH_SHORT).show();
            Log.d("Location", "Location set successfully");
            Intent intent = new Intent(LocationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
