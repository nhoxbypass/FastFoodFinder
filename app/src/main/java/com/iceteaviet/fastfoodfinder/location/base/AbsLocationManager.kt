package com.iceteaviet.fastfoodfinder.location.base

import android.content.Context
import android.location.Location
import com.iceteaviet.fastfoodfinder.location.LocationListener

/**
 * Created by tom on 2019-05-01.
 */
abstract class AbsLocationManager<T : LocationListener> protected constructor(context: Context) : ILocationManager<T> {
    protected var listeners: MutableList<T> = ArrayList()
    protected var currLocation: Location? = null

    protected var connected = false
    protected var requestingLocationUpdate = false

    init {
        this.initLocationProvider(context)
    }

    protected abstract fun initLocationProvider(context: Context)

    protected abstract fun getLastLocation(): Location?

    override fun getCurrentLocation(): Location? {
        return currLocation
    }

    override fun isConnected(): Boolean {
        return connected
    }

    override fun subscribeLocationUpdate(listener: T) {
        listeners.add(listener)
    }

    override fun unsubscribeLocationUpdate(listener: T) {
        listeners.remove(listener)
    }

    override fun terminate() {
        listeners.clear()
    }

    protected fun isRequestingLocationUpdate(): Boolean {
        return requestingLocationUpdate
    }
}