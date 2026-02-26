package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    EditText username, email, pass, cpass;
    Button createAccount;
    TextView already;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        cpass = findViewById(R.id.cpass);
        createAccount = findViewById(R.id.createAccount);
        createAccount.setOnClickListener(v -> {
            String usernameText = username.getText().toString().trim();
            String emailText = email.getText().toString().trim();
            String passText = pass.getText().toString();
            String cpassText = cpass.getText().toString();

            if (usernameText.isEmpty() || emailText.isEmpty() || passText.isEmpty() || cpassText.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
            else if (!passText.equals(cpassText)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
            else if (passText.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
            }
            else if (!passText.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$")) {
                Toast.makeText(this, "Password must be strong: use uppercase, lowercase, numbers, and special characters", Toast.LENGTH_LONG).show();
            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            }
            else if (usernameText.length() < 3) {
                Toast.makeText(this, "Username must be at least 3 characters long", Toast.LENGTH_SHORT).show();
            }
            else if (!usernameText.matches("[a-zA-Z0-9]+")) {
                Toast.makeText(this, "Username can only contain letters and numbers", Toast.LENGTH_SHORT).show();
            }
            else {
                auth.createUserWithEmailAndPassword(emailText, passText)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                                database.child("users").child(userId).child("username").setValue(usernameText);
                                database.child("users").child(userId).child("email").setValue(emailText);
                                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                Log.d("Account", "createUserWithEmail:success");
                                String user = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                                database.child("users").child(user).child("username").setValue(usernameText);
                                database.child("users").child(user).child("email").setValue(emailText);
                                database.child("users").child(user).child("password").setValue(passText.hashCode());
                                Intent intent = new Intent(this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(this, "Account creation failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        already = findViewById(R.id.alreadyAccount);
        already.setOnClickListener( v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}