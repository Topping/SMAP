package dk.topping.handin2.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.Date;

import dk.topping.handin2.R;
import dk.topping.handin2.WeatherApp;

public class LazyNotificationService {
    private static LazyNotificationService instance;
    private Context context;

    private LazyNotificationService() {
        context = WeatherApp.getContext();
    }

    public static LazyNotificationService getInstance() {
        if(instance == null) {
            instance = new LazyNotificationService();
        }
        return instance;
    }

    public void sendNotification() {
        String now = new Date().toString();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "WeatherApp")
                .setSmallIcon(R.drawable.ic_weather_notification)
                .setContentTitle(context.getString(R.string.weathernotification_title))
                .setContentText(context.getString(R.string.weathernotification_content) + " " + now)
                .setPriority(Notification.PRIORITY_LOW)
                .setChannelId(Constants.NotificationChannelId);
        NotificationManager manager = (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

        Notification notification = builder.build();

        manager.notify(Constants.NotificationId, notification);
    }
}
