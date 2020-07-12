package com.iceteaviet.fastfoodfinder.ui.ar.model

import androidx.annotation.DrawableRes
import com.iceteaviet.fastfoodfinder.location.LatLngAlt

/**
 * Created by Genius Doan on 20/07/2017.
 */

class AugmentedPOI(val name: String, lat: Double, lon: Double, alt: Double, @param:DrawableRes @field:DrawableRes val icon: Int) {
    private val location: com.iceteaviet.fastfoodfinder.location.LatLngAlt

    init {
        location = com.iceteaviet.fastfoodfinder.location.LatLngAlt(lat, lon, alt)
    }

    fun getLocation(): com.iceteaviet.fastfoodfinder.location.LatLngAlt {
        return location
    }

    override fun equals(other: Any?): Boolean {
        return if (other is AugmentedPOI) {
            name.equals(other.name) && location.equals(other.location)
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + icon
        result = 31 * result + location.hashCode()
        return result
    }
}
