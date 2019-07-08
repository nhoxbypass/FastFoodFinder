package com.iceteaviet.fastfoodfinder.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.junit.Test

class FormatUtilsTest {
    @Test
    fun getTrimmedShortInstruction_normal() {
        assertThat(getTrimmedShortInstruction("some text")).isEqualTo("some text")
        assertThat(getTrimmedShortInstruction("  some text")).isEqualTo("  some text")
        assertThat(getTrimmedShortInstruction("some text  ")).isEqualTo("some text")
    }

    @Test
    fun getTrimmedShortInstruction_empty() {
        assertThat(getTrimmedShortInstruction("")).isEmpty()
    }

    @Test
    fun getTrimmedShortInstruction_null() {
        assertThat(getTrimmedShortInstruction(null)).isEmpty()
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
        assertEquals("-2 Km", formatDistance(-2.0))
        assertEquals("-2.3 Km", formatDistance(-2.3))
    }

    @Test
    fun formatDistance_zero() {
        assertEquals("0 Km", formatDistance(0.0))
    }

    @Test
    fun normalizeDistrictQuery_randomText() {
        assertThat(standardizeDistrictQuery(" some text  ")).contains("some text")
        assertThat(standardizeDistrictQuery("some text")).contains("some text")
        assertThat(standardizeDistrictQuery("some address, phuong x, quan y, Saigon")).contains("some address, phuong x, quan y, saigon")
    }

    @Test
    fun normalizeDistrictQuery_vi() {
        assertThat(standardizeDistrictQuery("quan 1")).contains("quận 1", "Quận 1", "quan 1", "Quan 1", "q1", "District 1", "district 1")
        assertThat(standardizeDistrictQuery("quan 5 ")).contains("quận 5", "Quận 5", "quan 5", "Quan 5", "q5", "District 5", "district 5")
        assertThat(standardizeDistrictQuery(" q 3 ")).contains("quận 3", "Quận 3", "quan 3", "Quan 3", "q3", "District 3", "district 3")
        assertThat(standardizeDistrictQuery("quận 10 ")).contains("quận 10", "Quận 10", "quan 10", "Quan 10", "q10", "District 10", "district 10")

        assertThat(standardizeDistrictQuery(" quan tan binh  ")).contains("Tân Bình", "tân bình", "Tan Binh", "tan binh")
        assertThat(standardizeDistrictQuery(" tanbinh  ")).contains("Tân Bình", "tân bình", "Tan Binh", "tan binh")
        assertThat(standardizeDistrictQuery(" q tân bình")).contains("Tân Bình", "tân bình", "Tan Binh", "tan binh")

        assertThat(standardizeDistrictQuery("tân phú")).contains("Tân Phú", "tân phú", "Tan Phu", "tan phu")
        assertThat(standardizeDistrictQuery("q tan phu")).contains("Tân Phú", "tân phú", "Tan Phu", "tan phu")
        assertThat(standardizeDistrictQuery("quan tanphu")).contains("Tân Phú", "tân phú", "Tan Phu", "tan phu")

        assertThat(standardizeDistrictQuery("quận gò vấp")).contains("gò vấp", "Gò Vấp", "go vap", "Go Vap")
        assertThat(standardizeDistrictQuery(" quan go vap")).contains("gò vấp", "Gò Vấp", "go vap", "Go Vap")
        assertThat(standardizeDistrictQuery("quan govap ")).contains("gò vấp", "Gò Vấp", "go vap", "Go Vap")

        assertThat(standardizeDistrictQuery("quan 3")).contains("quận 3", "Quận 3", "quan 3", "Quan 3", "q3", "District 3", "district 3")
        assertThat(standardizeDistrictQuery("quan 5")).contains("quận 5", "Quận 5", "quan 5", "Quan 5", "q5", "District 5", "district 5")
    }

