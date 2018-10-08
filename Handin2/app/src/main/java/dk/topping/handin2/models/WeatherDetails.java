package dk.topping.handin2.models;

// https://openweathermap.org/current#name
// https://app.quicktype.io/
// Cleaned up to give more meaningful variable names.

import java.io.Serializable;

public class WeatherDetails implements Serializable {
    private double temp;
    private double pressure;
    private double humidity;
    private double tempMin;
    private double tempMax;

    public double getTemp() { return temp; }
    public void setTemp(double value) { this.temp = value; }

    public double getPressure() { return pressure; }
    public void setPressure(long value) { this.pressure = value; }

    public double getHumidity() { return humidity; }
    public void setHumidity(long value) { this.humidity = value; }

    public double getTempMin() { return tempMin; }
    public void setTempMin(double value) { this.tempMin = value; }

    public double getTempMax() { return tempMax; }
    public void setTempMax(double value) { this.tempMax = value; }
}
