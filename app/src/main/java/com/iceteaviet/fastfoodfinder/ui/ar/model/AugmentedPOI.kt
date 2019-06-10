package com.iceteaviet.fastfoodfinder.ui.ar.model

import androidx.annotation.DrawableRes
import com.iceteaviet.fastfoodfinder.location.LatLngAlt

/**
 * Created by Genius Doan on 20/07/2017.
 */

class AugmentedPOI(val name: String, lat: Double, lon: Double, alt: Double, @param:DrawableRes @field:DrawableRes
val icon: Int) {
    val location: LatLngAlt

    init {
        location = LatLngAlt(lat, lon, alt)
    }
}
