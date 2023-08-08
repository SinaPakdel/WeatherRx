package com.sina.weathersina.data;

import com.sina.weathersina.data.remote.WeatherService;
import com.sina.weathersina.model.WeatherResponse;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WeatherRepository {
    private final WeatherService weatherService;

    @Inject
    public WeatherRepository(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    public Observable<WeatherResponse> getWeatherInfo(Double lat, Double lon) {
        return subscribe(weatherService.getWeatherData(lat, lon));
    }

    private <T> Observable<T> subscribe(Observable<T> single) {
        return single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

//    public Future<Observable<WeatherResponse>> weatherFutureCall() {
//        final ExecutorService executorService = Executors.newSingleThreadExecutor();
//        final Callable<Observable<WeatherResponse>> callable = new Callable<Observable<WeatherResponse>>() {
//            @Override
//            public Observable<WeatherResponse> call() throws Exception {
//                return weatherService.getWeatherData();
//            }
//        };
//
//
//        final Future<Observable<WeatherResponse>> future = new Future<Observable<WeatherResponse>>() {
//            @Override
//            public boolean cancel(boolean mayInterruptIfRunning) {
//                if (mayInterruptIfRunning) {
//                    executorService.shutdown();
//                }
//                return false;
//            }
//
//            @Override
//            public boolean isCancelled() {
//                executorService.shutdown();
//                return false;
//            }
//
//            @Override
//            public boolean isDone() {
//                return executorService.isTerminated();
//            }
//
//            @Override
//            public Observable<WeatherResponse> get() throws ExecutionException, InterruptedException {
//                return executorService.submit(callable).get();
//            }
//
//            @Override
//            public Observable<WeatherResponse> get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
//                return executorService.submit(callable).get(timeout, unit);
//            }
//        };
//
//        return future;
//    }
}
