package com.iceteaviet.fastfoodfinder.utils

import com.iceteaviet.fastfoodfinder.App
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RetrofitUtilsTest {
    @Before
    fun setup() {
        App.PACKAGE_NAME = "com.iceteaviet.fastfoodfinder"
        App.SHA1 = "11:22:50:33:44:a8:55:66:6a:77:88:e9:99:00:2c:5e:5a:5d:78:a0"
    }

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