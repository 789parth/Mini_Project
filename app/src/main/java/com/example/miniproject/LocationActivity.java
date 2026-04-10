package com.example.miniproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.miniproject.ManagerClass.SessionManager;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LocationActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    MaterialAutoCompleteTextView location;
    ImageView dropDownBtn;
    Button continueBtn;

    private final ActivityResultLauncher<Intent> selectLocationLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // Assumes SelectLocationActivity returns the location in an extra called "selectedLocation"
                    if (data != null && data.hasExtra("selectedLocation")) {
                        String selectedLocation = data.getStringExtra("selectedLocation");
                        location.setText(selectedLocation, false);
                    }
                }
            });

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

        sessionManager = new SessionManager(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        continueBtn = findViewById(R.id.continueBtn2);

        location = findViewById(R.id.locationDropdown);
        dropDownBtn = findViewById(R.id.downbtn);

        dropDownBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LocationActivity.this, SelectLocationActivity.class);
            selectLocationLauncher.launch(intent);
        });

        location.setOnClickListener(v -> {
            Intent intent = new Intent(LocationActivity.this, SelectLocationActivity.class);
            selectLocationLauncher.launch(intent);
        });


        continueBtn.setOnClickListener(v -> {
            if (location.getText() == null || location.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show();
                return;
            }
            String user = Objects.requireNonNull(auth.getCurrentUser()).getUid();
            database.child("users").child(user).child("location").setValue(location.getText().toString());
            Toast.makeText(this, "Location set successfully", Toast.LENGTH_SHORT).show();

            // Save location in session=====================
            sessionManager.saveUser(sessionManager.getUid() ,sessionManager.getUsername(), location.getText().toString(), sessionManager.getEmail());

            Log.d("Location", "Location set successfully");
            Intent intent = new Intent(LocationActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
