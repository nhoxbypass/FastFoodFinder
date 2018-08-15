package com.iceteaviet.fastfoodfinder.data.remote.routing

import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * Created by Genius Doan on 11/11/2016.
 */
interface MapsRoutingApi {
    @GET("json")
    fun getDirection(@QueryMap options: Map<String, String>): Call<MapsDirection>
}
