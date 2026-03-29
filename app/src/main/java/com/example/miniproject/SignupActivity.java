package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.ClearCredentialStateRequest;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference database;
    private EditText username, email, pass, cpass;
    private CredentialManager credentialManager;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private static final String WEB_CLIENT_ID = "451391621088-2qnec80ouit2c37s1j544278vot4jt07.apps.googleusercontent.com";

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
        credentialManager = CredentialManager.create(this);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        cpass = findViewById(R.id.cpass);
        Button createAccount = findViewById(R.id.createAccount);
        TextView already = findViewById(R.id.alreadyAccount);
        Button googleBtn = findViewById(R.id.googleBtn);

        googleBtn.setOnClickListener(v -> {
            credentialManager.clearCredentialStateAsync(new ClearCredentialStateRequest(), null, executor, new androidx.credentials.CredentialManagerCallback<Void, androidx.credentials.exceptions.ClearCredentialException>() {
                @Override
                public void onResult(Void result) {
                    signInWithGoogle();
                }

                @Override
                public void onError(@NonNull androidx.credentials.exceptions.ClearCredentialException e) {
                    signInWithGoogle();
                }
            });
        });

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
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    String userId = user.getUid();
                                    database.child("users").child(userId).child("username").setValue(usernameText);
                                    database.child("users").child(userId).child("email").setValue(emailText);
                                    database.child("users").child(userId).child("password").setValue(passText.hashCode());
                                    
                                    Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Toast.makeText(this, "Account creation failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        already.setOnClickListener( v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void signInWithGoogle() {
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        credentialManager.getCredentialAsync(this, request, null, executor, new androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
            @Override
            public void onResult(GetCredentialResponse result) {
                Credential credential = result.getCredential();
                if (credential instanceof CustomCredential && credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
                    try {
                        GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(((CustomCredential) credential).getData());
                        firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());
                    } catch (Exception e) {
                        Log.e("GoogleSignIn", "Error parsing Google ID token: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(@NonNull GetCredentialException e) {
                Log.e("GoogleSignIn", "Error: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(SignupActivity.this, "Google Sign In Failed", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            DatabaseReference userRef = database.child("users").child(user.getUid());
                            userRef.child("email").setValue(user.getEmail());
                            userRef.child("username").setValue(user.getDisplayName());

                            Intent intent = new Intent(SignupActivity.this, LocationActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        runOnUiThread(() -> Toast.makeText(SignupActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show());
                    }
                });
    }
}
