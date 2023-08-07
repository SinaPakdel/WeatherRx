package com.sina.weathersina.di;

import static com.sina.weathersina.utils.Constants.OPEN_WEATHER_BASE_URL;

import com.sina.weathersina.data.WeatherRepository;
import com.sina.weathersina.data.remote.WeatherService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public Retrofit provideRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(OPEN_WEATHER_BASE_URL)
                .client(new OkHttpClient.Builder().addInterceptor(
                        new HttpLoggingInterceptor()
                                .setLevel(HttpLoggingInterceptor.Level.BODY)).build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    public WeatherService provideWeatherApi(Retrofit retrofit) {
        return retrofit.create(WeatherService.class);
    }

    @Provides
    @Singleton
    public WeatherRepository provideWeatherRepository(WeatherService weatherService) {
        return new WeatherRepository(weatherService);
    }
}
