package com.iceteaviet.fastfoodfinder.utils

import com.google.android.gms.maps.model.LatLng
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class LocationUtilsTest {
    @Test
    fun getLatLngString_normal() {
        assertThat(getLatLngString(LatLng(63.5, 103.7))).isEqualTo("63.5,103.7")
    }

    @Test
    fun getLatLngString_invalidLat() {
        assertThat(getLatLngString(LatLng(93.5, 103.7))).isEqualTo("90.0,103.7")
    }

    @Test
    fun getLatLngString_null() {
        assertThat(getLatLngString(null)).isEqualTo("")
    }

    @Test
    fun isValidLatTest() {
        assertThat(isValidLat("0.0")).isTrue()
        assertThat(isValidLat("10.2")).isTrue()
    }

    @Test
    fun isValidLatTest_invalid() {
        assertThat(isValidLat("")).isFalse()
        assertThat(isValidLat("-1")).isFalse()
        assertThat(isValidLat("91")).isFalse()
    }

    @Test
    fun isValidLngTest() {
        assertThat(isValidLng("0.0")).isTrue()
        assertThat(isValidLng("10.2")).isTrue()
    }

    @Test
    fun isValidLngTest_invalid() {
        assertThat(isValidLng("")).isFalse()
        assertThat(isValidLng("-1")).isFalse()
        assertThat(isValidLng("181")).isFalse()
    }

    @Test
    fun distanceBetweenTest() {
        assertThat(distanceBetween(LatLng(10.1234, 106.1234), LatLng(10.1101, 106.1101))).isEqualTo(2.07098)
        assertThat(distanceBetween(LatLng(10.1234, 106.1234), LatLng(10.4012, 106.4012))).isEqualTo(43.24741)
        assertThat(distanceBetween(LatLng(10.1234, 106.1234), LatLng(10.1234, 106.1234))).isEqualTo(0.0)
        assertThat(distanceBetween(LatLng(10.3214, 106.3214), LatLng(10.1234, 106.1234))).isEqualTo(30.82617)
    }
}