package com.sina.weathersina.data.remote;

import static com.sina.weathersina.utils.Constants.OPEN_WEATHER_GET_DATA;

import com.sina.weathersina.model.WeatherResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET(OPEN_WEATHER_GET_DATA)
    Single<WeatherResponse> getWeatherData(
            @Query("appid") String apiKey,
            @Query("lat") Double lat,
            @Query("lon") Double lon
    );
}