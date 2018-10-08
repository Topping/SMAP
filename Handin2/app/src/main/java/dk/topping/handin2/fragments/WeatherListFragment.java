package dk.topping.handin2.fragments;


import java.util.List;

import dk.topping.handin2.models.CityWeatherData;

public interface WeatherListFragment {
    void updateWeatherList(List<CityWeatherData> data);
    void disableSwipeRefresh();
    void enableSwipeRefresh();
    void stopRefreshing();
}
