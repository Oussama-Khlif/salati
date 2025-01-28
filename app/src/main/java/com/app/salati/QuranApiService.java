// QuranApiService.java
package com.app.salati;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface QuranApiService {
    @GET("surah/{surahNumber}")
    Call<SurahResponse> getSurahInfo(@Path("surahNumber") int surahNumber);

    @GET("ayah/{surahNumber}:{ayahNumber}")
    Call<AyahResponse> getAyah(
            @Path("surahNumber") int surahNumber,
            @Path("ayahNumber") int ayahNumber
    );
}

// Response models
class SurahResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private int number;
        private String name;
        private int numberOfAyahs;

        public int getNumber() {
            return number;
        }

        public String getName() {
            return name;
        }

        public int getNumberOfAyahs() {
            return numberOfAyahs;
        }
    }
}

class SurahData {
    private int number;
    private String name;
    private String englishName;
    private int numberOfAyahs;

    public int getNumberOfAyahs() {
        return numberOfAyahs;
    }
}

class AyahResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private String text;

        public String getText() {
            return text;
        }
    }
}

class AyahData {
    private int number;
    private String text;
    private SurahInfo surah;

    public String getText() {
        return text;
    }
}

class SurahInfo {
    private int number;
    private String name;
    private String englishName;
}