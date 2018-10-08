package dk.topping.handin2.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import dk.topping.handin2.R;
import dk.topping.handin2.externalservices.OpenWeatherMapApiWrapper;
import dk.topping.handin2.models.CityWeatherData;


public class WeatherListAdapter extends BaseAdapter {
    private List<CityWeatherData> weatherData;
    private Context context;
    private OpenWeatherMapApiWrapper api;

    public WeatherListAdapter(List<CityWeatherData> data, Context context) {
        this.weatherData = data;
        this.context = context;
        this.api = new OpenWeatherMapApiWrapper(context);
    }

    @Override
    public int getCount() {
        return weatherData.size();
    }

    @Override
    public Object getItem(int position) {
        return weatherData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fragment_weather_list_item, parent, false);
        }

        CityWeatherData weatherData = (CityWeatherData) getItem(position);
        TextView cityName = convertView.findViewById(R.id.cityListCityName);
        TextView temperature = convertView.findViewById(R.id.cityListTemperature);
        final ImageView weatherIcon = convertView.findViewById(R.id.cityListWeatherIcon);

        double tempInCelcius = weatherData.getWeatherDetails().getTemp();
        temperature.setText(String.valueOf(tempInCelcius) + "\u2103");
        cityName.setText(weatherData.getCityName());

        String iconId = weatherData.getWeatherDescription().get(0).getIconId();
        api.getWeatherIcon(iconId, bitmap -> weatherIcon.setImageBitmap(bitmap));

        return convertView;
    }
}
