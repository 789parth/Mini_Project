package com.example.miniproject;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SelectLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "SelectLocationActivity";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private Button confirmButton;
    private EditText searchEditText;
    private ImageView searchIconBtn;
    private FloatingActionButton currentLocationBtn;
    private Marker selectedLocationMarker;
    private LatLng selectedLatLng;
    private String selectedAddress = "";
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<IntentSenderRequest> resolutionLauncher =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    fetchCurrentLocation();
                } else {
                    Toast.makeText(this, "Location services are required for this feature", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        confirmButton = findViewById(R.id.confirmButton);
        searchEditText = findViewById(R.id.searchEditText);
        searchIconBtn = findViewById(R.id.searchIconBtn);
        currentLocationBtn = findViewById(R.id.currentLocationBtn);

        setupConfirmButton();
        setupSearch();
        setupCurrentLocation();
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

    private void setupSearch() {
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchLocation();
                return true;
            }
            return false;
        });

        searchIconBtn.setOnClickListener(v -> searchLocation());
    }

    private void searchLocation() {
        String location = searchEditText.getText().toString();
        if (!location.isEmpty()) {
            Geocoder geocoder = new Geocoder(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(location, 1, addresses -> {
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        runOnUiThread(() -> {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            updateMarker(latLng);
                        });
                    } else {
                        runOnUiThread(() -> Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show());
                    }
                });
            } else {
                try {
                    List<Address> addressList = geocoder.getFromLocationName(location, 1);
                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        updateMarker(latLng);
                    } else {
                        Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Geocoder error", e);
                }
            }
        } else {
            Toast.makeText(this, "Enter a location to search", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupCurrentLocation() {
        currentLocationBtn.setOnClickListener(v -> checkLocationSettingsAndFetchLocation());
    }

    private void checkLocationSettingsAndFetchLocation() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setMinUpdateIntervalMillis(5000)
                .build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, locationSettingsResponse -> fetchCurrentLocation());

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(resolvable.getResolution()).build();
                    resolutionLauncher.launch(intentSenderRequest);
                } catch (Exception ex) {
                    Log.e(TAG, "Error launching settings resolution", ex);
                }
            }
        });
    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        updateMarker(latLng);
                    } else {
                        fusedLocationClient.getLastLocation().addOnSuccessListener(lastLoc -> {
                            if (lastLoc != null) {
                                LatLng latLng = new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                updateMarker(latLng);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Location fetch failed", e));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission is required to find your position.", Toast.LENGTH_SHORT).show();
            }
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1, addresses -> {
                if (addresses != null && !addresses.isEmpty()) {
                    selectedAddress = addresses.get(0).getAddressLine(0);
                } else {
                    selectedAddress = "Unknown Location";
                }
            });
        } else {
            try {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    selectedAddress = addresses.get(0).getAddressLine(0);
                } else {
                    selectedAddress = "Unknown Location";
                }
            } catch (IOException e) {
                Log.e(TAG, "Error getting address", e);
            }
        }
    }
}
