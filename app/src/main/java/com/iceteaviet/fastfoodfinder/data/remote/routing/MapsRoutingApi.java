package com.iceteaviet.fastfoodfinder.data.remote.routing;

import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Genius Doan on 11/11/2016.
 */
public interface MapsRoutingApi {
    @GET("json")
    Call<MapsDirection> getDirection(@QueryMap Map<String, String> options);
}
