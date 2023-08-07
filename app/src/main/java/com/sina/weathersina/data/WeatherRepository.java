package com.sina.weathersina.data;

import com.sina.weathersina.data.remote.WeatherService;
import com.sina.weathersina.model.WeatherResponse;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class WeatherRepository {
    private final WeatherService weatherService;

    @Inject
    public WeatherRepository(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    public Single<WeatherResponse> getWeatherInfo(String appId, Double lat, Double lon) {
        return subscribe(weatherService.getWeatherData(appId, lat, lon));
    }

    private <T> Single<T> subscribe(Single<T> single) {
        return single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
