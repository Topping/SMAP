package dk.topping.handin2.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

// https://openweathermap.org/current#name
// https://app.quicktype.io/
// Cleaned up to give more meaningful variable names.
public class Clouds implements Serializable {

    @SerializedName("all")
    private long coveragePercent;

    public long getCoveragePercent() { return coveragePercent; }
    public void setCoveragePercent(long value) { this.coveragePercent = value; }
}
