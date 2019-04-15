package com.iceteaviet.fastfoodfinder.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class DataUtilsTest {
    @Test
    fun isValidUserUid_true() {
        assertEquals(true, isValidUserUid("abcxyz"))
        assertEquals(true, isValidUserUid("1abc5xyz"))
    }

    @Test
    fun isValidUserUid_false() {
        assertEquals(false, isValidUserUid(""))
        assertEquals(false, isValidUserUid("null"))
    }

    @Test
    fun isValidEmail_true() {
        assertEquals(true, isValidEmail("abcxyz@gmail.com"))
        assertEquals(true, isValidEmail("1abc5xyz@yahoo.com.vn"))
        assertEquals(true, isValidEmail("a@iceteaviet.com"))
    }

    @Test
    fun isValidEmail_false() {
        assertEquals(false, isValidEmail(""))
        assertEquals(false, isValidEmail("null"))
        assertEquals(false, isValidEmail("abczyx@gmail"))
        assertEquals(false, isValidEmail("abcxyc@"))
        assertEquals(false, isValidEmail("@gmail.com"))
    }

    @Test
    fun isValidPassword_true() {
        assertEquals(true, isValidPassword("somepassword"))
        assertEquals(true, isValidPassword("Str0n9passwOrd$"))
        assertEquals(true, isValidPassword("a@iceteaviet.com"))
    }

    @Test
    fun isValidPassword_false() {
        assertEquals(false, isValidPassword(""))
        assertEquals(false, isValidPassword("null"))
        assertEquals(false, isValidPassword("1234567"))
        assertEquals(false, isValidPassword("1"))
    }
}