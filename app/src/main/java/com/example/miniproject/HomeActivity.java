package com.example.miniproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.miniproject.Fragment.CartFragment;
import com.example.miniproject.Fragment.HistoryFragment;
import com.example.miniproject.Fragment.HomeFragment1;
import com.example.miniproject.Fragment.ProfileFragment;
import com.example.miniproject.Fragment.SearchFragment;
import com.example.miniproject.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    private final Fragment homeFragment = new HomeFragment1();
    private final Fragment cartFragment = new CartFragment();
    private final Fragment searchFragment = new SearchFragment();
    private final Fragment historyFragment = new HistoryFragment();
    private final Fragment profileFragment = new ProfileFragment();

    private Fragment activeFragment = homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, profileFragment, "profile")
                    .hide(profileFragment)
                    .add(R.id.fragment_container, historyFragment, "history")
                    .hide(historyFragment)
                    .add(R.id.fragment_container, searchFragment, "search")
                    .hide(searchFragment)
                    .add(R.id.fragment_container, cartFragment, "cart")
                    .hide(cartFragment)
                    .add(R.id.fragment_container, homeFragment, "home")
                    .commit();
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_home) {
                switchFragment(homeFragment);
            } else if (id == R.id.menu_cart) {
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
            } else if (id == R.id.menu_search) {
                switchFragment(searchFragment);
            } else if (id == R.id.menu_history) {
                switchFragment(historyFragment);
            } else if (id == R.id.menu_profile) {
                switchFragment(profileFragment);
            }

            return true;
        });
    }

    private void switchFragment(Fragment targetFragment) {
        if (activeFragment == targetFragment) return;

        getSupportFragmentManager()
                .beginTransaction()
                .hide(activeFragment)
                .show(targetFragment)
                .commit();

        activeFragment = targetFragment;
    }
}