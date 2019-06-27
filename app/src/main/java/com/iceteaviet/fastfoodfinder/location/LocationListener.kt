package com.iceteaviet.fastfoodfinder.location


/**
 * Created by tom on 2019-05-01.
 */
interface LocationListener {
    /**
     * This method will be invoked whenever new location update received
     */
    fun onLocationChanged(location: LatLngAlt)

    /**
     * When it is not possible to receive location, such as no active provider or no permission etc.
     * It will pass an integer value from [FailType]
     * which will help you to determine how did it fail to receive location
     */
    fun onLocationFailed(@FailType type: Int)
}