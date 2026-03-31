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

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
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
                // Force a token refresh to verify if the account still exists on the server
                currentUser.getIdToken(true).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Token refreshed successfully, user is still valid
                        startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                    } else {
                        // Refresh failed. Check if it's a network error or an auth error
                        if (task.getException() instanceof FirebaseNetworkException) {
                            // Network error: allow access via cached session for offline support
                            startActivity(new Intent(SplashScreen.this, HomeActivity.class));
                        } else {
                            // Auth error (e.g., account deleted or disabled): sign out and go to login
                            mAuth.signOut();
                            startActivity(new Intent(SplashScreen.this, StartActivity.class));
                        }
                    }
                    finish();
                });
            } else {
                // No cached user session found
                startActivity(new Intent(SplashScreen.this, StartActivity.class));
                finish();
            }
        }, 1500);
    }
}
