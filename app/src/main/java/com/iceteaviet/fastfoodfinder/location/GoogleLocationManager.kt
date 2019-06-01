package com.iceteaviet.fastfoodfinder.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.location.base.AbsLocationManager
import com.iceteaviet.fastfoodfinder.location.base.ILocationManager


/**
 * Created by tom on 2019-05-01.
 */
class GoogleLocationManager private constructor() : AbsLocationManager<LocationListener>(), ILocationManager<LocationListener>, com.google.android.gms.location.LocationListener, GoogleApiClient.ConnectionCallbacks {

    private var locationRequest: LocationRequest? = null
    private var googleApiClient: GoogleApiClient? = null

    override fun initLocationProvider() {
        locationRequest = createLocationRequest()
        googleApiClient = createGoogleApiClient(App.getContext())
        googleApiClient?.connect()
    }

    @SuppressLint("MissingPermission")
    override fun getLastLocation(): Location? {
        return LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
    }

    @SuppressLint("MissingPermission")
    override fun requestLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)
    }

    override fun terminate() {
        super.terminate()
        googleApiClient?.disconnect()
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
    }

    override fun onConnected(extras: Bundle?) {
        currLocation = getLastLocation()
        requestLocationUpdates()
    }

    override fun onConnectionSuspended(i: Int) {
        onFailed(FailType.GOOGLE_PLAY_SERVICES_CONNECTION_FAIL)
    }

    override fun onLocationChanged(location: Location) {
        for (listener in listeners) {
            listener.onLocationChanged(location)
        }

        currLocation = location
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
                .addConnectionCallbacks(this)
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

        private var instance: GoogleLocationManager? = null

        fun getInstance(): GoogleLocationManager {
            if (instance == null) {
                synchronized(GoogleLocationManager::class.java) {
                    if (instance == null) {
                        instance = GoogleLocationManager()
                    }
                }
            }
            return instance!!
        }
    }
}

