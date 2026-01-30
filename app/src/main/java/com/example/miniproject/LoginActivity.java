package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    Button getOTP;
    EditText phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login); // ← YOUR LOGIN XML

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.backlocation), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        getOTP = findViewById(R.id.getotp);
        phoneNumber = findViewById(R.id.editTextPhone);

        ProgressBar progressBarLogin = findViewById(R.id.progressBar);


        getOTP.setOnClickListener(v -> {

            if(!phoneNumber.getText().toString().trim().isEmpty()) {
                if (phoneNumber.getText().toString().trim().length() == 10) {

                    progressBarLogin.setVisibility(View.VISIBLE);
                    getOTP.setVisibility(View.GONE);

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91" + phoneNumber.getText().toString(),
                            60,
                            TimeUnit.SECONDS,
                            LoginActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    progressBarLogin.setVisibility(View.GONE);
                                    getOTP.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    progressBarLogin.setVisibility(View.GONE);
                                    getOTP.setVisibility(View.VISIBLE);
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String backendOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    progressBarLogin.setVisibility(View.GONE);
                                    getOTP.setVisibility(View.VISIBLE);

                                    Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
                                    intent.putExtra("phoneNumber",phoneNumber.getText().toString());
                                    intent.putExtra("backendOtp",backendOtp);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                    );
                }
                else {
                    Toast.makeText(LoginActivity.this, "Please Enter Valid Number", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(LoginActivity.this, "Please Enter Number", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
