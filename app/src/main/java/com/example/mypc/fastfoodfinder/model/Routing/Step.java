package com.example.mypc.fastfoodfinder.model.Routing;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nhoxb on 11/11/2016.
 */
public class Step {
    public String getDistance() {
        return distance.getAsJsonObject("text").getAsString();
    }

    public long getDistanceValue()
    {
        return distance.getAsJsonObject("value").getAsLong();
    }

    public String getDuration() {
        return duration.getAsJsonObject("text").getAsString();
    }

    public long getDurationValue()
    {
        return duration.getAsJsonObject("value").getAsLong();
    }

    public MapCoordination getStartMapCoordination() {
        return startMapCoordination;
    }

    public String getInstruction() {
        return instruction;
    }

    public MapCoordination getEndMapCoordination() {
        return endMapCoordination;
    }

    public String getTravelMode() {
        return travelMode;
    }

    @SerializedName("distance")
    JsonObject distance;
    @SerializedName("duration")
    JsonObject duration;
    @SerializedName("start_location")
    MapCoordination startMapCoordination;
    @SerializedName("end_location")
    MapCoordination endMapCoordination;
    @SerializedName("html_instructions")
    String instruction;
    @SerializedName("travel_mode")
    String travelMode;
}
