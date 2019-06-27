package com.iceteaviet.fastfoodfinder.location

/**
 * Created by tom on 2019-06-10.
 */
class LatLngAlt {
    var latitude: Double
    var longitude: Double
    var altitude: Double

    constructor(lat: Double, lng: Double, alt: Double) {
        if (-180.0 <= lng && lng < 180.0) {
            this.longitude = lng
        } else {
            this.longitude = ((lng - 180.0) % 360.0 + 360.0) % 360.0 - 180.0
        }

        this.latitude = Math.max(-90.0, Math.min(90.0, lat))
        this.altitude = alt
    }

    override fun equals(other: Any?): Boolean {
        return if (other is LatLngAlt) {
            latitude == other.latitude && longitude == other.longitude && altitude == other.altitude
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + altitude.hashCode()
        return result
    }
}