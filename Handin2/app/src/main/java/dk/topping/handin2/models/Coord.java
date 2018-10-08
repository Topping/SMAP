package dk.topping.handin2.models;

// https://openweathermap.org/current#name
// https://app.quicktype.io/
// Cleaned up to give more meaningful variable names.

import java.io.Serializable;

public class Coord implements Serializable {
    private double lon;
    private double lat;

    public double getLon() { return lon; }
    public void setLon(double value) { this.lon = value; }

    public double getLat() { return lat; }
    public void setLat(double value) { this.lat = value; }
}
