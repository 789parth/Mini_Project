package com.example.miniproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.miniproject.ManagerClass.SessionManager;
import com.example.miniproject.R;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;

public class ProfileFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SessionManager sessionManager = new SessionManager(requireContext());

        LinearLayout layoutMyAccount = view.findViewById(R.id.layoutMyAccount);
        LinearLayout layoutAccountDetails = view.findViewById(R.id.layoutAccountDetails);
        ImageView ivAccountExpand = view.findViewById(R.id.ivAccountExpand);
        LinearLayout layoutAccountView = view.findViewById(R.id.layoutAccountView);
        LinearLayout layoutAccountEdit = view.findViewById(R.id.layoutAccountEdit);
        ImageView ivEditAccount = view.findViewById(R.id.ivEditAccount);
        EditText etAccountName = view.findViewById(R.id.etAccountName);
        EditText etAccountEmail = view.findViewById(R.id.etAccountEmail);
        EditText etAccountPhone = view.findViewById(R.id.etAccountPhone);
        Button btnSaveAccount = view.findViewById(R.id.btnSaveAccount);
        TextView tvAccountName = view.findViewById(R.id.tvAccountName);
        TextView tvAccountEmail = view.findViewById(R.id.tvAccountEmail);
        TextView tvAccountPhone = view.findViewById(R.id.tvAccountPhone);

        // Profile header TextViews
        TextView tvProfileName = view.findViewById(R.id.tvProfileName);
        TextView tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        TextView tvProfilePhone = view.findViewById(R.id.tvProfilePhone);

        LinearLayout layoutHelp = view.findViewById(R.id.layoutHelp);
        LinearLayout layoutHelpDetails = view.findViewById(R.id.layoutHelpDetails);
        ImageView ivHelpExpand = view.findViewById(R.id.ivHelpExpand);
        LinearLayout layoutCustomerSupport = view.findViewById(R.id.layoutCustomerSupport);
        ImageView ivCustomerSupportExpand = view.findViewById(R.id.ivCustomerSupportExpand);
        LinearLayout layoutCustomerSupportDetails = view.findViewById(R.id.layoutCustomerSupportDetails);
        LinearLayout layoutFaqsBtn = view.findViewById(R.id.layoutFaqsBtn);
        ImageView ivFaqsExpand = view.findViewById(R.id.ivFaqsExpand);
        LinearLayout layoutFaqs = view.findViewById(R.id.layoutFaqs);

        boolean[] isAccountExpanded = {false};
        boolean[] isHelpExpanded = {false};
        boolean[] isCustomerSupportExpanded = {false};
        boolean[] isFaqsExpanded = {false};

        // My Account expand/collapse
        layoutMyAccount.setOnClickListener(v -> {
            isAccountExpanded[0] = !isAccountExpanded[0];
            layoutAccountDetails.setVisibility(isAccountExpanded[0] ? View.VISIBLE : View.GONE);
            ivAccountExpand.setImageResource(isAccountExpanded[0] ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float);
        });

        // Switch to edit mode
        ivEditAccount.setOnClickListener(v -> {
            layoutAccountView.setVisibility(View.GONE);
            layoutAccountEdit.setVisibility(View.VISIBLE);
            etAccountName.setText(tvAccountName.getText());
            etAccountEmail.setText(tvAccountEmail.getText());
            etAccountPhone.setText(tvAccountPhone.getText());
        });

        // Save edited details
        btnSaveAccount.setOnClickListener(v -> {
            String newName = etAccountName.getText().toString().trim();
            String newEmail = etAccountEmail.getText().toString().trim();
            String newPhone = etAccountPhone.getText().toString().trim();

            // Validate inputs
            if (newName.isEmpty() || newEmail.isEmpty() || newPhone.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get current user ID with null check
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(requireContext(), "User not logged in. Please login first.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            // Update user data in Firebase
            userRef.child("username").setValue(newName);
            userRef.child("email").setValue(newEmail);
            userRef.child("phone").setValue(newPhone)
                    .addOnSuccessListener(aVoid -> {
                        // Update both header and account details UI
                        tvProfileName.setText(newName);
                        tvProfileEmail.setText(newEmail);
                        tvProfilePhone.setText(newPhone);
                        tvAccountName.setText(newName);
                        tvAccountEmail.setText(newEmail);
                        tvAccountPhone.setText(newPhone);
                        layoutAccountEdit.setVisibility(View.GONE);
                        layoutAccountView.setVisibility(View.VISIBLE);
                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to update profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        // Help expand/collapse
        layoutHelp.setOnClickListener(v -> {
            isHelpExpanded[0] = !isHelpExpanded[0];
            layoutHelpDetails.setVisibility(isHelpExpanded[0] ? View.VISIBLE : View.GONE);
            ivHelpExpand.setImageResource(isHelpExpanded[0] ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float);
        });

        // Customer Support expand/collapse
        layoutCustomerSupport.setOnClickListener(v -> {
            isCustomerSupportExpanded[0] = !isCustomerSupportExpanded[0];
            layoutCustomerSupportDetails.setVisibility(isCustomerSupportExpanded[0] ? View.VISIBLE : View.GONE);
            ivCustomerSupportExpand.setImageResource(isCustomerSupportExpanded[0] ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float);
            // Close FAQs when opening Customer Support
            if (isCustomerSupportExpanded[0]) {
                isFaqsExpanded[0] = false;
                layoutFaqs.setVisibility(View.GONE);
                ivFaqsExpand.setImageResource(android.R.drawable.arrow_down_float);
            }
        });

        // FAQs expand/collapse
        layoutFaqsBtn.setOnClickListener(v -> {
            isFaqsExpanded[0] = !isFaqsExpanded[0];
            layoutFaqs.setVisibility(isFaqsExpanded[0] ? View.VISIBLE : View.GONE);
            ivFaqsExpand.setImageResource(isFaqsExpanded[0] ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float);
            // Close Customer Support when opening FAQs
            if (isFaqsExpanded[0]) {
                isCustomerSupportExpanded[0] = false;
                layoutCustomerSupportDetails.setVisibility(View.GONE);
                ivCustomerSupportExpand.setImageResource(android.R.drawable.arrow_down_float);
            }
        });

        return view;
    }

    // Load user profile data from Firebase
    private void loadUserProfile(TextView tvProfileName, TextView tvProfileEmail, TextView tvProfilePhone,
                                 TextView tvAccountName, TextView tvAccountEmail, TextView tvAccountPhone) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);

                    // Set default values if null
                    if (name == null) name = "User";
                    if (email == null) email = "Not set";
                    if (phone == null) phone = "Not set";

                    // Update all TextViews
                    tvProfileName.setText(name);
                    tvProfileEmail.setText(email);
                    tvProfilePhone.setText(phone);
                    tvAccountName.setText(name);
                    tvAccountEmail.setText(email);
                    tvAccountPhone.setText(phone);
                } else {
                    // User data not found
                    tvProfileName.setText("User");
                    tvProfileEmail.setText("Not set");
                    tvProfilePhone.setText("Not set");
                    tvAccountName.setText("User");
                    tvAccountEmail.setText("Not set");
                    tvAccountPhone.setText("Not set");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }
}