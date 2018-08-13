@file:JvmName("LocationUtils")

package com.iceteaviet.fastfoodfinder.utils

import android.location.Location

import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.model.LatLng

/**
 * Created by Genius Doan on 20/07/2017.
 */

private val WGS84_A = 6378137.0                  // WGS 84 semi-major axis constant in meters
private val WGS84_E2 = 0.00669437999014          // square of WGS 84 eccentricity

fun createLocationRequest(): LocationRequest {
    val mLocationRequest = LocationRequest()
    mLocationRequest.interval = Constant.MAPS_INTERVAL
    mLocationRequest.fastestInterval = Constant.MAPS_FASTEST_INTERVAL
    mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

    return mLocationRequest
}

fun getLatLngString(latLng: LatLng?): String? {
    return if (latLng != null)
        latLng.latitude.toString() + "," + latLng.longitude.toString()
    else
        null
}

fun calcDistance(startPosition: LatLng, endPosition: LatLng): Double {
    //
    val start = Location("pointA")
    start.latitude = startPosition.latitude
    start.longitude = startPosition.longitude
    val end = Location("pointB")
    end.latitude = endPosition.latitude
    end.longitude = endPosition.longitude

    return start.distanceTo(end) / 1000.0
}

fun convertWSG84toECEF(location: Location): FloatArray {
    val radLat = Math.toRadians(location.latitude)
    val radLon = Math.toRadians(location.longitude)

    val clat = Math.cos(radLat).toFloat()
    val slat = Math.sin(radLat).toFloat()
    val clon = Math.cos(radLon).toFloat()
    val slon = Math.sin(radLon).toFloat()

    val N = (WGS84_A / Math.sqrt(1.0 - WGS84_E2 * slat.toDouble() * slat.toDouble())).toFloat()

    val x = ((N + location.altitude) * clat.toDouble() * clon.toDouble()).toFloat()
    val y = ((N + location.altitude) * clat.toDouble() * slon.toDouble()).toFloat()
    val z = ((N * (1.0 - WGS84_E2) + location.altitude) * slat).toFloat()

    return floatArrayOf(x, y, z)
}

fun convertECEFtoENU(currentLocation: Location, ecefCurrentLocation: FloatArray, ecefPOI: FloatArray): FloatArray {
    val radLat = Math.toRadians(currentLocation.latitude)
    val radLon = Math.toRadians(currentLocation.longitude)

    val clat = Math.cos(radLat).toFloat()
    val slat = Math.sin(radLat).toFloat()
    val clon = Math.cos(radLon).toFloat()
    val slon = Math.sin(radLon).toFloat()

    val dx = ecefCurrentLocation[0] - ecefPOI[0]
    val dy = ecefCurrentLocation[1] - ecefPOI[1]
    val dz = ecefCurrentLocation[2] - ecefPOI[2]

    val east = -slon * dx + clon * dy

    val north = -slat * clon * dx - slat * slon * dy + clat * dz

    val up = clat * clon * dx + clat * slon * dy + slat * dz

    return floatArrayOf(east, north, up, 1f)
}