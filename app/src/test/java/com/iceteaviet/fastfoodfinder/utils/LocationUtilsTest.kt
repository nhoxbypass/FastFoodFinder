package com.iceteaviet.fastfoodfinder.utils

import com.google.android.gms.maps.model.LatLng
import org.junit.Assert.assertEquals
import org.junit.Test

class LocationUtilsTest {
    @Test
    fun getLatLngString_normal() {
        assertEquals("63.5,103.7", getLatLngString(LatLng(63.5, 103.7)))
    }

    @Test
    fun getLatLngString_invalidLat() {
        assertEquals("90.0,103.7", getLatLngString(LatLng(93.5, 103.7)))
    }

    @Test
    fun getLatLngString_null() {
        assertEquals("", getLatLngString(null))
    }
}