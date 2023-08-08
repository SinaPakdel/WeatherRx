package com.sina.weathersina.ui;

import static com.sina.weathersina.utils.Constants.APP_ID;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.sina.weathersina.data.WeatherRepository;
import com.sina.weathersina.model.WeatherResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.SingleObserver;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

@HiltViewModel
public class MainViewModel extends AndroidViewModel {
    private String cityName;
    // https://github.com/IvayloZankov/ZaniWeather
    private final FusedLocationProviderClient fusedLocationClient;
    @Inject
    WeatherRepository weatherRepository;
    public CompositeDisposable compositeDisposable = new CompositeDisposable();


    private MutableLiveData<Boolean> loadingMutableLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingMutableLiveData;
    }


    public MutableLiveData<Throwable> errorMutableLiveData = new MutableLiveData<>();

    public LiveData<Throwable> getErrorLiveData() {
        return errorMutableLiveData;
    }

    private final MutableLiveData<String> mutableLiveDataLocation = new MutableLiveData<>();

    public LiveData<String> getLiveDataLocation() {
        return mutableLiveDataLocation;
    }


    private final MutableLiveData<Boolean> mutableLiveDataPermissions = new MutableLiveData<>();

    public MutableLiveData<Boolean> getLiveDataRequestPermissions() {
        return mutableLiveDataPermissions;
    }

    private final MutableLiveData<WeatherResponse> mutableLiveDataWeatherResponse = new MutableLiveData<>();

    public LiveData<WeatherResponse> getLiveDataWeatherResponse() {
        return mutableLiveDataWeatherResponse;
    }


    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void resetPermissions() {
        mutableLiveDataPermissions.setValue(false);
    }

    @Inject
    public MainViewModel(
            @NonNull Application application,
            WeatherRepository weatherRepository
    ) {
        super(application);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication());
        this.weatherRepository = weatherRepository;
        getUserLocation();
    }

    public void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                Log.e("TAG", "getUserLocation: location");
                if (location != null) {
                    Log.e("TAG", "getUserLocation:-- " + location.getLatitude() + location.getLongitude());
                    initWeatherDataRequest(location.getLatitude(), location.getLongitude());

                    Geocoder geocoder = new Geocoder(getApplication());
                    List<Address> addresses = new ArrayList<>();
                    try {
                        addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 10);

                    } catch (IOException e) {
                        Log.e("TAG", "getUserLocation: IOException " + e.getMessage());
                        e.printStackTrace();
                    }
                    if (addresses.size() > 0) {
                        Address address = addresses.get(0);
                        cityName = address.getLocality();
                        mutableLiveDataLocation.setValue(cityName + ", " + address.getCountryName());
                    }
                } else {
                    Log.e("TAG", "getUserLocation: null");
                }
            });
        } else {
            Log.e("TAG", "getUserLocation: else");
            mutableLiveDataPermissions.setValue(true);
            loadingMutableLiveData.setValue(false);
        }
    }

    public void initWeatherDataRequest(double latitude, double longitude) {
        Log.e("TAG", "initWeatherDataRequest: " + latitude + longitude);
        loadingMutableLiveData.setValue(true);

        weatherRepository.getWeatherInfo(latitude, longitude).subscribe(new Observer<WeatherResponse>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                compositeDisposable.add(d);
            }

            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull WeatherResponse weatherResponse) {
                Log.e("TAG", "onNext: " + weatherResponse.getMain().getTemp());
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.e("TAG", "onError: " );
            }

            @Override
            public void onComplete() {
                Log.e("TAG", "onComplete: ");
            }
//            @Override
//            public void onSuccess(WeatherResponse weatherResponse) {
//                mutableLiveDataWeatherResponse.setValue(weatherResponse);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                super.onError(e);
//                errorMutableLiveData.setValue(e);
//            }
        });
    }

//    public Future<Observable<WeatherResponse>> weatherFuture() {
//        return weatherRepository.weatherFutureCall();
//    }


//    public abstract class WeatherObserver<T extends WeatherResponse> implements Observable<T> {
//        @Override
//        public void onSubscribe(Disposable d) {
//            compositeDisposable.add(d);
//        }
//
//        @Override
//        public void onError(Throwable e) {
//            errorMutableLiveData.setValue(e);
//            e.printStackTrace();
//        }
//    }

}