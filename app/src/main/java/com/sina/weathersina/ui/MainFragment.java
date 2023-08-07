package com.sina.weathersina.ui;

import static com.sina.weathersina.utils.LocationUtils.arePermissions;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sina.weathersina.R;
import com.sina.weathersina.model.WeatherResponse;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainFragment extends Fragment {
    private MainViewModel mainViewModel;
    private TextView tvCurrentTemp;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private ActivityResultLauncher<String[]> locationPermissionRequest;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts
                .RequestMultiplePermissions(), result -> {
            if (Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION)))
                mainViewModel.getUserLocation();
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
        initViews(view);
        observers();
    }

    private void initViews(View view) {
        tvCurrentTemp = view.findViewById(R.id.tvCurrentTemp);
    }

    private void observers() {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getLiveDataWeatherResponse().observe(getViewLifecycleOwner(), this::weatherData);
        mainViewModel.getLiveDataRequestPermissions().observe(getViewLifecycleOwner(),result->{
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
}