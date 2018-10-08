package dk.topping.handin2.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import dk.topping.handin2.OverviewActivity;
import dk.topping.handin2.R;
import dk.topping.handin2.externalservices.ImageReadyHandler;
import dk.topping.handin2.externalservices.OpenWeatherMapApiWrapper;
import dk.topping.handin2.models.CityWeatherData;
import dk.topping.handin2.util.Constants;
import dk.topping.handin2.util.LazyNotificationService;


public class WeatherService extends Service {

    private String NO_INTERNET_AVAILABLE;
    private final String TAG = this.getClass().getSimpleName();

    // API Level < 26 doesn't have the modern java.time library. So I made a date variable just for old apis.
    private ZonedDateTime lastUpdate;
    private Date lastUpdatedOldApi;
    private boolean running = false;
    private boolean isStarted = false;
    private OpenWeatherMapApiWrapper apiWrapper;
    private final int updateIntervalInSeconds = 120;

    private List<CityWeatherData> cityWeatherDataList;
    private CityWeatherData myCityData;
    private String myCityName;

    private final IBinder binder = new WeatherBinder();
    public class WeatherBinder extends Binder {
        public WeatherService getService() {
            return WeatherService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
         if(apiWrapper == null) {
            apiWrapper = new OpenWeatherMapApiWrapper(getApplicationContext());
        }
        return binder;
    }

    public WeatherService() {
    }

    public void stop() {
        running = false;
        this.stopSelf();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        running = true;
        NO_INTERNET_AVAILABLE = getString(R.string.broadcast_nointernet);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedpreferences_id), MODE_PRIVATE);
        myCityName = sharedPreferences.getString(getString(R.string.sharedpreferences_citykey), "Aarhus");
        Log.d("MYCITYNAME", myCityName);
        updateMyCity(myCityName);
        updateWeatherList();

        Runnable r = () -> {
            while(running) {
                try {
                    if(isTimeForUpdate()) {
                        updateMyCity(myCityName);
                        updateWeatherList();
                    } else {
                        Thread.sleep(updateIntervalInSeconds*1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread t = new Thread(r);
        t.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isStarted) {
            return super.onStartCommand(intent, flags, startId);
        }
        isStarted = true;
        Intent notificationIntent = new Intent(this, OverviewActivity.class);
        notificationIntent = Intent.makeRestartActivityTask(notificationIntent.getComponent());
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "Weather App")
                        .setContentTitle(getText(R.string.app_name))
                        .setContentText("-")
                        .setSmallIcon(R.drawable.ic_weather_notification)
                        .setContentIntent(pendingIntent)
                        .setPriority(Notification.PRIORITY_LOW)
                        .setTicker("-");

        // I tried following the documentation on https://developer.android.com/training/notify-user/build-notification.html
        // Documentation is wrong/outdated. There is no such thing createNotificationChannel on the compat version of notification manager.
        // API Levels 26+ require notifications to be written to a channel.
        // https://developer.android.com/guide/topics/ui/notifiers/notifications.html#compatibility
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Service.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(Constants.NotificationChannelId, "Weather App", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setBypassDnd(true);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }

        builder.setChannelId(Constants.NotificationChannelId);
        startForeground(Constants.NotificationId, builder.build());
        return super.onStartCommand(intent, flags, startId);
    }

    public void setMyCityName(String cityName) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.sharedpreferences_id), MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(getString(R.string.sharedpreferences_citykey), cityName).apply();
        myCityName = cityName;
    }

    public CityWeatherData getMyCityData() {
        return myCityData;
    }

    public CityWeatherData getSpecificCityData(String cityName) {
        if(cityWeatherDataList == null) {
            updateWeatherList();
            return null;
        }

        if(myCityData.getCityName().equals(cityName)) {
            return myCityData;
        }

        for (CityWeatherData d :
                cityWeatherDataList) {
            if(d.getCityName().equals(cityName)) {
                return d;
            }
        }
        return new CityWeatherData();
    }

    public List<CityWeatherData> getWeatherList() {
        return cityWeatherDataList;
    }

    public void getWeatherIcon(String iconId, ImageReadyHandler callback) {
        if(apiWrapper != null) {
            apiWrapper.getWeatherIcon(iconId, callback);
        }
    }

    public void updateMyCity() {
        updateMyCity(myCityName);
    }

    public void updateMyCity(String cityName) {
        if(isInternetAvailable() && apiWrapper != null) {
            Log.d(TAG, "Updating My City");
            setLastUpdate();
            apiWrapper.getWeatherForCity(cityName, data ->  {
                setMyCityName(cityName);
                myCityData = data;
            });
        }
    }

    public void updateWeatherList() {
        if(isInternetAvailable() && apiWrapper != null) {
            Log.d("updateWeatherList", "Refreshing data");
            setLastUpdate();
            apiWrapper.getWeatherForAllCities(list -> cityWeatherDataList = list);
            LazyNotificationService.getInstance().sendNotification();
        }
    }

    // Check if it's update time. Depending on which API level i used.
    private boolean isTimeForUpdate() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(lastUpdate == null) {
                lastUpdate = ZonedDateTime.now();
                return true;
            }
            if((ZonedDateTime.now().toEpochSecond() - lastUpdate.toEpochSecond()) > updateIntervalInSeconds) {
                setLastUpdate();
                return true;
            }
        }
        else {
            if(lastUpdatedOldApi == null) {
                lastUpdatedOldApi = new Date();
                return true;
            }
            if((new Date().getTime() - lastUpdatedOldApi.getTime() ) > updateIntervalInSeconds * 1000) {
                setLastUpdate();
                return true;
            }
        }

        return false;
    }

    private synchronized void setLastUpdate() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lastUpdate = ZonedDateTime.now();
        } else {
            lastUpdatedOldApi = new Date();
        }
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo == null) {
            Log.d(TAG, "No internet" );
            Intent intent = new Intent().setAction(NO_INTERNET_AVAILABLE);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            return false;
        }
        if(networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            Log.d(TAG, "No internet" );
            Intent intent = new Intent().setAction(NO_INTERNET_AVAILABLE);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            return false;
        }
    }

}
