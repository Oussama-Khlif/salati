package com.app.salati;

import com.google.gson.annotations.SerializedName;

public class PrayerTimesResponse {
    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {
        private Timings timings;
        private Meta meta;

        public Timings getTimings() {
            return timings;
        }
    }

    public static class Timings {
        @SerializedName("Fajr")
        private String fajr;
        @SerializedName("Dhuhr")
        private String dhuhr;
        @SerializedName("Asr")
        private String asr;
        @SerializedName("Maghrib")
        private String maghrib;
        @SerializedName("Isha")
        private String isha;

        public String getFajr() { return fajr; }
        public String getDhuhr() { return dhuhr; }
        public String getAsr() { return asr; }
        public String getMaghrib() { return maghrib; }
        public String getIsha() { return isha; }
    }

    public static class Meta {
        private double latitude;
        private double longitude;
        private String timezone;
        private Method method;

        public static class Method {
            private int id;
            private String name;
        }
    }
}