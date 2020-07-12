package com.iceteaviet.fastfoodfinder.location.base

import com.iceteaviet.fastfoodfinder.location.LatLngAlt

/**
 * Created by tom on 2019-05-01.
 */
interface ILocationManager<L> {
    fun getCurrentLocation(): LatLngAlt?
    fun isConnected(): Boolean
    fun requestLocationUpdates()
    fun subscribeLocationUpdate(listener: L)
    fun unsubscribeLocationUpdate(listener: L)
    fun terminate()
}