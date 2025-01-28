package com.app.salati;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Button references
        Button btnPrayerTimes = findViewById(R.id.btn_prayer_times);
        Button btnQibla = findViewById(R.id.btn_qibla);
        Button btnDuas = findViewById(R.id.btn_duas);
        Button btnPrayNow = findViewById(R.id.btn_pray_now);

        // Set button click listeners
        btnPrayerTimes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PrayerTimesActivity.class);
            startActivity(intent);
        });

        btnQibla.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, QiblaActivity.class);
            startActivity(intent);
        });

        btnPrayNow.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PrayNowActivity.class);
            startActivity(intent);
        });

        btnDuas.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DuasActivity.class);
            startActivity(intent);
        });
    }
}
