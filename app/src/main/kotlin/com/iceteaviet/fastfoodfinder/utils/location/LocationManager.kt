package com.iceteaviet.fastfoodfinder.utils.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import com.iceteaviet.fastfoodfinder.App


/**
 * Created by tom on 2019-05-01.
 */
class LocationManager private constructor() : android.location.LocationListener {
    private var listeners: MutableList<LocationListener> = ArrayList()
    private var locationManager: android.location.LocationManager? = null
    private var currentLocation: Location? = null

    private var minTime: Long = MIN_TIME_BW_UPDATES
    private var minDistance: Float = MIN_DISTANCE_CHANGE_FOR_UPDATES

    init {
        locationManager = App.getContext().getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager?
        requestLocationUpdates()
    }

    fun addListener(listener: LocationListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: LocationListener) {
        listeners.remove(listener)
    }

    fun getCurrentLocation(): Location {
        return currentLocation!!
    }

    override fun onLocationChanged(location: Location) {
        for (listener in listeners) {
            listener.onLocationChanged(location)
        }

        currentLocation = location
    }

    override fun onProviderDisabled(provider: String) {
        for (listener in listeners) {
            listener.onProviderDisabled(provider)
        }
    }

    override fun onProviderEnabled(provider: String) {
        for (listener in listeners) {
            listener.onProviderEnabled(provider)
        }
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        for (listener in listeners) {
            listener.onStatusChanged(provider, status, extras)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        // Get GPS and network status
        val isGPSEnabled = locationManager!!.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager!!.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)

        if (isNetworkEnabled) {
            locationManager!!.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance, this)
            currentLocation = locationManager!!.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)
        }

        if (isGPSEnabled) {
            locationManager!!.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance, this)

            currentLocation = locationManager!!.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
        }

        if (currentLocation == null)
            currentLocation = Location(android.location.LocationManager.PASSIVE_PROVIDER)
    }

    companion object {
        const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // 10 meters
        const val MIN_TIME_BW_UPDATES = (1000 * 30).toLong() // 30 seconds

        private var instance: LocationManager? = null

        fun getInstance(): LocationManager {
            if (instance == null) {
                synchronized(LocationManager::class.java) {
                    if (instance == null) {
                        instance = LocationManager()
                    }
                }
            }
            return instance!!
        }
    }
}

