package com.app.salati;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrayerTimesActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "PrayerTimes";

    // UI elements
    private TextView locationTextView;
    private TextView fajrTimeTextView, dhuhrTimeTextView, asrTimeTextView, maghribTimeTextView, ishaTimeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_times);

        // Initialize UI elements
        initializeViews();

        // Initialize location services
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Check and request location permissions
        checkLocationPermission();

        ImageButton backButton = findViewById(R.id.back_button);  // Use ImageButton instead of Button
        backButton.setOnClickListener(v -> {
            // Finish current activity and go back to the main activity
            finish();  // This will close the current activity and return to the previous activity (e.g., MainActivity)
        });
    }

    private void initializeViews() {
        locationTextView = findViewById(R.id.location_text);
        fajrTimeTextView = findViewById(R.id.fajr_time);
        dhuhrTimeTextView = findViewById(R.id.dhuhr_time);
        asrTimeTextView = findViewById(R.id.asr_time);
        maghribTimeTextView = findViewById(R.id.maghrib_time);
        ishaTimeTextView = findViewById(R.id.isha_time);
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // If we have permission, get the location
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // For testing purposes, directly call the static city endpoint
        fetchPrayerTimesByCity();

        // Keep the original location code commented for future use
        /*
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    getCityName(latitude, longitude);
                    fetchPrayerTimes(34.020882, -6.841650);
                } else {
                    Toast.makeText(PrayerTimesActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    getCityName(34.020882, -6.841650);
                    fetchPrayerTimes(34.020882, -6.841650);
                }
            }
        });
        */
    }

    private void getCityName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String cityName = address.getLocality();
                String countryName = address.getCountryName();
                StringBuilder locationText = new StringBuilder();

                if (cityName != null && !cityName.isEmpty()) {
                    locationText.append(cityName);
                    if (countryName != null && !countryName.isEmpty()) {
                        locationText.append(", ").append(countryName);
                    }
                } else if (countryName != null && !countryName.isEmpty()) {
                    locationText.append(countryName);
                }

                if (locationText.length() > 0) {
                    locationTextView.setText(locationText.toString());
                } else {
                    locationTextView.setText("Location Unknown");
                }

                // Log the location for debugging
                Log.d(TAG, "Location: " + locationText.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            locationTextView.setText("Location Unavailable");
            Log.e(TAG, "Geocoder error: " + e.getMessage());
        }
    }

    private void fetchPrayerTimes(double latitude, double longitude) {

        String apiUrl = "https://api.aladhan.com/v1/timings/today?latitude=" + latitude + "&longitude=" + longitude + "&method=2&timezonestring=auto";

        Log.d(TAG, "API URL will be: " + apiUrl);

        PrayerTimesAPI prayerTimesAPI = RetrofitClient.getRetrofitInstance().create(PrayerTimesAPI.class);

        Call<PrayerTimesResponse> call = prayerTimesAPI.getPrayerTimes(String.valueOf(latitude), String.valueOf(longitude), 2, "auto");

        call.enqueue(new Callback<PrayerTimesResponse>() {
            @Override
            public void onResponse(Call<PrayerTimesResponse> call, Response<PrayerTimesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Log.d(TAG, "Full API Response: " + new Gson().toJson(response.body()));

                    PrayerTimesResponse.Data data = response.body().getData();
                    if (data != null) {
                        PrayerTimesResponse.Timings timings = data.getTimings();
                        if (timings != null) {
                            Log.d(TAG, timings.getFajr());
                            Log.d(TAG,  timings.getDhuhr());
                            Log.d(TAG, timings.getAsr());
                            Log.d(TAG, timings.getMaghrib());
                            Log.d(TAG, timings.getIsha());

                            updateUI(timings.getFajr(), timings.getDhuhr(), timings.getAsr(), timings.getMaghrib(), timings.getIsha());
                        } else {
                            Log.e(TAG, "Timings object is NULL");
                        }
                    } else {
                        Log.e(TAG, "Data object is NULL");
                    }
                } else {
                    try {

                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error response: " + errorBody);
                        Log.e(TAG, "Error code: " + response.code());
                        Toast.makeText(PrayerTimesActivity.this, "Error fetching prayer times: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PrayerTimesResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(PrayerTimesActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Add new method for static city-based API call
    private void fetchPrayerTimesByCity() {
        PrayerTimesAPI prayerTimesAPI = RetrofitClient.getRetrofitInstance().create(PrayerTimesAPI.class);
        Call<PrayerTimesResponse> call = prayerTimesAPI.getPrayerTimesByCity();

        // Set location text for static city
        locationTextView.setText("Masakin, Tunisia");

        call.enqueue(new Callback<PrayerTimesResponse>() {
            @Override
            public void onResponse(Call<PrayerTimesResponse> call, Response<PrayerTimesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Full API Response: " + new Gson().toJson(response.body()));

                    PrayerTimesResponse.Data data = response.body().getData();
                    if (data != null) {
                        PrayerTimesResponse.Timings timings = data.getTimings();
                        if (timings != null) {
                            Log.d(TAG, timings.getFajr());
                            Log.d(TAG, timings.getDhuhr());
                            Log.d(TAG, timings.getAsr());
                            Log.d(TAG, timings.getMaghrib());
                            Log.d(TAG, timings.getIsha());

                            updateUI(timings.getFajr(), timings.getDhuhr(), timings.getAsr(), timings.getMaghrib(), timings.getIsha());
                        } else {
                            Log.e(TAG, "Timings object is NULL");
                        }
                    } else {
                        Log.e(TAG, "Data object is NULL");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Error response: " + errorBody);
                        Log.e(TAG, "Error code: " + response.code());
                        Toast.makeText(PrayerTimesActivity.this, "Error fetching prayer times: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<PrayerTimesResponse> call, Throwable t) {
                Log.e(TAG, "Network error: " + t.getMessage(), t);
                Toast.makeText(PrayerTimesActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateUI(String fajr, String dhuhr, String asr, String maghrib, String isha) {
        fajrTimeTextView.setText(fajr);
        dhuhrTimeTextView.setText(dhuhr);
        asrTimeTextView.setText(asr);
        maghribTimeTextView.setText(maghrib);
        ishaTimeTextView.setText(isha);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this,
                        "Location permission is required for accurate prayer times",
                        Toast.LENGTH_LONG).show();
                // Use default coordinates as fallback
                getCityName(36.8065, 10.1815);
                fetchPrayerTimes(36.8065, 10.1815);
            }
        }
    }
}