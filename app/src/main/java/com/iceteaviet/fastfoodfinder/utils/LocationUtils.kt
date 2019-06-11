@file:JvmName("LocationUtils")

package com.iceteaviet.fastfoodfinder.utils

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.iceteaviet.fastfoodfinder.location.LatLngAlt

/**
 * Created by Genius Doan on 20/07/2017.
 */

private const val WGS84_A = 6378137.0                  // WGS 84 semi-major axis constant in meters
private const val WGS84_E2 = 0.00669437999014          // square of WGS 84 eccentricity

/**
 * Format LatLng string
 */
fun getLatLngString(latLng: LatLng?): String {
    return if (latLng != null)
        latLng.latitude.toString() + "," + latLng.longitude.toString()
    else
        ""
}

/**
 * Calculate distance between
 */
fun calcDistance(startPosition: LatLng, endPosition: LatLng): Double {
    val start = Location("pointA")
    start.latitude = startPosition.latitude
    start.longitude = startPosition.longitude
    val end = Location("pointB")
    end.latitude = endPosition.latitude
    end.longitude = endPosition.longitude

    return start.distanceTo(end) / 1000.0
}

fun isValidLocation(latLng: LatLng?): Boolean {
    if (latLng == null)
        return false

    return !latLng.latitude.equals(0.0) || !latLng.longitude.equals(0.0)
}

fun isValidLat(lat: String): Boolean {
    if (lat.isBlank())
        return false

    val latD = lat.toDoubleOrNull()
    return latD != null && (latD in 0.0..90.0)
}

fun isValidLng(lng: String): Boolean {
    if (lng.isBlank())
        return false

    val lngD = lng.toDoubleOrNull()
    return lngD != null && (lngD in 0.0..180.0)
}

/**
 * Convert WSG84 to ECEF location array
 */
fun convertWSG84toECEF(location: LatLngAlt): FloatArray {
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

/**
 * Convert ECEF to ENU location array
 */
fun convertECEFtoENU(currentLocation: LatLngAlt, ecefCurrentLocation: FloatArray, ecefPOI: FloatArray): FloatArray {
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