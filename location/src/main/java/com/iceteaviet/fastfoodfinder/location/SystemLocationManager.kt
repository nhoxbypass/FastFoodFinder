package com.iceteaviet.fastfoodfinder.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import com.iceteaviet.fastfoodfinder.androidext.getLocationManager
import com.iceteaviet.fastfoodfinder.location.base.AbsLocationManager
import com.iceteaviet.fastfoodfinder.location.base.ILocationManager


/**
 * Created by tom on 2019-05-01.
 */
open class SystemLocationManager private constructor(context: Context)
    : AbsLocationManager(context), ILocationManager {

    private var locationManager: LocationManager? = null

    private var minTime: Long = MIN_TIME_BW_UPDATES
    private var minDistance: Float = MIN_DISTANCE_CHANGE_FOR_UPDATES

    private val androidLocationListener = object : android.location.LocationListener {
        override fun onLocationChanged(location: Location) {
            for (listener in listeners) {
                listener.onLocationChanged(LatLngAlt(location.latitude, location.longitude, location.altitude))
            }

            currLocation = location
        }

        override fun onProviderDisabled(provider: String) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        }
    }

    init {
        currLocation = getLastLocation()
        connected = true
        requestLocationUpdates()
    }

    override fun initLocationProvider(context: Context) {
        locationManager = context.getLocationManager()
    }

    @SuppressLint("MissingPermission")
    override fun getLastLocation(): Location? {
        // Get GPS and network status
        var location: Location? = null
        val isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (isNetworkEnabled) {
            location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }

        if (isGPSEnabled) {
            location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }

        if (location == null)
            location = Location(LocationManager.PASSIVE_PROVIDER)

        return location
    }

    @SuppressLint("MissingPermission")
    override fun requestLocationUpdates() {
        if (!isConnected() || isRequestingLocationUpdate())
            return

        // Get GPS and network status
        val isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (isNetworkEnabled) {
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance, androidLocationListener)
        }

        if (isGPSEnabled) {
            locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    minTime,
                    minDistance, androidLocationListener)
        }
    }

    companion object {
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // 10 meters
        private const val MIN_TIME_BW_UPDATES = (1000 * 30).toLong() // 30 seconds

        private lateinit var appContext: Context

        private var instance: SystemLocationManager? = null

        fun init(context: Context) {
            appContext = context.applicationContext
        }

        fun getInstance(): SystemLocationManager {
            if (instance == null) {
                synchronized(SystemLocationManager::class.java) {
                    if (instance == null) {
                        if (!Companion::appContext.isInitialized)
                            throw IllegalStateException("Call `SystemLocationManager.init(Context)` before calling this method.")
                        else
                            instance = SystemLocationManager(appContext)
                    }
                }
            }
            return instance!!
        }
    }
}

