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
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.example.miniproject.ManagerClass.SessionManager;
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

public class LoginActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private FirebaseAuth auth;
    private DatabaseReference database;
    private EditText emailInput, passwordInput;
    private CredentialManager credentialManager;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private static final String WEB_CLIENT_ID = "451391621088-2qnec80ouit2c37s1j544278vot4jt07.apps.googleusercontent.com";

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

        sessionManager = new SessionManager(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();
        credentialManager = CredentialManager.create(this);

        Button loginBtn = findViewById(R.id.createAccount);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        TextView noAccount = findViewById(R.id.noAccount);
        TextView fpass = findViewById(R.id.forgot_pass);
        Button googleBtn = findViewById(R.id.googleBtn);

        googleBtn.setOnClickListener(v -> {
            // Force account picker by clearing state first
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

        loginBtn.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "All Fields Are Required", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    FirebaseUser user = auth.getCurrentUser();

                    if (user != null) {

                        DatabaseReference userRef = database.child("users").child(user.getUid());

                        userRef.get().addOnSuccessListener(snapshot -> {

                            // SAVE in session====================================
                            String sessionName = snapshot.child("username").getValue(String.class);
                            String sessionEmail = snapshot.child("email").getValue(String.class);

                            sessionManager.saveUser(user.getUid() ,sessionName, "", sessionEmail);
                            //=====================================================

                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivity.this, LocationActivity.class));
                            finish();
                        });
                    }
                } else {
                    Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        fpass.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
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
                try {
                    GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                    firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());
                } catch (Exception e) {
                    Log.e("GoogleSignIn", "Error parsing Google ID token: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error processing Google account", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onError(@NonNull GetCredentialException e) {
                Log.e("GoogleSignIn", "Error: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Google Sign In Failed", Toast.LENGTH_SHORT).show());
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

                            sessionManager.saveUser(user.getUid() ,user.getDisplayName(),"", user.getEmail());
                            Intent intent = new Intent(LoginActivity.this, LocationActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
