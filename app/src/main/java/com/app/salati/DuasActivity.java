package com.app.salati;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.Random;

public class DuasActivity extends AppCompatActivity {
    private TextView tvVerse;
    private TextView tvCounter;
    private Button btnIncrement;
    private Button btnReset;
    private Button btnRefreshVerse;
    private ProgressBar progressBar;
    private Spinner surahSpinner;
    private int counter = 0;
    private static final String BASE_URL = "https://api.alquran.cloud/v1/";
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duas);

        initializeViews();
        setupSpinner();
        setupTasbihCounter();
        setupBackButton();
        fetchRandomVerse();
    }

    private void initializeViews() {
        tvVerse = findViewById(R.id.tvVerse);
        tvCounter = findViewById(R.id.tvCounter);
        btnIncrement = findViewById(R.id.btnIncrement);
        btnReset = findViewById(R.id.btnReset);
        btnRefreshVerse = findViewById(R.id.btnRefreshVerse);
        progressBar = findViewById(R.id.progressBar);
        surahSpinner = findViewById(R.id.surahSpinner);
    }

    private void setupSpinner() {
        String[] surahs = new String[]{
                "1. الفاتحة",
                "2. البقرة",
                "3. آل عمران",
                "4. النساء",
                "5. المائدة",
                "6. الأنعام",
                "7. الأعراف",
                "8. الأنفال",
                "9. التوبة",
                "10. يونس"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                surahs
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        surahSpinner.setAdapter(adapter);
    }

    private void setupTasbihCounter() {
        tvCounter.setText(String.valueOf(counter));

        btnIncrement.setOnClickListener(v -> {
            counter++;
            tvCounter.setText(String.valueOf(counter));
            if (counter % 33 == 0) {
                Toast.makeText(this, "مَا شَاءَ اللَّهُ! لقد سبّحت 33 مرة", Toast.LENGTH_SHORT).show();
                fetchRandomVerse();
            }
        });

        btnReset.setOnClickListener(v -> {
            counter = 0;
            tvCounter.setText(String.valueOf(counter));
            Toast.makeText(this, "أعيد العداد إلى الصفر", Toast.LENGTH_SHORT).show();
        });

        btnRefreshVerse.setOnClickListener(v -> fetchRandomVerse());
    }

    private void setupBackButton() {
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(DuasActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void fetchRandomVerse() {
        showLoading(true);

        // Generate random surah number (1-10)
        int randomSurah = random.nextInt(10) + 1;

        // Update spinner selection to match the random surah
        surahSpinner.setSelection(randomSurah - 1);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        QuranApiService apiService = retrofit.create(QuranApiService.class);
        Call<SurahResponse> surahCall = apiService.getSurahInfo(randomSurah);

        surahCall.enqueue(new Callback<SurahResponse>() {
            @Override
            public void onResponse(Call<SurahResponse> call, Response<SurahResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SurahResponse.Data surahData = response.body().getData();
                    int numberOfAyahs = surahData.getNumberOfAyahs();
                    int randomAyah = random.nextInt(numberOfAyahs) + 1;
                    String surahName = surahData.getName();
                    fetchAyahWithSurahInfo(randomSurah, randomAyah, surahName);
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(Call<SurahResponse> call, Throwable t) {
                showError();
            }
        });
    }

    private void fetchAyahWithSurahInfo(int surah, int ayah, String surahName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        QuranApiService apiService = retrofit.create(QuranApiService.class);
        Call<AyahResponse> arabicCall = apiService.getAyah(surah, ayah);

        arabicCall.enqueue(new Callback<AyahResponse>() {
            @Override
            public void onResponse(Call<AyahResponse> call, Response<AyahResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String arabicText = response.body().getData().getText();
                    // Format the display with surah name and ayah number
                    String formattedText = String.format("%s\n\n%s - آية %d", arabicText, surahName, ayah);
                    tvVerse.setText(formattedText);
                    showLoading(false);
                } else {
                    showError();
                }
            }

            @Override
            public void onFailure(Call<AyahResponse> call, Throwable t) {
                showError();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        tvVerse.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError() {
        showLoading(false);
        Toast.makeText(this, "Error fetching verse", Toast.LENGTH_SHORT).show();
    }
}