    @Test
    fun normalizeDistrictQuery_en() {
        assertThat(standardizeDistrictQuery("district 8")).contains("quận 8", "Quận 8", "quan 8", "Quan 8", "q8", "District 8", "district 8")
        assertThat(standardizeDistrictQuery("dist 11  ")).contains("quận 11", "Quận 11", "quan 11", "Quan 11", "q11", "District 11", "district 11")
        assertThat(standardizeDistrictQuery(" dist 12")).contains("quận 12", "Quận 12", "quan 12", "Quan 12", "q12", "District 12", "district 12")

        assertThat(standardizeDistrictQuery(" district Binh Chanh  ")).contains("Bình Chánh", "bình chánh", "Binh Chanh", "binh chanh")
        assertThat(standardizeDistrictQuery(" binh chanh")).contains("Bình Chánh", "bình chánh", "Binh Chanh", "binh chanh")
        assertThat(standardizeDistrictQuery(" binhchanh ")).contains("Bình Chánh", "bình chánh", "Binh Chanh", "binh chanh")

        assertThat(standardizeDistrictQuery("binh Tan district ")).contains("Bình Tân", "bình tân", "Binh Tan", "binh tan")
        assertThat(standardizeDistrictQuery("binhtan district ")).contains("Bình Tân", "bình tân", "Binh Tan", "binh tan")
        assertThat(standardizeDistrictQuery("Binh tan  ")).contains("Bình Tân", "bình tân", "Binh Tan", "binh tan")

        assertThat(standardizeDistrictQuery("district thu duc")).contains("Thủ Đức", "thủ đức", "Thu Duc", "thu duc")
        assertThat(standardizeDistrictQuery(" thuduc")).contains("Thủ Đức", "thủ đức", "Thu Duc", "thu duc")

        assertThat(standardizeDistrictQuery("Binh thanh dist")).contains("Bình Thạnh", "bình thạnh", "Binh Thanh", "binh thanh")
        assertThat(standardizeDistrictQuery(" binhthanh dist")).contains("Bình Thạnh", "bình thạnh", "Binh Thanh", "binh thanh")

        assertThat(standardizeDistrictQuery("q phunhuan")).contains("Phú Nhuận", "phú nhuận", "Phu Nhuan", "phu nhuan")
        assertThat(standardizeDistrictQuery("q phu nhuan")).contains("Phú Nhuận", "phú nhuận", "Phu Nhuan", "phu nhuan")
        assertThat(standardizeDistrictQuery("q Phu Nhuan  ")).contains("Phú Nhuận", "phú nhuận", "Phu Nhuan", "phu nhuan")
    }

    @Test
    fun normalizeDistrictQuery_empty() {
        assertThat(standardizeDistrictQuery("")).isEmpty()
    }

    @Test
    fun getStoreTypeFromQuery_normal() {
        assertEquals(StoreType.TYPE_CIRCLE_K, getStoreTypeFromQuery("circle K"))
        assertEquals(StoreType.TYPE_CIRCLE_K, getStoreTypeFromQuery("circle K  "))
        assertEquals(StoreType.TYPE_CIRCLE_K, getStoreTypeFromQuery(" circleK"))

        assertEquals(StoreType.TYPE_BSMART, getStoreTypeFromQuery("bmart"))
        assertEquals(StoreType.TYPE_BSMART, getStoreTypeFromQuery("bsmart"))
        assertEquals(StoreType.TYPE_BSMART, getStoreTypeFromQuery("bs'mart"))
        assertEquals(StoreType.TYPE_BSMART, getStoreTypeFromQuery("b'smart"))
        assertEquals(StoreType.TYPE_BSMART, getStoreTypeFromQuery("b's mart"))
        assertEquals(StoreType.TYPE_BSMART, getStoreTypeFromQuery("b smart"))
        assertEquals(StoreType.TYPE_BSMART, getStoreTypeFromQuery("bs mart"))

        assertEquals(StoreType.TYPE_SHOP_N_GO, getStoreTypeFromQuery("shopngo"))
        assertEquals(StoreType.TYPE_SHOP_N_GO, getStoreTypeFromQuery(" shop n go"))
        assertEquals(StoreType.TYPE_SHOP_N_GO, getStoreTypeFromQuery("shopandgo"))
        assertEquals(StoreType.TYPE_SHOP_N_GO, getStoreTypeFromQuery(" shop and go  "))

        assertEquals(StoreType.TYPE_FAMILY_MART, getStoreTypeFromQuery("familymart"))
        assertEquals(StoreType.TYPE_FAMILY_MART, getStoreTypeFromQuery("famima"))
        assertEquals(StoreType.TYPE_FAMILY_MART, getStoreTypeFromQuery("family mart"))
        assertEquals(StoreType.TYPE_FAMILY_MART, getStoreTypeFromQuery(" family mart  "))

        assertEquals(StoreType.TYPE_MINI_STOP, getStoreTypeFromQuery("ministop"))
        assertEquals(StoreType.TYPE_MINI_STOP, getStoreTypeFromQuery("mini stop"))
    }

    @Test
    fun getStoreTypeFromQuery_empty() {
        assertEquals(-1, getStoreTypeFromQuery(""))
    }

    @Test
    fun formatDecimalTest() {
        assertEquals(formatDecimal(1.2345, 1), "1.2")
        assertEquals(formatDecimal(1.2345, 2), "1.23")
        assertEquals(formatDecimal(1.2345, 3), "1.234")
        assertEquals(formatDecimal(1.2345, 4), "1.2345")
        assertEquals(formatDecimal(1.2345, 5), "1.2345")
        assertEquals(formatDecimal(1.234567, 5), "1.234567")
    }

    @Test
    fun roundToDecimalsTest() {
        assertThat(roundToDecimals(2.0709774176400737, 5)).isEqualTo(2.07098)
        assertThat(roundToDecimals(2.0709774176400737, 9)).isEqualTo(2.070977418)
        assertThat(roundToDecimals(43.24740945911383, 3)).isEqualTo(43.247)
        assertThat(roundToDecimals(0.0, 5)).isEqualTo(0.0)
        assertThat(roundToDecimals(30.826165963297818, 6)).isEqualTo(30.826166)
    }
}