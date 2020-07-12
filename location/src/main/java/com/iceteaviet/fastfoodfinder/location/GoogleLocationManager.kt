package com.iceteaviet.fastfoodfinder.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.iceteaviet.fastfoodfinder.location.base.AbsLocationManager
import com.iceteaviet.fastfoodfinder.location.base.ILocationManager


/**
 * Created by tom on 2019-05-01.
 */
open class GoogleLocationManager private constructor(context: Context)
    : AbsLocationManager(context), ILocationManager {

    private var locationRequest: LocationRequest? = null
    private var googleApiClient: GoogleApiClient? = null

    /**
     * Will be trigger by Google Fused Location Api & manual when Location Api service connected
     */
    private val googleLocationListener = com.google.android.gms.location.LocationListener { location ->
        currLocation = location
        for (listener in listeners) {
            listener.onLocationChanged(LatLngAlt(location.latitude, location.longitude, location.altitude))
        }
    }

    private val clientConnectionCallbacks = object : GoogleApiClient.ConnectionCallbacks {
        override fun onConnected(extras: Bundle?) {
            connected = true
            currLocation = getLastLocation()
            currLocation?.let {
                googleLocationListener.onLocationChanged(it)
            }
        }

        override fun onConnectionSuspended(i: Int) {
            onFailed(FailType.GOOGLE_PLAY_SERVICES_CONNECTION_FAIL)
            connected = false
        }
    }

    override fun initLocationProvider(context: Context) {
        locationRequest = createLocationRequest()
        googleApiClient = createGoogleApiClient(context)
        googleApiClient?.connect()
    }

    @SuppressLint("MissingPermission")
    override fun getLastLocation(): Location? {
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
    }

    @SuppressLint("MissingPermission")
    override fun requestLocationUpdates() {
        if (isConnected() && !isRequestingLocationUpdate())
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, googleLocationListener)
    }

    override fun terminate() {
        super.terminate()
        googleApiClient?.disconnect()
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, googleLocationListener)
    }


    /**
     * Create location request with high accuracy
     */
    private fun createLocationRequest(): LocationRequest {
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = INTERVAL
        mLocationRequest.fastestInterval = FASTEST_INTERVAL
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        return mLocationRequest
    }

    private fun createGoogleApiClient(context: Context): GoogleApiClient {
        return GoogleApiClient.Builder(context)
                .addConnectionCallbacks(clientConnectionCallbacks)
                .addOnConnectionFailedListener {
                    onFailed(FailType.GOOGLE_PLAY_SERVICES_CONNECTION_FAIL)
                }
                .addApi(LocationServices.API)
                .build()
    }

    private fun onFailed(failType: Int) {
        for (listener in listeners) {
            listener.onLocationFailed(failType)
        }
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
                        if (!Companion::appContext.isInitialized)
                            throw IllegalStateException("Call `GoogleLocationManager.init(Context)` before calling this method.")
                        else
                            instance = GoogleLocationManager(appContext)
                    }
                }
            }
            return instance!!
        }
    }
}

