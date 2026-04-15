package com.iceteaviet.fastfoodfinder.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.iceteaviet.fastfoodfinder.core.location.base.AbsLocationManager
import com.iceteaviet.fastfoodfinder.core.location.base.ILocationManager


/**
 * Created by tom on 2019-05-01.
 *
 * Uses the modern FusedLocationProviderClient API (non-blocking).
 * The deprecated GoogleApiClient + FusedLocationApi pattern caused ANRs because
 * FusedLocationApi.getLastLocation() blocked the main thread internally via CountDownLatch.
 */
open class GoogleLocationManager private constructor(context: Context) : AbsLocationManager(context), ILocationManager {

    private var locationRequest: LocationRequest? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return
            currLocation = location
            for (listener in listeners) {
                listener.onLocationChanged(LatLngAlt(location.latitude, location.longitude, location.altitude))
            }
        }
    }

    init {
        initLocationProvider(context)
    }

    override fun initLocationProvider(context: Context) {
        locationRequest = createLocationRequest()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // FusedLocationProviderClient is ready immediately — no connection step needed.
        // Fetch last known location asynchronously via a Task callback (non-blocking).
        connected = true
        fetchLastLocationAsync()
    }

    /**
     * Fetches the last known location asynchronously without blocking the main thread.
     * When the result is available, it notifies all registered listeners.
     */
    @SuppressLint("MissingPermission")
    private fun fetchLastLocationAsync() {
        fusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
            if (location != null) {
                currLocation = location
                for (listener in listeners) {
                    listener.onLocationChanged(LatLngAlt(location.latitude, location.longitude, location.altitude))
                }
            }
        }
    }

    /**
     * Returns the last cached location synchronously (no network call, no blocking).
     * Returns null if no location has been received yet.
     */
    override fun getLastLocation(): Location? {
        return currLocation
    }

    @SuppressLint("MissingPermission")
    override fun requestLocationUpdates() {
        if (!isConnected() || isRequestingLocationUpdate()) return

        fusedLocationClient?.requestLocationUpdates(
            locationRequest!!,
            locationCallback,
            Looper.getMainLooper()
        )
        requestingLocationUpdate = true
    }

    override fun terminate() {
        super.terminate()
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        requestingLocationUpdate = false
    }

    /**
     * Create location request with high accuracy
     */
    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL)
            .setMinUpdateIntervalMillis(FASTEST_INTERVAL)
            .build()
    }

    companion object {
        private const val INTERVAL = (1000 * 10).toLong()
        private const val FASTEST_INTERVAL = (1000 * 5).toLong()

        private lateinit var appContext: Context

        private var instance: GoogleLocationManager? = null

        fun init(context: Context) {
            appContext = context.applicationContext
        }

        fun getInstance(): GoogleLocationManager {
            if (instance == null) {
                synchronized(GoogleLocationManager::class.java) {
                    if (instance == null) {
                        if (!::appContext.isInitialized) {
                            throw IllegalStateException("Call `GoogleLocationManager.init(Context)` before calling this method.")
                        } else {
                            instance = GoogleLocationManager(appContext)
                        }
                    }
                }
            }
            return instance!!
        }
    }
}
