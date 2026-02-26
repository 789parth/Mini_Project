package com.example.miniproject;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button confirmButton;
    private Marker selectedLocationMarker;
    private LatLng selectedLatLng;
    private String selectedAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        confirmButton = findViewById(R.id.confirmButton);

        setupConfirmButton();
    }

    private void setupConfirmButton() {
        confirmButton.setOnClickListener(v -> {
            if (selectedLatLng != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedLocation", selectedAddress);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Please select a location on the map.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng bvmCollegeLocation = new LatLng(22.5523, 72.9231);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bvmCollegeLocation, 15));
        updateMarker(bvmCollegeLocation);
        mMap.setOnMapClickListener(this::updateMarker);
    }

    private void updateMarker(LatLng latLng) {
        if (selectedLocationMarker == null) {
            selectedLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng));
        } else {
            selectedLocationMarker.setPosition(latLng);
        }
        selectedLatLng = latLng;
        getAddressFromLatLng(latLng);
    }

    private void getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                selectedAddress = addresses.get(0).getAddressLine(0);
            } else {
                selectedAddress = "Unknown Location";
            }
        } catch (IOException e) {
            Toast.makeText(this, "Error getting address", Toast.LENGTH_SHORT).show();
        }
    }
}
