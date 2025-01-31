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
    private static final String FIRST_TASHAHUD = "التحيات لله، الزَّكيات لله، الطيبات الصلوات لله، السلام عليك أيها النبي ورحمة الله وبركاته.. السلام علينا وعلى عباد الله الصالحين، أشهد أن لا إله إلا الله، وأشهد أن محمدًا عبده ورسوله";

    private static final String FINAL_TASHAHUD = "التحيات لله، الزَّكيات لله، الطيبات الصلوات لله، السلام عليك أيها النبي ورحمة الله وبركاته.. السلام علينا وعلى عباد الله الصالحين، أشهد أن لا إله إلا الله، وأشهد أن محمدًا عبده ورسوله، اللهم صل على محمد وعلى آل محمد كما صليت على إبراهيم وعلى آل إبراهيم إنك حميد مجيد، اللهم بارك على محمد وعلى آل محمد كما باركت على إبراهيم وعلى آل إبراهيم إنك حميد مجيد";

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
        // Additional shorter surahs
        randomSurahs.add(new Surah("سورة الزلزلة",
                "إِذَا زُلْزِلَتِ الْأَرْضُ زِلْزَالَهَا وَأَخْرَجَتِ الْأَرْضُ أَثْقَالَهَا وَقَالَ الْإِنسَانُ مَا لَهَا يَوْمَئِذٍ تُحَدِّثُ أَخْبَارَهَا بِأَنَّ رَبَّكَ أَوْحَىٰ لَهَا يَوْمَئِذٍ يَصْدُرُ النَّاسُ أَشْتَاتًا لِّيُرَوْا أَعْمَالَهُمْ فَمَن يَعْمَلْ مِثْقَالَ ذَرَّةٍ خَيْرًا يَرَهُ وَمَن يَعْمَلْ مِثْقَالَ ذَرَّةٍ شَرًّا يَرَهُ", 8));
        randomSurahs.add(new Surah("سورة العاديات",
                "وَالْعَادِيَاتِ ضَبْحًا فَالْمُورِيَاتِ قَدْحًا فَالْمُغِيرَاتِ صُبْحًا فَأَثَرْنَ بِهِ نَقْعًا فَوَسَطْنَ بِهِ جَمْعًا إِنَّ الْإِنسَانَ لِرَبِّهِ لَكَنُودٌ وَإِنَّهُ عَلَىٰ ذَٰلِكَ لَشَهِيدٌ وَإِنَّهُ لِحُبِّ الْخَيْرِ لَشَدِيدٌ أَفَلَا يَعْلَمُ إِذَا بُعْثِرَ مَا فِي الْقُبُورِ وَحُصِّلَ مَا فِي الصُّدُورِ إِنَّ رَبَّهُم بِهِمْ يَوْمَئِذٍ لَّخَبِيرٌ", 11));
        randomSurahs.add(new Surah("سورة القارعة",
                "الْقَارِعَةُ مَا الْقَارِعَةُ وَمَا أَدْرَاكَ مَا الْقَارِعَةُ يَوْمَ يَكُونُ النَّاسُ كَالْفَرَاشِ الْمَبْثُوثِ وَتَكُونُ الْجِبَالُ كَالْعِهْنِ الْمَنفُوشِ فَأَمَّا مَن ثَقُلَتْ مَوَازِينُهُ فَهُوَ فِي عِيشَةٍ رَّاضِيَةٍ وَأَمَّا مَنْ خَفَّتْ مَوَازِينُهُ فَأُمُّهُ هَاوِيَةٌ وَمَا أَدْرَاكَ مَا هِيَهْ نَارٌ حَامِيَةٌ", 11));
        randomSurahs.add(new Surah("سورة التكاثر",
                "أَلْهَاكُمُ التَّكَاثُرُ حَتَّىٰ زُرْتُمُ الْمَقَابِرَ كَلَّا سَوْفَ تَعْلَمُونَ ثُمَّ كَلَّا سَوْفَ تَعْلَمُونَ كَلَّا لَوْ تَعْلَمُونَ عِلْمَ الْيَقِينِ لَتَرَوُنَّ الْجَحِيمَ ثُمَّ لَتَرَوُنَّهَا عَيْنَ الْيَقِينِ ثُمَّ لَتُسْأَلُنَّ يَوْمَئِذٍ عَنِ النَّعِيمِ", 8));
        randomSurahs.add(new Surah("سورة الهمزة",
                "وَيْلٌ لِّكُلِّ هُمَزَةٍ لُّمَزَةٍ الَّذِي جَمَعَ مَالًا وَعَدَّدَهُ يَحْسَبُ أَنَّ مَالَهُ أَخْلَدَهُ كَلَّا لَيُنبَذَنَّ فِي الْحُطَمَةِ وَمَا أَدْرَاكَ مَا الْحُطَمَةُ نَارُ اللَّهِ الْمُوقَدَةُ الَّتِي تَطَّلِعُ عَلَى الْأَفْئِدَةِ إِنَّهَا عَلَيْهِم مُّؤْصَدَةٌ فِي عَمَدٍ مُّمَدَّدَةٍ", 9));
        randomSurahs.add(new Surah("سورة التين",
                "وَالتِّينِ وَالزَّيْتُونِ وَطُورِ سِينِينَ وَهَٰذَا الْبَلَدِ الْأَمِينِ لَقَدْ خَلَقْنَا الْإِنسَانَ فِي أَحْسَنِ تَقْوِيمٍ ثُمَّ رَدَدْنَاهُ أَسْفَلَ سَافِلِينَ إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ فَلَهُمْ أَجْرٌ غَيْرُ مَمْنُونٍ فَمَا يُكَذِّبُكَ بَعْدُ بِالدِّينِ أَلَيْسَ اللَّهُ بِأَحْكَمِ الْحَاكِمِينَ", 8));
        randomSurahs.add(new Surah("سورة الشرح",
                "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ وَوَضَعْنَا عَنكَ وِزْرَكَ الَّذِي أَنقَضَ ظَهْرَكَ وَرَفَعْنَا لَكَ ذِكْرَكَ فَإِنَّ مَعَ الْعُسْرِ يُسْرًا إِنَّ مَعَ الْعُسْرِ يُسْرًا فَإِذَا فَرَغْتَ فَانصَبْ وَإِلَىٰ رَبِّكَ فَارْغَب", 8));
        randomSurahs.add(new Surah("سورة الضحى",
                "وَالضُّحَىٰ وَاللَّيْلِ إِذَا سَجَىٰ مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ وَلَلْآخِرَةُ خَيْرٌ لَّكَ مِنَ الْأُولَىٰ وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰ أَلَمْ يَجِدْكَ يَتِيمًا فَآوَىٰ وَوَجَدَكَ ضَالًّا فَهَدَىٰ وَوَجَدَكَ عَائِلًا فَأَغْنَىٰ فَأَمَّا الْيَتِيمَ فَلَا تَقْهَرْ وَأَمَّا السَّائِلَ فَلَا تَنْهَرْ وَأَمَّا بِنِعْمَةِ رَبِّكَ فَحَدِّثْ", 11));
        randomSurahs.add(new Surah("سورة البلد",
                "لَا أُقْسِمُ بِهَٰذَا الْبَلَدِ وَأَنتَ حِلٌّ بِهَٰذَا الْبَلَدِ وَوَالِدٍ وَمَا وَلَدَ لَقَدْ خَلَقْنَا الْإِنسَانَ فِي كَبَدٍ أَيَحْسَبُ أَن لَّن يَقْدِرَ عَلَيْهِ أَحَدٌ يَقُولُ أَهْلَكْتُ مَالًا لُّبَدًا أَيَحْسَبُ أَن لَّمْ يَرَهُ أَحَدٌ أَلَمْ نَجْعَل لَّهُ عَيْنَيْنِ وَلِسَانًا وَشَفَتَيْنِ وَهَدَيْنَاهُ النَّجْدَيْنِ فَلَا اقْتَحَمَ الْعَقَبَةَ وَمَا أَدْرَاكَ مَا الْعَقَبَةُ فَكُّ رَقَبَةٍ أَوْ إِطْعَامٌ فِي يَوْمٍ ذِي مَسْغَبَةٍ يَتِيمًا ذَا مَقْرَبَةٍ أَوْ مِسْكِينًا ذَا مَتْرَبَةٍ ثُمَّ كَانَ مِنَ الَّذِينَ آمَنُوا وَتَوَاصَوْا بِالصَّبْرِ وَتَوَاصَوْا بِالْمَرْحَمَةِ أُولَٰئِكَ أَصْحَابُ الْمَيْمَنَةِ وَالَّذِينَ كَفَرُوا بِآيَاتِنَا هُمْ أَصْحَابُ الْمَشْأَمَةِ عَلَيْهِمْ نَارٌ مُّؤْصَدَةٌ", 20));
        randomSurahs.add(new Surah("سورة الغاشية",
                "هَلْ أَتَاكَ حَدِيثُ الْغَاشِيَةِ وُجُوهٌ يَوْمَئِذٍ خَاشِعَةٌ عَامِلَةٌ نَّاصِبَةٌ تَصْلَىٰ نَارًا حَامِيَةً تُسْقَىٰ مِنْ عَيْنٍ آنِيَةٍ لَّيْسَ لَهُمْ طَعَامٌ إِلَّا مِن ضَرِيعٍ لَّا يُسْمِنُ وَلَا يُغْنِي مِن جُوعٍ وُجُوهٌ يَوْمَئِذٍ نَّاعِمَةٌ لِّسَعْيِهَا رَاضِيَةٌ فِي جَنَّةٍ عَالِيَةٍ لَّا تَسْمَعُ فِيهَا لَاغِيَةً فِيهَا عَيْنٌ جَارِيَةٌ فِيهَا سُرُرٌ مَّرْفُوعَةٌ وَأَكْوَابٌ مَّوْضُوعَةٌ وَنَمَارِقُ مَصْفُوفَةٌ وَزَرَابِيُّ مَبْثُوثَةٌ أَفَلَا يَنظُرُونَ إِلَى الْإِبِلِ كَيْفَ خُلِقَتْ وَإِلَى السَّمَاءِ كَيْفَ رُفِعَتْ وَإِلَى الْجِبَالِ كَيْفَ نُصِبَتْ وَإِلَى الْأَرْضِ كَيْفَ سُطِحَتْ فَذَكِّرْ إِنَّمَا أَنتَ مُذَكِّرٌ لَّسْتَ عَلَيْهِم بِمُصَيْطِرٍ إِلَّا مَن تَوَلَّىٰ وَكَفَرَ فَيُعَذِّبُهُ اللَّهُ الْعَذَابَ الْأَكْبَرَ إِنَّ إِلَيْنَا إِيَابَهُمْ ثُمَّ إِنَّ عَلَيْنَا حِسَابَهُمْ", 26));
        randomSurahs.add(new Surah("سورة الفجر",
                "وَالْفَجْرِ وَلَيَالٍ عَشْرٍ وَالشَّفْعِ وَالْوَتْرِ وَاللَّيْلِ إِذَا يَسْرِ هَلْ فِي ذَٰلِكَ قَسَمٌ لِّذِي حِجْرٍ أَلَمْ تَرَ كَيْفَ فَعَلَ رَبُّكَ بِعَادٍ إِرَمَ ذَاتِ الْعِمَادِ الَّتِي لَمْ يُخْلَقْ مِثْلُهَا فِي الْبِلَادِ وَثَمُودَ الَّذِينَ جَابُوا الصَّخْرَ بِالْوَادِ وَفِرْعَوْنَ ذِي الْأَوْتَادِ الَّذِينَ طَغَوْا فِي الْبِلَادِ فَأَكْثَرُوا فِيهَا الْفَسَادَ فَصَبَّ عَلَيْهِمْ رَبُّكَ سَوْطَ عَذَابٍ إِنَّ رَبَّكَ لَبِالْمِرْصَادِ فَأَمَّا الْإِنسَانُ إِذَا مَا ابْتَلَاهُ رَبُّهُ فَأَكْرَمَهُ وَنَعَّمَهُ فَيَقُولُ رَبِّي أَكْرَمَنِ وَأَمَّا إِذَا مَا ابْتَلَاهُ فَقَدَرَ عَلَيْهِ رِزْقَهُ فَيَقُولُ رَبِّي أَهَانَنِ كَلَّا بَل لَّا تُكْرِمُونَ الْيَتِيمَ وَلَا تَحَاضُّونَ عَلَىٰ طَعَامِ الْمِسْكِينِ وَتَأْكُلُونَ التُّرَاثَ أَكْلًا لَّمًّا وَتُحِبُّونَ الْمَالَ حُبًّا جَمًّا كَلَّا إِذَا دُكَّتِ الْأَرْضُ دَكًّا دَكًّا وَجَاءَ رَبُّكَ وَالْمَلَكُ صَفًّا صَفًّا وَجِيءَ يَوْمَئِذٍ بِجَهَنَّمَ يَوْمَئِذٍ يَتَذَكَّرُ الْإِنسَانُ وَأَنَّىٰ لَهُ الذِّكْرَىٰ يَقُولُ يَا لَيْتَنِي قَدَّمْتُ لِحَيَاتِي فَيَوْمَئِذٍ لَّا يُعَذِّبُ عَذَابَهُ أَحَدٌ وَلَا يُوثِقُ وَثَاقَهُ أَحَدٌ يَا أَيَّتُهَا النَّفْسُ الْمُطْمَئِنَّةُ ارْجِعِي إِلَىٰ رَبِّكِ رَاضِيَةً مَّرْضِيَّةً فَادْخُلِي فِي عِبَادِي وَادْخُلِي جَنَّتِي", 30));
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