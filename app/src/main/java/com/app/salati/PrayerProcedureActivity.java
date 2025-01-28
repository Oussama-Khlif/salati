package com.app.salati;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrayerProcedureActivity extends AppCompatActivity {

    private TextView rakaaTextView, surahTextView, instructionTextView;
    private Button nextRakaaButton, previousStepButton;
    private ImageButton backButton;
    private ImageView stepImageView;
    private int rakaaCounter = 1;
    private int totalRakaa;
    private String prayerName;
    private List<Surah> randomSurahs;
    private boolean inTashahud = false;
    private int stepCounter = 0;
    private boolean isShafaWitr;
    private boolean isLastRakaaWitr = false;
    private static final String FIRST_TASHAHUD = "التحيات لله والصلوات والطيبات، السلام عليك أيها النبي ورحمة الله وبركاته، السلام علينا وعلى عباد الله الصالحين، أشهد أن لا إله إلا الله وأشهد أن محمداً عبده ورسوله";

    private static final String FINAL_TASHAHUD = "التحيات لله والصلوات والطيبات، السلام عليك أيها النبي ورحمة الله وبركاته، السلام علينا وعلى عباد الله الصالحين، أشهد أن لا إله إلا الله وأشهد أن محمداً عبده ورسوله، اللهم صل على محمد وعلى آل محمد كما صليت على إبراهيم وعلى آل إبراهيم إنك حميد مجيد، اللهم بارك على محمد وعلى آل محمد كما باركت على إبراهيم وعلى آل إبراهيم إنك حميد مجيد";

    private static final String SURAH_FATIHA =
            "ٱلْحَمْدُ لِلَّهِ رَبِّ ٱلْعَٰلَمِينَ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ\n" +
                    "مَٰلِكِ يَوْمِ ٱلدِّينِ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ\n" +
                    "ٱهْدِنَا ٱلصِّرَٰطَ ٱلْمُسْتَقِيمَ صِرَٰطَ ٱلَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ ٱلْمَغْضُوبِ عَلَيْهِمْ وَلَا ٱلضَّآلِّينَ";

    private static class Surah {
        String name;
        String arabicText;
        int verseCount;

        Surah(String name, String arabicText, int verseCount) {
            this.name = name;
            this.arabicText = arabicText;
            this.verseCount = verseCount;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_procedure);

        initializeViews();
        initializeSurahs();
        setupListeners();
        updateUI();

        // Load the custom font from the 'res/font' folder
        Typeface customFont = ResourcesCompat.getFont(this, R.font.ya_modernpro_bold);

        // Apply custom font to the TextViews and Buttons (not the Quran text)
        rakaaTextView.setTypeface(customFont);
        instructionTextView.setTypeface(customFont);
        nextRakaaButton.setTypeface(customFont);
        previousStepButton.setTypeface(customFont);
    }

    private void initializeViews() {
        rakaaTextView = findViewById(R.id.rakaa_text);
        surahTextView = findViewById(R.id.surah_text);
        instructionTextView = findViewById(R.id.instruction_text);
        nextRakaaButton = findViewById(R.id.next_rakaa_button);
        previousStepButton = findViewById(R.id.previous_step_button);
        backButton = findViewById(R.id.back_button_prayer_procedure);
        stepImageView = findViewById(R.id.step_image);

        // Set text style to bold for all TextViews
        rakaaTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        surahTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        instructionTextView.setTypeface(null, android.graphics.Typeface.BOLD);
        nextRakaaButton.setTypeface(null, android.graphics.Typeface.BOLD);
        previousStepButton.setTypeface(null, android.graphics.Typeface.BOLD);

        totalRakaa = getIntent().getIntExtra("totalRakaa", 2);
        prayerName = getIntent().getStringExtra("prayerName");

        // Initially disable the back button since we're at the start
        previousStepButton.setEnabled(false);
        isShafaWitr = getIntent().getBooleanExtra("isShafaWitr", false);
    }

    private void initializeSurahs() {
        randomSurahs = new ArrayList<>();
        randomSurahs.add(new Surah("سورة الإخلاص",
                "قُلْ هُوَ اللَّهُ أَحَدٌ اللَّهُ الصَّمَدُ لَمْ يَلِدْ وَلَمْ يُولَدْ وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", 4));
        randomSurahs.add(new Surah("سورة الفلق",
                "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ مِن شَرِّ مَا خَلَقَ وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", 5));
        randomSurahs.add(new Surah("سورة الناس",
                "قُلْ أَعُوذُ بِرَبِّ النَّاسِ مَلِكِ النَّاسِ إِلَٰهِ النَّاسِ مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ مِنَ الْجِنَّةِ وَالنَّاسِ", 6));
        randomSurahs.add(new Surah("سورة الكوثر",
                "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ فَصَلِّ لِرَبِّكَ وَانْحَرْ إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", 3));
        randomSurahs.add(new Surah("سورة العصر",
                "وَالْعَصْرِ إِنَّ الْإِنسَانَ لَفِي خُسْرٍ إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", 3));
        randomSurahs.add(new Surah("سورة المسد",
                "تَبَّتْ يَدَا أَبِي لَهَبٍ وَتَبَّ مَا أَغْنَىٰ عَنْهُ مَالُهُ وَمَا كَسَبَ سَيَصْلَىٰ نَارًا ذَاتَ لَهَبٍ وَامْرَأَتُهُ حَمَّالَةَ الْحَطَبِ فِي جِيدِهَا حَبْلٌ مِّن مَّسَدٍ", 5));

        // Additional Surahs
        randomSurahs.add(new Surah("سورة قريش",
                "لِإِيلَافِ قُرَيْشٍ إِيلَافِهِمْ رِحْلَةَ الشِّتَاءِ وَالصَّيْفِ فَلْيَعْبُدُوا رَبَّ هَذَا الْبَيْتِ الَّذِي أَطْعَمَهُم مِّن جُوعٍ وَآمَنَهُم مِّنْ خَوْفٍ", 4));
        randomSurahs.add(new Surah("سورة الفيل",
                "أَلَمْ تَرَ كَيْفَ فَعَلَ رَبُّكَ بِأَصْحَابِ الْفِيلِ أَلَمْ يَجْعَلْ كَيْدَهُمْ فِي تَضْلِيلٍ وَأَرْسَلَ عَلَيْهِمْ طَيْرًا أَبَابِيلَ تَرْمِيهِم بِحِجَارَةٍ مِّن سِجِّيلٍ فَجَعَلَهُمْ كَعَصْفٍ مَّأْكُولٍ", 5));
        randomSurahs.add(new Surah("سورة الماعون",
                "أَرَأَيْتَ الَّذِي يُكَذِّبُ بِالدِّينِ فَذَٰلِكَ الَّذِي يَدُعُّ الْيَتِيمَ وَلَا يَحُضُّ عَلَى طَعَامِ الْمِسْكِينِ فَوَيْلٌ لِّلْمُصَلِّينَ الَّذِينَ هُمْ عَن صَلَاتِهِمْ سَاهُونَ الَّذِينَ هُمْ يُرَاءُونَ وَيَمْنَعُونَ الْمَاعُونَ", 7));
        randomSurahs.add(new Surah("سورة الكافرون",
                "قُلْ يَا أَيُّهَا الْكَافِرُونَ لَا أَعْبُدُ مَا تَعْبُدُونَ وَلَا أَنتُمْ عَابِدُونَ مَا أَعْبُدُ وَلَا أَنَا عَابِدٌ مَّا عَبَدتُّمْ وَلَا أَنتُمْ عَابِدُونَ مَا أَعْبُدُ لَكُمْ دِينُكُمْ وَلِيَ دِينِ", 6));
        randomSurahs.add(new Surah("سورة النصر",
                "إِذَا جَاءَ نَصْرُ اللَّهِ وَالْفَتْحُ وَرَأَيْتَ النَّاسَ يَدْخُلُونَ فِي دِينِ اللَّهِ أَفْوَاجًا فَسَبِّحْ بِحَمْدِ رَبِّكَ وَاسْتَغْفِرْهُ إِنَّهُ كَانَ تَوَّابًا", 3));
    }

    private void setupListeners() {
        nextRakaaButton.setOnClickListener(v -> {
            stepCounter++;
            updateBackButtonState();

            if (inTashahud) {
                inTashahud = false;
                if (isShafaWitr && rakaaCounter == 2) {
                    // Moving to Witr rakaa
                    rakaaCounter++;
                    isLastRakaaWitr = true;
                    updateUI();
                } else if (rakaaCounter < totalRakaa) {
                    rakaaCounter++;
                    updateUI();
                } else {
                    completePrayer();
                }
            } else if (isShafaWitr && rakaaCounter == 2) {
                // End of Shaf'a, start Witr
                inTashahud = true;
                updateUI();
            } else if (rakaaCounter % 2 == 0 && rakaaCounter < totalRakaa) {
                inTashahud = true;
                updateUI();
            } else if (rakaaCounter < totalRakaa) {
                rakaaCounter++;
                updateUI();
            } else {
                inTashahud = true;
                updateUI();
            }
        });

        previousStepButton.setOnClickListener(v -> {
            stepCounter--;
            updateBackButtonState();
            if (rakaaCounter > 1) {
                rakaaCounter--;
            } else {
                rakaaCounter = 1;
            }
            updateUI();
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrayNowActivity.class);
            startActivity(intent);
        });
    }

    private void updateBackButtonState() {
        if (stepCounter > 0) {
            previousStepButton.setEnabled(true);
        } else {
            previousStepButton.setEnabled(false);
        }
    }

    private void updateUI() {
        TextView surahFatihaText = findViewById(R.id.surah_fatiha_text);
        surahFatihaText.setVisibility(View.GONE);

        if (inTashahud) {
            String prayerStage = isShafaWitr && rakaaCounter == 2 ? " - نهاية الشفع" : " - التشهد";
            rakaaTextView.setText(prayerName + prayerStage);
            nextRakaaButton.setText("التالي");
            stepImageView.setImageResource(R.drawable.tashahud_position);

            if (rakaaCounter == totalRakaa) {
                surahTextView.setText(FINAL_TASHAHUD);
                instructionTextView.setText("التشهد الأخير");
            } else {
                surahTextView.setText(FIRST_TASHAHUD);
                instructionTextView.setText("التشهد الأوسط");
            }
        } else if (rakaaCounter > totalRakaa) {
            completePrayer();
        } else {
            String currentRakaa = isShafaWitr && rakaaCounter == 3 ? "الوتر" : String.valueOf(rakaaCounter);
            rakaaTextView.setText(prayerName + " - ركعة: " + currentRakaa + " من " + totalRakaa);
            nextRakaaButton.setText("التالي");
            stepImageView.setImageResource(R.drawable.standing_position);

            // Special handling for Witr rakaa
            if (isShafaWitr && isLastRakaaWitr) {
                // You might want to add Qunut dua here for Witr
                Surah randomSurah = getRandomSurah();
                surahTextView.setText("سورة الفاتحة\n\n" + SURAH_FATIHA + "\n\n" +
                        randomSurah.name + "\n\n" + randomSurah.arabicText +
                        "\n\n--- دعاء القنوت ---\n\n" + getQunutDua());
            } else if (rakaaCounter <= 2) {
                Surah randomSurah = getRandomSurah();
                surahTextView.setText("سورة الفاتحة\n\n" + SURAH_FATIHA + "\n\n" +
                        randomSurah.name + "\n\n" + randomSurah.arabicText);
            } else {
                surahTextView.setText("سورة الفاتحة\n\n" + SURAH_FATIHA);
            }

            instructionTextView.setText("القيام");
        }
    }
    private String getQunutDua() {
        return "اللَّهُمَّ اهْدِنِي فِيمَنْ هَدَيْتَ، وَعَافِنِي فِيمَنْ عَافَيْتَ، وَتَوَلَّنِي فِيمَنْ تَوَلَّيْتَ، وَبَارِكْ لِي فِيمَا أَعْطَيْتَ، وَقِنِي شَرَّ مَا قَضَيْتَ، فَإِنَّكَ تَقْضِي وَلَا يُقْضَى عَلَيْكَ، وَإِنَّهُ لَا يَذِلُّ مَنْ وَالَيْتَ، تَبَارَكْتَ رَبَّنَا وَتَعَالَيْتَ";
    }
    private void completePrayer() {
        rakaaTextView.setText(" انتهت صلاة "+prayerName );
        surahTextView.setText("السلام عليكم ورحمة الله و بركاته");
        instructionTextView.setText("التسليم");
        stepImageView.setImageResource(R.drawable.tasleem_position);
        nextRakaaButton.setVisibility(View.GONE);
        previousStepButton.setVisibility(View.GONE);
    }

    private Surah getRandomSurah() {
        Random random = new Random();
        return randomSurahs.get(random.nextInt(randomSurahs.size()));
    }
}