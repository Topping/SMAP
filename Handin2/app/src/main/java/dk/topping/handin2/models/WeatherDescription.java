package dk.topping.handin2.models;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

// https://openweathermap.org/current#name
// https://app.quicktype.io/
// Cleaned up to give more meaningful variable names.

public class WeatherDescription implements Serializable {
    private long id;
    @SerializedName("main")
    private String shortDescription;
    private String description;
    @SerializedName("icon")
    private String iconId;

    public long getID() { return id; }
    public void setID(long value) { this.id = value; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String value) { this.shortDescription = value; }

    public String getDescription() { return description; }
    public void setDescription(String value) { this.description = value; }

    public String getIconId() { return iconId; }
    public void setIconId(String value) { this.iconId = value; }
}
