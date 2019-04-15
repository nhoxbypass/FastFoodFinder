package com.iceteaviet.fastfoodfinder.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatUtilsTest {
    @Test
    fun getTrimmedShortInstruction_normal() {
        assertEquals("some text", getTrimmedShortInstruction("some text"))
    }

    @Test
    fun getTrimmedShortInstruction_empty() {
        assertEquals("", getTrimmedShortInstruction(""))
    }

    @Test
    fun getTrimmedShortInstruction_null() {
        assertEquals("", getTrimmedShortInstruction(null))
    }

    @Test
    fun formatDistance_floating() {
        assertEquals("5.5 Km", formatDistance(5.5))
    }

    @Test
    fun formatDistance_normal() {
        assertEquals("7 Km", formatDistance(7.0))
    }

    @Test
    fun formatDistance_negativeNumb() {
        assertEquals("-1 Km", formatDistance(-1.0))
        assertEquals("-2.3 Km", formatDistance(-2.3))
    }

    @Test
    fun formatDistance_zero() {
        assertEquals("0 Km", formatDistance(0.0))
    }

    @Test
    fun normalizeDistrictQuery_randomText() {
        assertEquals(" some text  ", normalizeDistrictQuery(" some text  "))
        assertEquals("some text", normalizeDistrictQuery("some text"))
        assertEquals("some address, phuong x, quan y, Saigon", normalizeDistrictQuery("some address, phuong x, quan y, Saigon"))
    }

    @Test
    fun normalizeDistrictQuery_district() {
        assertEquals("quan 6", normalizeDistrictQuery("quan 6"))
        assertEquals("quan 5", normalizeDistrictQuery("quan Nam "))
        assertEquals("quan tan binh", normalizeDistrictQuery(" quan tan binh  "))
        assertEquals("quan tan binh", normalizeDistrictQuery("tân bình"))
        assertEquals("quan tan binh", normalizeDistrictQuery("quận tân bình"))
        assertEquals("quan 6", normalizeDistrictQuery("quận 6"))
        assertEquals("quan 5", normalizeDistrictQuery("quận năm"))

        assertEquals("quan 6", normalizeDistrictQuery("district 6"))
        assertEquals("quan 5", normalizeDistrictQuery("5 district"))
        assertEquals("quan tan binh", normalizeDistrictQuery(" district Tan Binh  "))
        assertEquals("quan tan binh", normalizeDistrictQuery("tân Bình district "))
        assertEquals("quan tan binh", normalizeDistrictQuery("district Tân bình"))
    }

    @Test
    fun normalizeDistrictQuery_storeType() {
        assertEquals("circle k", normalizeDistrictQuery("circle K"))
        assertEquals("circle k", normalizeDistrictQuery("circle K  "))
        assertEquals("circle k", normalizeDistrictQuery(" circleK"))

        assertEquals("bsmart", normalizeDistrictQuery("bmart"))
        assertEquals("bsmart", normalizeDistrictQuery("bs'mart"))
        assertEquals("bsmart", normalizeDistrictQuery("b's mart"))

        assertEquals("shopngo", normalizeDistrictQuery("shopngo"))
        assertEquals("shopngo", normalizeDistrictQuery(" shop n go"))
        assertEquals("shopngo", normalizeDistrictQuery("shopandgo"))
        assertEquals("shopngo", normalizeDistrictQuery(" shop and go  "))

        assertEquals("familymart", normalizeDistrictQuery("familymart"))
        assertEquals("familymart", normalizeDistrictQuery("famima"))
        assertEquals("familymart", normalizeDistrictQuery("family mart"))
        assertEquals("familymart", normalizeDistrictQuery(" family mart  "))

        assertEquals("ministop", normalizeDistrictQuery("ministop"))
        assertEquals("ministop", normalizeDistrictQuery("mini stop"))
    }

    @Test
    fun normalizeDistrictQuery_empty() {
        assertEquals("", normalizeDistrictQuery(""))
    }
}