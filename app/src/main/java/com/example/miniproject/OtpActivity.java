package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class OtpActivity extends AppCompatActivity {
    Button conform;
    EditText otp;
    String OtpBackend;
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.otp), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        conform = findViewById(R.id.continueBtn);
        otp = findViewById(R.id.editTextNumberPassword);

        TextView displayEmail = findViewById(R.id.textView12);

        displayEmail.setText(String.format("OTP send to %s",getIntent().getStringExtra("emailId")));
        OtpBackend = String.valueOf(100000 + new Random().nextInt(900000));

        msg = "Your OTP for Reset Password is : "+OtpBackend;

        conform.setOnClickListener(v -> {
            if (otp.getText().toString().trim().isEmpty()) {
                Toast.makeText(OtpActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                return;
            }
            if (otp.getText().toString().trim().equals(OtpBackend)) {
                Toast.makeText(OtpActivity.this, "OTP verified successfully", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
//                intent.putExtra("emailId", getIntent().getStringExtra("emailId"));
//                startActivity(intent);
//                finish();
            }
            else {
                Toast.makeText(OtpActivity.this, "OTP verification failed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OtpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}