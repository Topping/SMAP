package dk.topping.handin2.models;

// https://openweathermap.org/current#name
// https://app.quicktype.io/
// Cleaned up to give more meaningful variable names.

import java.io.Serializable;

public class Wind implements Serializable {
    private double speed;
    private double deg;

    public double getSpeed() { return speed; }
    public void setSpeed(double value) { this.speed = value; }

    public double getDeg() { return deg; }
    public void setDeg(long value) { this.deg = value; }
}
