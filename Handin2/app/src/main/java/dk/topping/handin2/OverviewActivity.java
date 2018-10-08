package dk.topping.handin2;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import java.util.List;

import dk.topping.handin2.fragments.MyCityFragment;
import dk.topping.handin2.fragments.MyCityFragmentImpl;
import dk.topping.handin2.fragments.WeatherListFragment;
import dk.topping.handin2.fragments.WeatherListFragmentImpl;
import dk.topping.handin2.models.CityWeatherData;
import dk.topping.handin2.service.WeatherService;

public class OverviewActivity extends FragmentActivity implements WeatherListFragmentImpl.WeatherListFragmentListener, MyCityFragmentImpl.MyCityFragmentListener {

    private final String TAG = this.getClass().getSimpleName();

    private String WEATHER_LIST_UPDATED;
    private String MY_CITY_UPDATED;
    private String NO_INTERNET_AVAILABLE;

    private CoordinatorLayout coordinatorLayout;
    private ServiceConnection connection;
    private WeatherService weatherService;
    private boolean bound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        WEATHER_LIST_UPDATED = getString(R.string.broadcast_weatherlistready);
        MY_CITY_UPDATED = getString(R.string.broadcast_mycityready);
        NO_INTERNET_AVAILABLE = getString(R.string.broadcast_nointernet);

        coordinatorLayout = findViewById(R.id.overviewSnackbar);
        FrameLayout myCity = findViewById(R.id.myCityFragment);
        myCity.setOnClickListener(l -> {
            showMyCityDetails();
        });


        MyCityFragmentImpl cityFragment = MyCityFragmentImpl.newInstance();
        WeatherListFragmentImpl fragment = WeatherListFragmentImpl.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.weatherListFragment, fragment, "WeatherListFragmentImpl")
                .replace(R.id.myCityFragment, cityFragment, "MyCityFragmentImpl")
                .commit();

        setupForegroundService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupServiceConnection();

        IntentFilter dataReady = new IntentFilter();
        IntentFilter noInternet = new IntentFilter();
        IntentFilter myCityReady = new IntentFilter();
        myCityReady.addAction(MY_CITY_UPDATED);
        dataReady.addAction(WEATHER_LIST_UPDATED);
        noInternet.addAction(NO_INTERNET_AVAILABLE);

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(onWeatherDataUpdated, dataReady);
        manager.registerReceiver(onNoInternetConnection, noInternet);
        manager.registerReceiver(onMyCityReady, myCityReady);

        Intent intent = new Intent(this, WeatherService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onWeatherDataUpdated);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onNoInternetConnection);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onMyCityReady);
        if(bound) {
            if(isFinishing()) {
                Log.d(TAG, "Is finishing");
                weatherService.stop();
            }
            unbindService(connection);
            bound = false;
        }
    }

    private void setupServiceConnection() {
        if(connection == null) {
            connection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    WeatherService.WeatherBinder binder = (WeatherService.WeatherBinder) service;
                    weatherService = binder.getService();
                    bound = true;
                    updateMyCityData();
                    updateWeatherList();
                    refreshWeatherData();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    bound = false;
                }
            };
        }
    }

    private void setupForegroundService() {
        Intent intent = new Intent(this, WeatherService.class);
        startService(intent);
    }

    private BroadcastReceiver onWeatherDataUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Received new weather data");

            updateWeatherList();
        }
    };

    private BroadcastReceiver onNoInternetConnection = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "NO CONNECTION IN OVERVIEW");
            displayNoInternetSnackbar();
        }
    };

    private BroadcastReceiver onMyCityReady = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "My city updated");
            updateMyCityData();
        }
    };

    private void updateMyCityData() {
        if(bound && weatherService != null) {
            CityWeatherData d = weatherService.getMyCityData();
            if(d != null) {
                MyCityFragment fragment = (MyCityFragment) getSupportFragmentManager().findFragmentByTag("MyCityFragmentImpl");
                if(fragment != null) {
                    fragment.setCityInformation(d);
                }
            }
        }
    }

    private void updateWeatherList() {
        if(bound && weatherService != null) {
            List<CityWeatherData> l = weatherService.getWeatherList();
            if(l != null) {
                WeatherListFragment fragment = (WeatherListFragment) getSupportFragmentManager().findFragmentByTag("WeatherListFragmentImpl");
                if(fragment != null) {
                    fragment.updateWeatherList(l);
                }
            }
        }
    }

    private void showMyCityDetails() {
        if(bound && weatherService != null) {
            CityWeatherData data = weatherService.getMyCityData();
            if(data != null) {
                Log.d(TAG, "Clicked on city: " + data.getCityName());
                goToDetails(data.getCityName());
            }
        }
    }

    // I wanted a snackbar to display an error message when there is no internet. Problem is this:
    // https://issuetracker.google.com/issues/64285517
    // This is a hack that makes it work based on the comments in the issue.
    private void displayNoInternetSnackbar() {
        coordinatorLayout.bringToFront();
        Snackbar snackbar = Snackbar.make(coordinatorLayout, getString(R.string.error_nointernet), Snackbar.LENGTH_LONG);
        WeatherListFragment fragment = (WeatherListFragment) getSupportFragmentManager().findFragmentByTag("WeatherListFragmentImpl");
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fragment.disableSwipeRefresh();
            snackbar.show();
            fragment.enableSwipeRefresh();
        } else {
            snackbar.show();
        }
        //Make the SwipeRefreshLayout stop showing the loading circle.
        fragment.stopRefreshing();
    }

    // Fragment interface functions
    @Override
    public void refreshWeatherData() {
        if(bound) {
            weatherService.updateMyCity();
            weatherService.updateWeatherList();
        }
    }
    @Override
    public void goToDetails(String cityName) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(getString(R.string.intentcode_selectedcityname), cityName);
        startActivity(intent);
    }
    @Override
    public void setMyCityName(String cityName) {
        weatherService.updateMyCity(cityName);
    }
    @Override
    public void refreshMyCity() {
        updateMyCityData();
    }
}
