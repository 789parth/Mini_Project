package com.example.miniproject.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.miniproject.ManagerClass.SessionManager;
import com.example.miniproject.R;
import com.example.miniproject.SelectLocationActivity;
import com.example.miniproject.StartActivity;
import com.example.miniproject.adapter.BuyAgainAdapter;
import com.example.miniproject.adapter.FaqAdapter;
import com.example.miniproject.domain.FaqModel;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ProfileFragment extends Fragment {

    // Views
    private ImageView ivProfilePhoto;
    private ImageButton btnChangePhoto;
    private MaterialButton btnEditUsername, btnChangeLocation;
    private TextView tvProfileName, tvProfileEmail;
    private TextView tvAccountEmail, tvAccountPhone, tvAccountLocation;
    private TextView tvNoOrders, tvNoFaqs;
    private AppCompatButton btnLogout;
    private RecyclerView rvPastOrders, rvFaqs;

    // Firebase
    private FirebaseAuth auth;
    private DatabaseReference database;
    private SessionManager sessionManager;

    // Orders
    private final ArrayList<String> orderFoodNames     = new ArrayList<>();
    private final ArrayList<String> orderFoodPrices    = new ArrayList<>();
    private final ArrayList<String> orderFoodImageUrls = new ArrayList<>();
    private final ArrayList<String> orderProductIds    = new ArrayList<>();
    private BuyAgainAdapter orderAdapter;

    // FAQs
    private final ArrayList<FaqModel> faqList = new ArrayList<>();
    private FaqAdapter faqAdapter;

    // ── Launchers ────────────────────────────────────────────────────────────

    /** Gallery picker — picks any image */
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) savePhotoLocally(uri);
            });

    /** Location map picker */
    private final ActivityResultLauncher<Intent> locationLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    String loc = result.getData().getStringExtra("selectedLocation");
                    if (loc != null && !loc.isEmpty()) saveLocation(loc);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth           = FirebaseAuth.getInstance();
        database       = FirebaseDatabase.getInstance().getReference();
        sessionManager = new SessionManager(requireContext());

        // Bind views
        ivProfilePhoto    = view.findViewById(R.id.ivProfilePhoto);
        btnChangePhoto    = view.findViewById(R.id.btnChangePhoto);
        tvProfileName     = view.findViewById(R.id.tvProfileName);
        tvProfileEmail    = view.findViewById(R.id.tvProfileEmail);
        tvAccountEmail    = view.findViewById(R.id.tvAccountEmail);
        tvAccountPhone    = view.findViewById(R.id.tvAccountPhone);
        tvAccountLocation = view.findViewById(R.id.tvAccountLocation);
        tvNoOrders        = view.findViewById(R.id.tvNoOrders);
        tvNoFaqs          = view.findViewById(R.id.tvNoFaqs);
        btnEditUsername   = view.findViewById(R.id.btnEditUsername);
        btnChangeLocation = view.findViewById(R.id.btnChangeLocation);
        btnLogout         = view.findViewById(R.id.btnLogout);
        rvPastOrders      = view.findViewById(R.id.rvPastOrders);
        rvFaqs            = view.findViewById(R.id.rvFaqs);

        // ── Listeners ────────────────────────────────────────────────────────
        btnChangePhoto.setOnClickListener(v -> showPhotoOptions());
        btnEditUsername.setOnClickListener(v -> showEditUsernameDialog());
        btnChangeLocation.setOnClickListener(v ->
                locationLauncher.launch(new Intent(getActivity(), SelectLocationActivity.class)));

        // ── RecyclerViews ────────────────────────────────────────────────────
        orderAdapter = new BuyAgainAdapter(
                orderFoodNames, orderFoodPrices, orderFoodImageUrls, orderProductIds);
        orderAdapter.setOnAddToCartListener((position, productId, name, priceStr, imageUrl) ->
                addToCart(productId, name, priceStr, imageUrl));
        rvPastOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPastOrders.setAdapter(orderAdapter);

        faqAdapter = new FaqAdapter(faqList);
        rvFaqs.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvFaqs.setAdapter(faqAdapter);

        // ── Load data ────────────────────────────────────────────────────────
        loadUserData();
        loadPastOrders();
        loadFaqs();

        // ── Logout ───────────────────────────────────────────────────────────
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            sessionManager.logout();
            startActivity(new Intent(getActivity(), StartActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            requireActivity().finish();
        });

        return view;
    }

    // ─── Profile Photo ────────────────────────────────────────────────────────

    private void showPhotoOptions() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Profile Photo")
                .setItems(new String[]{"Choose from Gallery", "Remove Photo"}, (dialog, which) -> {
                    if (which == 0) imagePickerLauncher.launch("image/*");
                    else removeProfilePhoto();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Copies the picked image into the app's internal storage so the URI stays valid
     * forever. Saves the file path to Firebase DB under users/{uid}/profileImage.
     */
    private void savePhotoLocally(Uri imageUri) {
        String uid = auth.getUid();
        if (uid == null) return;

        try {
            // Copy into private files dir: profile_<uid>.jpg
            File destFile = new File(requireContext().getFilesDir(), "profile_" + uid + ".jpg");
            try (InputStream in  = requireContext().getContentResolver().openInputStream(imageUri);
                 OutputStream out = new FileOutputStream(destFile)) {
                if (in == null) throw new Exception("Cannot open image");
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
            }

            String localPath = destFile.getAbsolutePath();

            // Load immediately into UI
            Glide.with(requireContext())
                    .load(destFile)
                    .circleCrop()
                    .placeholder(R.drawable.app_logo)
                    .into(ivProfilePhoto);

            // Persist path in Firebase so it's available on next open
            database.child("users").child(uid).child("profileImage").setValue(localPath)
                    .addOnSuccessListener(u ->
                            Toast.makeText(getContext(), "Photo updated!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Saved locally, DB error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to save photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void removeProfilePhoto() {
        String uid = auth.getUid();
        if (uid == null) return;

        // Delete local file
        File f = new File(requireContext().getFilesDir(), "profile_" + uid + ".jpg");
        if (f.exists()) f.delete();

        // Clear from DB
        database.child("users").child(uid).child("profileImage").removeValue()
                .addOnSuccessListener(u -> {
                    if (!isAdded()) return;
                    ivProfilePhoto.setImageResource(R.drawable.app_logo);
                    Toast.makeText(getContext(), "Photo removed", Toast.LENGTH_SHORT).show();
                });
    }

    /** Load profile photo — first try local file (fast), fall back to DB path */
    private void loadProfilePhoto(String savedPath) {
        if (savedPath == null || savedPath.isEmpty()) return;
        File local = new File(savedPath);
        if (local.exists()) {
            Glide.with(requireContext())
                    .load(local)
                    .circleCrop()
                    .placeholder(R.drawable.app_logo)
                    .error(R.drawable.app_logo)
                    .into(ivProfilePhoto);
        }
        // If file doesn't exist on this device (different device / reinstall), show default
    }

    // ─── Edit Username ────────────────────────────────────────────────────────

    private void showEditUsernameDialog() {
        EditText etName = new EditText(requireContext());
        etName.setText(tvProfileName.getText());
        etName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        etName.setSelectAllOnFocus(true);
        etName.setPadding(40, 20, 40, 20);

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Username")
                .setView(etName)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = etName.getText().toString().trim();
                    if (newName.isEmpty()) {
                        Toast.makeText(getContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (newName.length() < 3) {
                        Toast.makeText(getContext(), "At least 3 characters required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveUsername(newName);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveUsername(String newName) {
        String uid = auth.getUid();
        if (uid == null) return;
        database.child("users").child(uid).child("username").setValue(newName)
                .addOnSuccessListener(u -> {
                    if (!isAdded()) return;
                    tvProfileName.setText(newName);
                    sessionManager.saveUser(uid, newName, sessionManager.getLocation(), sessionManager.getEmail());
                    Toast.makeText(getContext(), "Username updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // ─── Save Location ────────────────────────────────────────────────────────

    private void saveLocation(String newLocation) {
        String uid = auth.getUid();
        if (uid == null) return;
        database.child("users").child(uid).child("location").setValue(newLocation)
                .addOnSuccessListener(u -> {
                    if (!isAdded()) return;
                    tvAccountLocation.setText(newLocation);
                    sessionManager.saveUser(uid, sessionManager.getUsername(), newLocation, sessionManager.getEmail());
                    Toast.makeText(getContext(), "Location updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) Toast.makeText(getContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // ─── Load User Data ───────────────────────────────────────────────────────

    private void loadUserData() {
        String uid = auth.getUid();
        if (uid == null) { applySessionFallback(); return; }

        database.child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isAdded()) return;
                        if (snapshot.exists()) {
                            String name     = snapshot.child("username").getValue(String.class);
                            String email    = snapshot.child("email").getValue(String.class);
                            String mobile   = snapshot.child("mobile").getValue(String.class);
                            String location = snapshot.child("location").getValue(String.class);
                            String photoPath = snapshot.child("profileImage").getValue(String.class);

                            tvProfileName.setText(name != null ? name : "User Name");
                            tvProfileEmail.setText(email != null ? email : "Not set");
                            tvAccountEmail.setText(email != null ? email : "Not set");
                            tvAccountPhone.setText(mobile != null ? mobile : "Not set");
                            tvAccountLocation.setText(location != null ? location : "No delivery location set");

                            // Load profile photo from local file path
                            loadProfilePhoto(photoPath);
                        } else {
                            applySessionFallback();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (isAdded()) applySessionFallback();
                    }
                });
    }

    private void applySessionFallback() {
        tvProfileName.setText(sessionManager.getUsername());
        tvProfileEmail.setText(sessionManager.getEmail());
        tvAccountEmail.setText(sessionManager.getEmail());
        tvAccountPhone.setText("Not set");
        tvAccountLocation.setText(sessionManager.getLocation());
    }

    // ─── Past Orders ─────────────────────────────────────────────────────────

    private void loadPastOrders() {
        String uid = auth.getUid();
        if (uid == null) { showEmptyOrders(); return; }

        database.child("orders").child(uid).limitToLast(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isAdded()) return;
                        orderFoodNames.clear(); orderFoodPrices.clear();
                        orderFoodImageUrls.clear(); orderProductIds.clear();

                        for (DataSnapshot orderSnap : snapshot.getChildren()) {
                            for (DataSnapshot product : orderSnap.child("products").getChildren()) {
                                String  name     = product.child("product_name").getValue(String.class);
                                String  imageUrl = product.child("product_image").getValue(String.class);
                                String  pid      = product.child("product_id").getValue(String.class);
                                Integer price    = product.child("product_price").getValue(Integer.class);
                                Integer qty      = product.child("quantity").getValue(Integer.class);
                                if (name != null) {
                                    orderFoodNames.add(name);
                                    orderFoodImageUrls.add(imageUrl != null ? imageUrl : "");
                                    orderProductIds.add(pid != null ? pid : "");
                                    if (price != null && qty != null && qty > 1)
                                        orderFoodPrices.add("₹" + price + " × " + qty);
                                    else if (price != null) orderFoodPrices.add("₹" + price);
                                    else orderFoodPrices.add("—");
                                }
                            }
                        }
                        orderAdapter.notifyDataSetChanged();
                        if (orderFoodNames.isEmpty()) showEmptyOrders();
                        else { rvPastOrders.setVisibility(View.VISIBLE); tvNoOrders.setVisibility(View.GONE); }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (isAdded()) {
                            Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show();
                            showEmptyOrders();
                        }
                    }
                });
    }

    private void showEmptyOrders() {
        rvPastOrders.setVisibility(View.GONE);
        tvNoOrders.setVisibility(View.VISIBLE);
    }

    // ─── Add to Cart ─────────────────────────────────────────────────────────

    private void addToCart(String productId, String name, String priceStr, String imageUrl) {
        String uid = auth.getUid();
        if (uid == null) { Toast.makeText(getContext(), "Please login first", Toast.LENGTH_SHORT).show(); return; }
        int price = 0;
        try {
            String n = priceStr.replaceAll("[^0-9]", "").trim();
            if (!n.isEmpty()) price = Integer.parseInt(n.substring(0, Math.min(n.length(), 6)));
        } catch (Exception ignored) {}
        String cartKey = (productId != null && !productId.isEmpty()) ? productId : name.replaceAll("[^a-zA-Z0-9]", "_");
        final DatabaseReference cartRef = database.child("carts").child(uid).child(cartKey);
        final int fp = price;
        cartRef.get().addOnSuccessListener(snap -> {
            if (!isAdded()) return;
            if (snap.exists()) {
                Integer q = snap.child("quantity").getValue(Integer.class);
                cartRef.child("quantity").setValue((q != null ? q : 1) + 1)
                        .addOnSuccessListener(u -> Toast.makeText(getContext(), name + " qty updated", Toast.LENGTH_SHORT).show());
            } else {
                HashMap<String, Object> item = new HashMap<>();
                item.put("product_name", name); item.put("product_image", imageUrl);
                item.put("product_price", fp); item.put("quantity", 1);
                cartRef.setValue(item)
                        .addOnSuccessListener(u -> Toast.makeText(getContext(), name + " added to cart!", Toast.LENGTH_SHORT).show());
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // ─── FAQs ────────────────────────────────────────────────────────────────

    private void loadFaqs() {
        database.child("faqs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!isAdded()) return;
                ArrayList<FaqModel> loaded = new ArrayList<>();
                for (DataSnapshot s : snapshot.getChildren()) {
                    FaqModel faq = s.getValue(FaqModel.class);
                    if (faq != null && faq.getQuestion() != null) loaded.add(faq);
                }
                if (loaded.isEmpty()) seedDefaultFaqs();
                else { faqAdapter.updateList(loaded); rvFaqs.setVisibility(View.VISIBLE); tvNoFaqs.setVisibility(View.GONE); }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) showHardcodedFaqs();
            }
        });
    }

    private void seedDefaultFaqs() {
        DatabaseReference ref = database.child("faqs");
        ref.child("faq1").child("question").setValue("How do I track my order?");
        ref.child("faq1").child("answer").setValue("You can track your order in the 'Past Orders' section of your profile.");
        ref.child("faq2").child("question").setValue("Can I cancel my order?");
        ref.child("faq2").child("answer").setValue("Orders can be cancelled before dispatch. Please contact customer support immediately.");
        ref.child("faq3").child("question").setValue("What payment methods are accepted?");
        ref.child("faq3").child("answer")
                .setValue("We currently accept Cash on Delivery (COD). Online payment options coming soon.")
                .addOnCompleteListener(task -> { if (isAdded()) loadFaqs(); });
    }

    private void showHardcodedFaqs() {
        ArrayList<FaqModel> d = new ArrayList<>();
        d.add(new FaqModel("How do I track my order?", "Track your order in the 'Past Orders' section."));
        d.add(new FaqModel("Can I cancel my order?", "Orders can be cancelled before dispatch."));
        d.add(new FaqModel("What payment methods are accepted?", "Cash on Delivery (COD) is accepted."));
        faqAdapter.updateList(d);
        rvFaqs.setVisibility(View.VISIBLE);
        tvNoFaqs.setVisibility(View.GONE);
    }
}
