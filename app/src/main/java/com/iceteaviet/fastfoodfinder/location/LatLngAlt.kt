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
}