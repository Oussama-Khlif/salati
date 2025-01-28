package com.app.salati;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PrayerTimesAPI {
    // Keep the original method for future use
    @GET("v1/timings/today")
    Call<PrayerTimesResponse> getPrayerTimes(
            @Query("latitude") String latitude,
            @Query("longitude") String longitude,
            @Query("method") int method,
            @Query("timezonestring") String timezone
    );

    // Add new static method for testing
    @GET("v1/timingsByCity/28-01-2025?city=Masakin&country=Tunisia&method=2")
    Call<PrayerTimesResponse> getPrayerTimesByCity();
}