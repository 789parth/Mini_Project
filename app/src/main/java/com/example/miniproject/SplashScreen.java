package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.backlocation), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // If a user session exists locally, verify it with Firebase (e.g., check if account was deleted)
                currentUser.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User is valid and account exists in Firebase Auth
                        startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                    } else {
                        // If reload fails, check if it's because the user is no longer valid (deleted/disabled)
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            mAuth.signOut(); // Clear the local session
                            startActivity(new Intent(SplashScreen.this, StartActivity.class));
                        } else {
                            // Likely a network error, we proceed with the cached session for offline support
                            startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                        }
                    }
                    finish();
                });
            } else {
                // No user found locally, redirect to onboarding
                startActivity(new Intent(SplashScreen.this, StartActivity.class));
                finish();
            }
        }, 1500);
    }
}
