package com.sina.weathersina.ui;

import static com.sina.weathersina.utils.LocationUtils.arePermissions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.sina.weathersina.R;
import com.sina.weathersina.model.WeatherResponse;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainFragment extends Fragment {
    private MainViewModel mainViewModel;
    private TextView tvCurrentTemp;
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String[]> locationPermissionRequest;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts
                .RequestMultiplePermissions(), result -> {
            if (Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION)))
//                mainViewModel.getUserLocation();
                requestLocationUpdates();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        requestLocationUpdates();
        initViews(view);
        observers();
    }

    private void initViews(View view) {
        tvCurrentTemp = view.findViewById(R.id.tvCurrentTemp);
    }

    private void observers() {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getLiveDataWeatherResponse().observe(getViewLifecycleOwner(), this::weatherData);
        mainViewModel.getLiveDataRequestPermissions().observe(getViewLifecycleOwner(), result -> {
            if (result) {
                requestPermissions();
                mainViewModel.resetPermissions();
            }
        });
    }

    private void weatherData(WeatherResponse weatherResponse) {
        tvCurrentTemp.setText(weatherResponse.getMain().getTemp().toString());
    }

    private void requestPermissions() {
        if (!arePermissions(requireContext())) {
            locationPermissionRequest.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainViewModel.compositeDisposable.clear();
    }

    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.e("REZ", "onLocationResult: " + latitude + " " + longitude);
                    }
                }
            }
        }, Looper.getMainLooper());
    }

}