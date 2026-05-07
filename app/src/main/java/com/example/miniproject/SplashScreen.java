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

import com.example.miniproject.ManagerClass.SessionManager;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;

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
        sessionManager = new SessionManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // If user is logged in, check progress
                navigateToNextStep();
            } else {
                // No user session, go to StartActivity or Login
                startActivity(new Intent(SplashScreen.this, StartActivity.class));
                finish();
            }
        }, 1500);
    }

    private void navigateToNextStep() {
        Intent intent;
        if (!sessionManager.isLoginDone()) {
            intent = new Intent(SplashScreen.this, LoginActivity.class);
        } else if (!sessionManager.isOtpVerified()) {
            intent = new Intent(SplashScreen.this, OtpActivity.class);
        } else if (!sessionManager.isLocationSelected()) {
            intent = new Intent(SplashScreen.this, LocationActivity.class);
        } else {
            intent = new Intent(SplashScreen.this, HomeActivity.class);
        }
        
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
