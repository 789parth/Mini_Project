package com.example.miniproject;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.miniproject.Fragment.CartFragment;
import com.example.miniproject.Fragment.HistoryFragment;
import com.example.miniproject.Fragment.HomeFragment;
import com.example.miniproject.Fragment.ProfileFragment;
import com.example.miniproject.Fragment.SearchFragment;
import com.example.miniproject.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Toast.makeText(this, "Clicked", Toast.LENGTH_SHORT).show();

            int id = item.getItemId();

            if (id == R.id.menu_home) {
                replaceFragment(new HomeFragment());
            } else if (id == R.id.menu_cart) {
                replaceFragment(new CartFragment());
            } else if (id == R.id.menu_search) {
                replaceFragment(new SearchFragment());
            } else if (id == R.id.menu_history) {
                replaceFragment(new HistoryFragment());
            } else if (id == R.id.menu_profile) {
                replaceFragment(new ProfileFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
