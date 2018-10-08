package dk.topping.handin2.externalservices;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import dk.topping.handin2.R;
import dk.topping.handin2.models.CityWeatherData;
import dk.topping.handin2.util.HardcodedCities;
import dk.topping.handin2.volley.VolleySingleton;

public class OpenWeatherMapApiWrapper {
    private final String TAG = this.getClass().getSimpleName();

    private final Context context;
    private final String baseUrl;
    private final String apiKey;
    private final String WEATHER_LIST_UPDATED;
    private final String MY_CITY_UPDATED;

    public OpenWeatherMapApiWrapper(Context context) {
        this.context = context;
        this.baseUrl = context.getString(R.string.openweathermapurl);
        this.apiKey = context.getString(R.string.openweathermapkey);
        this.WEATHER_LIST_UPDATED = context.getString(R.string.broadcast_weatherlistready);
        this.MY_CITY_UPDATED = context.getString(R.string.broadcast_mycityready);
    }

    public void getWeatherForCity(String cityName, WeatherReadyHandler<CityWeatherData> callback) {
        final String city = cityName.replaceAll("\\s+","");
        String uri = Uri.parse(baseUrl).buildUpon()
                .appendEncodedPath("weather")
                .appendQueryParameter("APPID", apiKey)
                .appendQueryParameter("q", city)
                .appendQueryParameter("units", "metric")
                .build()
                .toString();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                uri,
                null,
                response -> {
                    Gson gson = new GsonBuilder().create();
                    CityWeatherData data = gson.fromJson(response.toString(), CityWeatherData.class);
                    callback.setWeatherResponse(data);
                    broadcastMyCityReady();
                },
                error -> {
                    if (error.networkResponse.statusCode == 404) {
                        Toast.makeText(context, String.format("%s doesn't exist", city), Toast.LENGTH_LONG).show();
                    }
                    Log.w(TAG, "Failed to get data for city: " + city);
                });
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getWeatherForAllCities(WeatherReadyHandler<List<CityWeatherData>> callback) {
        HashMap<String, Integer> majorCities = HardcodedCities.getMajorDanishCities();
        // I'm sorry whoever has to read this. But I'm simply too lazy to make a propper solution. And this "works"
        // Also all the nice stuff with java streams I can't do because it requires API level 24... :(
        StringBuilder strBuilder = new StringBuilder();
        for (int i : majorCities.values()) {
            strBuilder.append(i).append(',');
        }
        // Delete the last char, because the last character is a comma, which I don't want.
        strBuilder.deleteCharAt(strBuilder.length() - 1);
        String cityIds = strBuilder.toString();

        String uri = Uri.parse(baseUrl).buildUpon()
                .appendEncodedPath("group")
                .appendQueryParameter("APPID", apiKey)
                .appendQueryParameter("id", cityIds)
                .appendQueryParameter("units", "metric")
                .build()
                .toString();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                uri,
                null,
                response -> {
                    Gson gson = new GsonBuilder().create();
                    JsonObject jsonObject = gson.fromJson(response.toString(), JsonElement.class).getAsJsonObject();
                    Type collectionType = new TypeToken<List<CityWeatherData>>(){}.getType();
                    List<CityWeatherData> weatherData = gson.fromJson(jsonObject.get("list"), collectionType);
                    callback.setWeatherResponse(weatherData);
                    broadcastWeatherListReady();
                },
                error -> Log.w(TAG, "FAILED"));

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    public void getWeatherIcon(String iconId, ImageReadyHandler callback) {
        Log.d(TAG, "Getting icon id: " + iconId);
        String iconUrl = String.format("%s%s.png",context.getString(R.string.openweathermapiconurl),iconId);
        VolleySingleton.getInstance(context).getImageLoader().get(iconUrl, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                callback.setImageView(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Failed to get icon");
            }
        });
    }

    private void broadcastMyCityReady() {
        Intent intent = new Intent().setAction(MY_CITY_UPDATED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void broadcastWeatherListReady() {
        Intent intent = new Intent().setAction(WEATHER_LIST_UPDATED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
