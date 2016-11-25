package com.example.mypc.fastfoodfinder.model.Routing;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nhoxb on 11/11/2016.
 */
public class MapCoordination {
    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public LatLng getLocation()
    {
        return new LatLng(latitude, longitude);
    }

    @SerializedName("lat")
    Double latitude;
    @SerializedName("lng")
    Double longitude;
}
