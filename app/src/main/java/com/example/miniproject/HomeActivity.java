package com.example.miniproject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.miniproject.Fragment.CartFragment;
import com.example.miniproject.Fragment.HistoryFragment;
import com.example.miniproject.Fragment.HomeFragment;
import com.example.miniproject.Fragment.HomeFragment1;
import com.example.miniproject.Fragment.ProfileFragment;
import com.example.miniproject.Fragment.SearchFragment;
import com.example.miniproject.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;
    LinearLayout searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment1());

        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu_home) {
                replaceFragment(new HomeFragment1());
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
        // Add custom animations for smooth fragment transitions
        fragmentTransaction.setCustomAnimations(
            R.anim.fade_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.fade_out
        );
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
