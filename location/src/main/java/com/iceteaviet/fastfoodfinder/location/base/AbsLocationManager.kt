package com.iceteaviet.fastfoodfinder.location.base

import android.content.Context
import android.location.Location
import com.iceteaviet.fastfoodfinder.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.location.LocationListener

/**
 * Created by tom on 2019-05-01.
 */
abstract class AbsLocationManager protected constructor(context: Context) : ILocationManager {
    protected var listeners: MutableList<LocationListener> = ArrayList()
    protected var currLocation: Location? = null

    protected var connected = false
    protected var requestingLocationUpdate = false

    init {
        this.initLocationProvider(context)
    }

    protected abstract fun initLocationProvider(context: Context)

    protected abstract fun getLastLocation(): Location?

    override fun getCurrentLocation(): LatLngAlt? {
        if (currLocation == null)
            currLocation = getLastLocation()

        currLocation?.let {
            return LatLngAlt(it.latitude, it.longitude, it.altitude)
        }

        return null
    }

    override fun isConnected(): Boolean {
        return connected
    }

    override fun subscribeLocationUpdate(listener: LocationListener) {
        listeners.add(listener)
    }

    override fun unsubscribeLocationUpdate(listener: LocationListener) {
        listeners.remove(listener)
    }

    override fun terminate() {
        listeners.clear()
    }

    protected fun isRequestingLocationUpdate(): Boolean {
        return requestingLocationUpdate
    }
}