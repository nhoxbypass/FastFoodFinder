package com.iceteaviet.fastfoodfinder.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Base64

class StringUtilsTest {
    @Test
    fun isEmpty_true() {
        assertEquals(true, isEmpty(""))
    }

    @Test
    fun isEmpty_nullString() {
        assertEquals(true, isEmpty(null))
    }

    @Test
    fun isEmpty_false() {
        assertEquals(false, isEmpty("abc"))
    }

    @Test
    fun `bytesToBase64 then base64ToBytes returns original byte array`() {
        val originalBytes = byteArrayOf(1, 2, 3, 4, 5, 127, -128)
        val base64 = bytesToBase64(originalBytes)
        val decoded = base64ToBytes(base64)

        assertThat(decoded).isEqualTo(originalBytes)
    }

    @Test
    fun `base64ToBytes then bytesToBase64 returns original base64 string`() {
        val originalBytes = byteArrayOf(10, 20, 30, 40, 50, 60, 70)
        val base64 = Base64.getEncoder().encodeToString(originalBytes)
        val decoded = base64ToBytes(base64)
        val encodedAgain = bytesToBase64(decoded)

        assertThat(encodedAgain).isEqualTo(base64)
    }

    @Test
    fun `base64 encode and decode empty byte array`() {
        val emptyBytes = byteArrayOf()
        val base64 = bytesToBase64(emptyBytes)
        val decoded = base64ToBytes(base64)

        assertThat(decoded).isEqualTo(emptyBytes)
    }
}