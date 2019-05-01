package com.iceteaviet.fastfoodfinder.utils.location

import android.location.Location
import android.os.Bundle


/**
 * Created by tom on 2019-05-01.
 */
interface LocationListener {
    /**
     * This method will be invoked whenever new location update received
     */
    fun onLocationChanged(location: Location)

    /**
     * When it is not possible to receive location, such as no active provider or no permission etc.
     * It will pass an integer value from [FailType]
     * which will help you to determine how did it fail to receive location
     */
    fun onLocationFailed(@FailType type: Int)

    /**
     * This method will be invoked if only you use android.location.LocationManager
     * with GPS or Network Providers to receive location
     */
    fun onStatusChanged(provider: String, status: Int, extras: Bundle)

    /**
     * This method will be invoked if only you use android.location.LocationManager
     * with GPS or Network Providers to receive location
     */
    fun onProviderEnabled(provider: String)

    /**
     * This method will be invoked if only you use android.location.LocationManager
     * with GPS or Network Providers to receive location
     */
    fun onProviderDisabled(provider: String)

}