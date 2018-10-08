package dk.topping.handin2;

import android.app.Application;
import android.content.Context;


public class WeatherApp extends Application {
    private static WeatherApp instance;

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
