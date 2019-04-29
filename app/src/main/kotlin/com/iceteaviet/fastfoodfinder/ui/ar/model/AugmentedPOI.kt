package com.iceteaviet.fastfoodfinder.ui.ar.model

import android.location.Location

import androidx.annotation.DrawableRes

/**
 * Created by Genius Doan on 20/07/2017.
 */

class AugmentedPOI(val name: String, lat: Double, lon: Double, altitude: Double, @param:DrawableRes @field:DrawableRes
val icon: Int) {
    val location: Location

    init {
        location = Location(name)
        location.latitude = lat
        location.longitude = lon
        location.altitude = altitude
    }
}
