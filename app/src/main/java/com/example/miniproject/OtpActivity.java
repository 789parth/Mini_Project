package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    Button conform;
    EditText otp;
    String getOtpBackend;


    @SuppressWarnings("deprecation")
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


        TextView displayNumber = findViewById(R.id.textView12);
        final ProgressBar progressBarOtp = findViewById(R.id.progress_circular);

        displayNumber.setText(String.format("OTP send to +91-%s",getIntent().getStringExtra("phoneNumber")));
        getOtpBackend = getIntent().getStringExtra("backendOtp");



        conform.setOnClickListener(v ->{
            if(!otp.getText().toString().trim().isEmpty() && otp.getText().toString().trim().length() == 6) {

                String otpEntered = otp.getText().toString();

                if(getOtpBackend != null){
                    progressBarOtp.setVisibility(View.VISIBLE);
                    conform.setVisibility(View.GONE);

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(
                            getOtpBackend,otpEntered
                    );

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBarOtp.setVisibility(View.GONE);
                                    conform.setVisibility(View.VISIBLE);

                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(OtpActivity.this, LocationActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        Toast.makeText(OtpActivity.this, "Enter The Correct OTP.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else {
                    Toast.makeText(OtpActivity.this, "Please Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(OtpActivity.this, "OTP Verified", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(OtpActivity.this, "Please Enter All Numbers.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.resendOtp).setOnClickListener(v -> {

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91" + getIntent().getStringExtra("phoneNumber"),
                    60,
                    TimeUnit.SECONDS,
                    OtpActivity.this,

                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {

                            Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCodeSent(@NonNull String resendBackendOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {

                            getOtpBackend = resendBackendOtp;
                            Toast.makeText(OtpActivity.this, "OTP Resent Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });
    }
}