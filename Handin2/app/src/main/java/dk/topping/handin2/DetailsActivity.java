package dk.topping.handin2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import dk.topping.handin2.models.CityWeatherData;
import dk.topping.handin2.service.WeatherService;

public class DetailsActivity extends AppCompatActivity {

    private TextView timestamp, windContent, temperatureContent, descriptionContent, cityName;
    private ImageView weatherIcon;
    private CoordinatorLayout coordinatorLayout;
    private Button okButton;

    private String WEATHER_DATA_UPDATED, NO_INTERNET_AVAILABLE, selectedCityName;
    private CityWeatherData selectedCity;
    private ServiceConnection connection;
    private WeatherService weatherService;
    private boolean bound = false;

    @Override
    protected void onStart() {
        super.onStart();
        setupServiceConnection();
        Intent intent = new Intent(DetailsActivity.this, WeatherService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        IntentFilter weatherUpdated = new IntentFilter();
        IntentFilter noInternet = new IntentFilter();
        weatherUpdated.addAction(WEATHER_DATA_UPDATED);
        noInternet.addAction(NO_INTERNET_AVAILABLE);

        LocalBroadcastManager.getInstance(this).registerReceiver(onWeatherDataUpdated, weatherUpdated);
        LocalBroadcastManager.getInstance(this).registerReceiver(onNoInternetConnection, noInternet);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        windContent = findViewById(R.id.detailsWindContent);
        temperatureContent = findViewById(R.id.detailsTemperatureContent);
        descriptionContent = findViewById(R.id.detailsDescriptionContent);
        coordinatorLayout = findViewById(R.id.detailsSnackbar);
        weatherIcon = findViewById(R.id.detailsWeatherIcon);
        cityName = findViewById(R.id.detailsCityName);
        timestamp = findViewById(R.id.detailsTimestamp);
        okButton = findViewById(R.id.detailsOkButton);
        okButton.setOnClickListener(l -> finish());

        WEATHER_DATA_UPDATED = getString(R.string.broadcast_weatherlistready);
        NO_INTERNET_AVAILABLE = getString(R.string.broadcast_nointernet);
        selectedCityName = getIntent().getStringExtra(getString(R.string.intentcode_selectedcityname));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onWeatherDataUpdated);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNoInternetConnection);
        if(bound) {
            unbindService(connection);
            bound = false;
        }
    }

    private void setDisplayText() {
        Long unixTime = selectedCity.getTimeStampUnix();
        Date normalTime = new Date(unixTime * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        String now = sdf.format(normalTime);
        timestamp.setText(now);
        windContent.setText(String.format("%.1f m/s", selectedCity.getWind().getSpeed()));
        temperatureContent.setText(String.format("%.1f \u2103", selectedCity.getWeatherDetails().getTemp()));
        descriptionContent.setText(selectedCity.getWeatherDescription().get(0).getDescription());
        cityName.setText(selectedCity.getCityName());
    }

    private void setupServiceConnection() {
        if(connection == null) {
            connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    WeatherService.WeatherBinder binder = (WeatherService.WeatherBinder) service;
                    weatherService = binder.getService();
                    bound = true;
                    updateSelectedCityData();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    bound = false;
                }
            };
        }
    }

    private BroadcastReceiver onWeatherDataUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("onWeatherDataUpdated", "Received new weather data");
            updateSelectedCityData();
        }
    };

    private BroadcastReceiver onNoInternetConnection = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("overviewNoConnection", "NO CONNECTION IN DETAIL");
            displayNoInternetSnackbar();
        }
    };

    private void displayNoInternetSnackbar() {
        coordinatorLayout.bringToFront();
        Snackbar snackbar = Snackbar.make(coordinatorLayout, getString(R.string.error_nointernet), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void updateSelectedCityData() {
        if(bound) {
            selectedCity = weatherService.getSpecificCityData(selectedCityName);
            weatherService.getWeatherIcon(selectedCity.getWeatherDescription().get(0).getIconId(), bitmap -> weatherIcon.setImageBitmap(bitmap));
        }
        setDisplayText();
    }
}
