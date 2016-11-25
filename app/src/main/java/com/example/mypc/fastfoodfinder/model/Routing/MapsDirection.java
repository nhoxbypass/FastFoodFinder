package com.example.mypc.fastfoodfinder.model.Routing;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nhoxb on 11/11/2016.
 */
public class MapsDirection {
    public List<Route> getRouteList() {
        return routeList;
    }

    @SerializedName("routes")
    List<Route> routeList;
}
