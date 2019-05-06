package com.iceteaviet.fastfoodfinder.location.base

import android.location.Location

/**
 * Created by tom on 2019-05-01.
 */
interface ILocationManager<L> {
    fun getCurrentLocation(): Location?
    fun subscribeLocationUpdate(listener: L)
    fun unsubscribeLocationUpdate(listener: L)
    fun terminate()
}