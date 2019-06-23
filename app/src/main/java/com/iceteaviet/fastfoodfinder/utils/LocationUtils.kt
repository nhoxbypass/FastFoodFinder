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

fun calcDistance_v2(startPosition: LatLng, endPosition: LatLng): Double {
    val pk = (180f / Math.PI).toFloat()

    val startLat = startPosition.latitude / pk
    val startLng = startPosition.longitude / pk
    val endLat = endPosition.latitude / pk
    val endLng = endPosition.longitude / pk

    val t1 = Math.cos(startLat) * Math.cos(startLng) * Math.cos(endLat) * Math.cos(endLng)
    val t2 = Math.cos(startLat) * Math.sin(startLng) * Math.cos(endLat) * Math.sin(endLng)
    val t3 = Math.sin(startLat) * Math.sin(endLat)
    val tt = Math.acos(t1 + t2 + t3)

    return 6366000 * tt / 1000.0
}

fun calcDistance_v3(startPosition: LatLng, endPosition: LatLng): Double {
    val rlat1 = Math.PI * startPosition.latitude / 180.0f
    val rlat2 = Math.PI * endPosition.latitude / 180.0f
    val rlon1 = Math.PI * startPosition.longitude / 180.0f
    val rlon2 = Math.PI * endPosition.longitude / 180.0f

    val theta = startPosition.longitude - endPosition.longitude
    val rtheta = Math.PI * theta / 180.0f

    var dist = Math.sin(rlat1) * Math.sin(rlat2) + Math.cos(rlat1) * Math.cos(rlat2) * Math.cos(rtheta)
    dist = Math.acos(dist)
    dist = dist * 180.0f / Math.PI
    dist = dist * 60.0 * 1.1515

    return dist * 1.609344f
}

fun calcDistance_v4(startPosition: LatLng, endPosition: LatLng): Double {
    // Based on http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf
    // using the "Inverse Formula" (section 4)

    var lat1 = startPosition.latitude
    var lat2 = endPosition.latitude
    var lon1 = startPosition.longitude
    var lon2 = endPosition.longitude

    val MAXITERS = 20
    // Convert lat/long to radians
    lat1 *= Math.PI / 180.0
    lat2 *= Math.PI / 180.0
    lon1 *= Math.PI / 180.0
    lon2 *= Math.PI / 180.0

    val a = 6378137.0 // WGS84 major axis
    val b = 6356752.3142 // WGS84 semi-major axis
    val f = (a - b) / a
    val aSqMinusBSqOverBSq = (a * a - b * b) / (b * b)

    val L = lon2 - lon1
    var A = 0.0
    val U1 = Math.atan((1.0 - f) * Math.tan(lat1))
    val U2 = Math.atan((1.0 - f) * Math.tan(lat2))

    val cosU1 = Math.cos(U1)
    val cosU2 = Math.cos(U2)
    val sinU1 = Math.sin(U1)
    val sinU2 = Math.sin(U2)
    val cosU1cosU2 = cosU1 * cosU2
    val sinU1sinU2 = sinU1 * sinU2

    var sigma = 0.0
    var deltaSigma = 0.0
    var cosSqAlpha = 0.0
    var cos2SM = 0.0
    var cosSigma = 0.0
    var sinSigma = 0.0
    var cosLambda = 0.0
    var sinLambda = 0.0

    var lambda = L // initial guess
    for (iter in 0 until MAXITERS) {
        val lambdaOrig = lambda
        cosLambda = Math.cos(lambda)
        sinLambda = Math.sin(lambda)
        val t1 = cosU2 * sinLambda
        val t2 = cosU1 * sinU2 - sinU1 * cosU2 * cosLambda
        val sinSqSigma = t1 * t1 + t2 * t2 // (14)
        sinSigma = Math.sqrt(sinSqSigma)
        cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda // (15)
        sigma = Math.atan2(sinSigma, cosSigma) // (16)
        val sinAlpha = if (sinSigma == 0.0)
            0.0
        else
            cosU1cosU2 * sinLambda / sinSigma // (17)
        cosSqAlpha = 1.0 - sinAlpha * sinAlpha
        cos2SM = if (cosSqAlpha == 0.0)
            0.0
        else
            cosSigma - 2.0 * sinU1sinU2 / cosSqAlpha // (18)

        val uSquared = cosSqAlpha * aSqMinusBSqOverBSq // defn
        A = 1 + uSquared / 16384.0 * // (3)
                (4096.0 + uSquared * (-768 + uSquared * (320.0 - 175.0 * uSquared)))
        val B = uSquared / 1024.0 * // (4)
                (256.0 + uSquared * (-128.0 + uSquared * (74.0 - 47.0 * uSquared)))
        val C = f / 16.0 *
                cosSqAlpha *
                (4.0 + f * (4.0 - 3.0 * cosSqAlpha)) // (10)
        val cos2SMSq = cos2SM * cos2SM
        deltaSigma = B * sinSigma * // (6)

                (cos2SM + B / 4.0 * (cosSigma * (-1.0 + 2.0 * cos2SMSq) - B / 6.0 * cos2SM *
                        (-3.0 + 4.0 * sinSigma * sinSigma) *
                        (-3.0 + 4.0 * cos2SMSq)))

        lambda = L + (1.0 - C) * f * sinAlpha *
                (sigma + C * sinSigma *
                        (cos2SM + C * cosSigma *
                                (-1.0 + 2.0 * cos2SM * cos2SM))) // (11)

        val delta = (lambda - lambdaOrig) / lambda
        if (Math.abs(delta) < 1.0e-12) {
            break
        }
    }

    val distance = (b * A * (sigma - deltaSigma))
    return distance / 1000.0
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