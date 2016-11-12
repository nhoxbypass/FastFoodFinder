package com.example.mypc.fastfoodfinder.rest;

import com.example.mypc.fastfoodfinder.model.MapsDirection;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by nhoxb on 11/11/2016.
 */
public interface MapsDirectionApi {
    @GET("json")
    Call<MapsDirection> getDirection(@QueryMap Map<String, String> options);
}
