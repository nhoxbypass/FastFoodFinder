package com.iceteaviet.fastfoodfinder.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class RetrofitUtilsTest {
    @Test
    fun get_baseUrl() {
        assertEquals("https://maps.googleapis.com/maps/api/directions/", get("some_api_key", "https://maps.googleapis.com/maps/api/directions/").baseUrl().toString())
    }

    @Test
    fun get_baseUrl_hostName() {
        assertEquals("maps.googleapis.com", get("some_api_key", "https://maps.googleapis.com/maps/api/directions/").baseUrl().host())
    }

    @Test
    fun get_baseUrl_path() {
        assertEquals("/maps/api/directions/", get("some_api_key", "https://maps.googleapis.com/maps/api/directions/").baseUrl().encodedPath())
    }

    @Test
    fun get_baseUrl_nullQuery() {
        assertEquals(null, get("some_api_key", "https://maps.googleapis.com/maps/api/directions/").baseUrl().query())
    }

    @Test
    fun get_baseUrl_https() {
        assertEquals(true, get("some_api_key", "https://maps.googleapis.com/maps/api/directions/").baseUrl().isHttps)
    }
}