package com.iceteaviet.fastfoodfinder.rest;

import com.iceteaviet.fastfoodfinder.model.Routing.MapsDirection;
import com.iceteaviet.fastfoodfinder.utils.RetrofitUtils;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by tamdoan on 13/07/2017.
 */

public class RestClient {
    public static String MAP_BASE_URL = "https://maps.googleapis.com/maps/api/directions/";
    public String googlemapBrowserKey = "";
    private MapsDirectionApi mMapDirectionApi;
    private static RestClient mInstance = null;

    private RestClient() {
        mMapDirectionApi = RetrofitUtils.get(googlemapBrowserKey, MAP_BASE_URL).create(MapsDirectionApi.class);
    }

    public static RestClient getInstance() {
        if (mInstance != null)
            mInstance = new RestClient();

        return mInstance;
    }

    public void getDirection(Map<String, String> queries, Callback<MapsDirection> callback) {
        mMapDirectionApi.getDirection(queries).enqueue(callback);
    }

    public void setGooglemapBrowserKey(String key) {
        if (key != null)
            this.googlemapBrowserKey = key;
    }
}
