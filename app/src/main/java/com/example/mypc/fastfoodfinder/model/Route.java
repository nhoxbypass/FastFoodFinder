package com.example.mypc.fastfoodfinder.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by nhoxb on 11/11/2016.
 */
public class Route {
    public List<Leg> getLegList() {
        return legList;
    }

    public String getSummary() {
        return summary;
    }

    @SerializedName("legs")
    List<Leg> legList;
    @SerializedName("summary")
    String summary;
}
