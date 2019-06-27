package com.iceteaviet.fastfoodfinder.location

import android.os.Bundle

/**
 * Created by tom on 2019-05-01.
 */
interface SystemLocationListener : LocationListener {
    /**
     * This method will be invoked if only you use android.location.SystemLocationManager
     * with GPS or Network Providers to receive location
     */
    fun onStatusChanged(provider: String, status: Int, extras: Bundle)

    /**
     * This method will be invoked if only you use android.location.SystemLocationManager
     * with GPS or Network Providers to receive location
     */
    fun onProviderEnabled(provider: String)

    /**
     * This method will be invoked if only you use android.location.SystemLocationManager
     * with GPS or Network Providers to receive location
     */
    fun onProviderDisabled(provider: String)
}