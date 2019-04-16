package com.iceteaviet.fastfoodfinder.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeUtilsTest {
    @Test
    fun getRelativeTimeAgo_justNow() {
        val ts = System.currentTimeMillis()

        assertEquals("just now", getRelativeTimeAgo(ts, ts))
        assertEquals("just now", getRelativeTimeAgo(ts - 1, ts))
        assertEquals("just now", getRelativeTimeAgo(ts - 59000, ts))
    }

    @Test
    fun getRelativeTimeAgo_minute() {
        val ts = System.currentTimeMillis()

        assertEquals("1m", getRelativeTimeAgo(ts - 60000, ts))
        assertEquals("2m", getRelativeTimeAgo(ts - 123000, ts))
        assertEquals("10m", getRelativeTimeAgo(ts - 10 * 60000, ts))
        assertEquals("59m", getRelativeTimeAgo(ts - 59 * 60000, ts))
    }

    @Test
    fun getRelativeTimeAgo_hour() {
        val ts = System.currentTimeMillis()

        assertEquals("1h", getRelativeTimeAgo(ts - 60 * 60000, ts))
        assertEquals("2h", getRelativeTimeAgo(ts - 123 * 60000, ts))
        assertEquals("10h", getRelativeTimeAgo(ts - 10 * 60 * 60000, ts))
        assertEquals("23h", getRelativeTimeAgo(ts - 23 * 60 * 60000, ts))
    }


    @Test
    fun getRelativeTimeAgo_day() {
        val ts = System.currentTimeMillis()

        assertEquals("yesterday", getRelativeTimeAgo(ts - 24 * 60 * 60000, ts))
        assertEquals("2d", getRelativeTimeAgo(ts - 50 * 60 * 60000, ts))
        assertEquals("6d", getRelativeTimeAgo(ts - 6 * 24 * 60 * 60000, ts))
        assertEquals("14 Apr", getRelativeTimeAgo(1555245461987, 1555245461987 + 8 * 24 * 60 * 60000))
    }

    @Test
    fun isEmpty_invalidInput() {
        val ts = System.currentTimeMillis()

        assertEquals("", getRelativeTimeAgo(-1, -1))
        assertEquals("", getRelativeTimeAgo(-1, ts))
        assertEquals("", getRelativeTimeAgo(ts, -1))
        assertEquals("", getRelativeTimeAgo(ts + 1, ts))
    }
}