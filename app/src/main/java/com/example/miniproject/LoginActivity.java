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

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    Button loginBtn;
    EditText emailInput, passwordInput;
    TextView noAccount,fpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.backlocation), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        loginBtn = findViewById(R.id.createAccount);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        noAccount = findViewById(R.id.noAccount);
        fpass = findViewById(R.id.forgot_pass);


        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "All Fields Are Required", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, LocationActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, task1 -> {
                                if (task1.isSuccessful()) {
                                    String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                                    database.child("users").child(userId).child("email").setValue(email);
                                    Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                    Log.d("Account", "createUserWithEmail:success");
                                    String user = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                                    database.child("users").child(user).child("email").setValue(email);
                                    database.child("users").child(user).child("password").setValue(password);
                                    Intent intent = new Intent(this, LocationActivity.class);
                                    startActivity(intent);
                                    finish();
                        } else {
                            Toast.makeText(LoginActivity.this, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });

        fpass.setOnClickListener(v -> {
            if (emailInput.getText().toString().trim().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.sendPasswordResetEmail(emailInput.getText().toString().trim()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Error sending password reset email", Toast.LENGTH_SHORT).show();
                }
            });
        });

        noAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
