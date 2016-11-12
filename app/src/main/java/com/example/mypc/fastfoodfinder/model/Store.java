package com.example.mypc.fastfoodfinder.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nhoxb on 11/10/2016.
 */
public class Store {
    public String getTitle() {
        return title;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LatLng getPosition()
    {
        return new LatLng(latitude, longitude);
    }

    public String getType() {
        return type;
    }

    @SerializedName("title")
    private String title;
    @SerializedName("lat")
    private double latitude;
    @SerializedName("lng")
    private double longitude;
    String type;
}
