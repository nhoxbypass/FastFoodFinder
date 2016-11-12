package com.example.mypc.fastfoodfinder.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nhoxb on 11/11/2016.
 */
public class Leg {

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

    public String getStartAddress() {
        return startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public List<Step> getStepList() {
        return stepList;
    }

    @SerializedName("distance")
    JsonObject distance;
    @SerializedName("duration")
    JsonObject duration;
    @SerializedName("start_address")
    String startAddress;
    @SerializedName("end_address")
    String endAddress;
    @SerializedName("steps")
    List<Step> stepList;
}
