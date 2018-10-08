package dk.topping.handin2.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

// https://openweathermap.org/current#name
// https://app.quicktype.io/
// Cleaned up to give more meaningful variable names.
public class CityWeatherData implements Serializable {
    private Coord coord;
    @SerializedName("weather")
    private ArrayList<WeatherDescription> weatherDescriptions;
    @SerializedName("main")
    private WeatherDetails weatherDetails;
    private long visibility;
    private Wind wind;
    private Clouds clouds;
    @SerializedName("dt")
    private long timeStampUnix;
    @SerializedName("sys")
    private CountryInformation countryInformation;
    @SerializedName("id")
    private long cityId;
    @SerializedName("name")
    private String cityName;

    public CityWeatherData() {
        coord = new Coord();
        weatherDescriptions = new ArrayList<>();
        weatherDescriptions.add(new WeatherDescription());
        wind = new Wind();
        clouds = new Clouds();
        countryInformation = new CountryInformation();
        weatherDetails = new WeatherDetails();
    }

    public Coord getCoord() { return coord; }
    public void setCoord(Coord value) { this.coord = value; }

    public ArrayList<WeatherDescription> getWeatherDescription() { return weatherDescriptions; }
    public void setWeatherDescription(ArrayList<WeatherDescription> value) { this.weatherDescriptions = value; }

    public WeatherDetails getWeatherDetails() { return weatherDetails; }
    public void setWeatherDetails(WeatherDetails value) { this.weatherDetails = value; }

    public long getVisibility() { return visibility; }
    public void setVisibility(long value) { this.visibility = value; }

    public Wind getWind() { return wind; }
    public void setWind(Wind value) { this.wind = value; }

    public Clouds getClouds() { return clouds; }
    public void setClouds(Clouds value) { this.clouds = value; }

    public long getTimeStampUnix() { return timeStampUnix; }
    public void setTimeStampUnix(long value) { this.timeStampUnix = value; }

    public CountryInformation getCountryInformation() { return countryInformation; }
    public void setCountryInformation(CountryInformation value) { this.countryInformation = value; }

    public long getCityId() { return cityId; }
    public void setCityId(long value) { this.cityId = value; }

    public String getCityName() { return cityName; }
    public void setCityName(String value) { this.cityName = value; }

}
