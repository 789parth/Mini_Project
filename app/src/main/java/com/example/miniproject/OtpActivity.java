package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {

    private static final String ACCOUNT_SID = BuildConfig.TWILIO_ACCOUNT_SID;
    private static final String AUTH_TOKEN = BuildConfig.TWILIO_AUTH_TOKEN;
    private static final String VERIFY_SERVICE_SID = BuildConfig.VERIFY_SERVICE_SID;

    private EditText etMobileNumber, etOtp;
    private Button btnSendOtp, btnVerifyOtp;
    private LinearLayout llOtpSection;
    private ProgressBar progressBar;
    private ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        
        View root = findViewById(R.id.otp);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Initialize UI components
        etMobileNumber = findViewById(R.id.etMobileNumber);
        etOtp = findViewById(R.id.etOtp);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        llOtpSection = findViewById(R.id.llOtpSection);
        TextView resendOtp = findViewById(R.id.resendOtp);
        TextView tvOtpMessage = findViewById(R.id.tvOtpMessage);
        progressBar = findViewById(R.id.progress_circular);

        // Initialize Retrofit API Interface
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        // Send OTP button click listener
        btnSendOtp.setOnClickListener(v -> {
            String mobileNumber = etMobileNumber.getText().toString().trim();
            if (isValidMobile(mobileNumber)) {
                sendOtpRequest(mobileNumber, tvOtpMessage);
            } else {
                Toast.makeText(OtpActivity.this, "Invalid mobile number. Use format +91XXXXXXXXXX", Toast.LENGTH_SHORT).show();
            }
        });

        // Verify OTP button click listener
        btnVerifyOtp.setOnClickListener(v -> {
            String otpCode = etOtp.getText().toString().trim();
            String mobileNumber = etMobileNumber.getText().toString().trim();
            if (otpCode.length() == 6) {
                verifyOtpRequest(mobileNumber, otpCode);
            } else {
                Toast.makeText(OtpActivity.this, "Please enter 6-digit OTP", Toast.LENGTH_SHORT).show();
            }
        });

        // Resend OTP click listener
        resendOtp.setOnClickListener(v -> {
            String mobileNumber = etMobileNumber.getText().toString().trim();
            if (!mobileNumber.isEmpty()) {
                sendOtpRequest(mobileNumber, tvOtpMessage);
            }
        });
    }

    private boolean isValidMobile(String mobile) {
        return mobile.startsWith("+") && mobile.length() >= 12;
    }

    private void sendOtpRequest(String mobileNumber, TextView tvOtpMessage) {
        showLoading(true);
        String authHeader = "Basic " + Base64.encodeToString((ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP);

        apiInterface.sendOtp(authHeader, VERIFY_SERVICE_SID, mobileNumber, "sms").enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(OtpActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                    llOtpSection.setVisibility(View.VISIBLE);
                    btnSendOtp.setVisibility(View.GONE);
                    tvOtpMessage.setText(getString(R.string.otp_sent_to, mobileNumber));
                } else {
                    Toast.makeText(OtpActivity.this, "Failed to send OTP: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(OtpActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyOtpRequest(String mobileNumber, String otpCode) {
        showLoading(true);
        String authHeader = "Basic " + Base64.encodeToString((ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP);

        apiInterface.verifyOtp(authHeader, VERIFY_SERVICE_SID, mobileNumber, otpCode).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    String status = (String) response.body().get("status");
                    if ("approved".equalsIgnoreCase(status)) {
                        Toast.makeText(OtpActivity.this, "Verification Successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(OtpActivity.this, LocationActivity.class);
                        startActivity(intent);
                        finish();
                    } else if ("pending".equalsIgnoreCase(status)) {
                        Toast.makeText(OtpActivity.this, "OTP is pending verification", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OtpActivity.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OtpActivity.this, "Verification failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(OtpActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSendOtp.setEnabled(false);
            btnVerifyOtp.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSendOtp.setEnabled(true);
            btnVerifyOtp.setEnabled(true);
        }
    }
}
