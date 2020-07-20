package com.iceteaviet.fastfoodfinder.location.base

import com.iceteaviet.fastfoodfinder.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.location.LocationListener

/**
 * Created by tom on 2019-05-01.
 */
interface ILocationManager {
    fun getCurrentLocation(): LatLngAlt?
    fun isConnected(): Boolean
    fun requestLocationUpdates()
    fun subscribeLocationUpdate(listener: LocationListener)
    fun unsubscribeLocationUpdate(listener: LocationListener)
    fun terminate()
}