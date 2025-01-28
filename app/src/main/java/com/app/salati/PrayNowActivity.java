package com.app.salati;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class PrayNowActivity extends AppCompatActivity {

    private Button fajrButton, dhuhrButton, asrButton, maghribButton, ishaButton, shafaWitrButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pray_now);

        // Initialize prayer selection buttons
        fajrButton = findViewById(R.id.fajr_button);
        dhuhrButton = findViewById(R.id.dhuhr_button);
        asrButton = findViewById(R.id.asr_button);
        maghribButton = findViewById(R.id.maghrib_button);
        ishaButton = findViewById(R.id.isha_button);
        shafaWitrButton = findViewById(R.id.shaf_button);

        // Initialize the back button
        backButton = findViewById(R.id.back_button);

        // Set onClickListeners for each prayer button
        fajrButton.setOnClickListener(v -> startPrayerProcedure(2, "الفجر"));
        dhuhrButton.setOnClickListener(v -> startPrayerProcedure(4, "الظهر"));
        asrButton.setOnClickListener(v -> startPrayerProcedure(4, "العصر"));
        maghribButton.setOnClickListener(v -> startPrayerProcedure(3, "المغرب"));
        ishaButton.setOnClickListener(v -> startPrayerProcedure(4, "العشاء"));
        shafaWitrButton.setOnClickListener(v -> startPrayerProcedure(3, "الشفع والوتر", true));

        // Set onClickListener for back button
        backButton.setOnClickListener(v -> onBackPressed());
    }

    // Overloaded method for regular prayers
    private void startPrayerProcedure(int rakaaCount, String prayerName) {
        startPrayerProcedure(rakaaCount, prayerName, false);
    }

    // Method to start the Prayer Procedure Activity with additional parameter for Shafa and Witr
    private void startPrayerProcedure(int rakaaCount, String prayerName, boolean isShafaWitr) {
        Intent intent = new Intent(PrayNowActivity.this, PrayerProcedureActivity.class);
        intent.putExtra("totalRakaa", rakaaCount);
        intent.putExtra("prayerName", prayerName);
        intent.putExtra("isShafaWitr", isShafaWitr);
        startActivity(intent);
    }
}