@file:JvmName("FormatUtils")

package com.iceteaviet.fastfoodfinder.utils

import java.text.DecimalFormat
import java.util.*

/**
 * Created by tom on 7/10/18.
 */

private val distanceFormat = DecimalFormat("##.## Km")
private val oneDecimalFormat = DecimalFormat("#.#")
private val twoDecimalFormat = DecimalFormat("#.##")
private val threeDecimalFormat = DecimalFormat("#.###")
private val fourDecimalFormat = DecimalFormat("#.####")

/**
 * Trim instruction
 */
fun getTrimmedShortInstruction(source: CharSequence?): CharSequence {

    if (source == null)
        return ""

    var i = 0
    var newLen = 0

    // loop back to the first non-whitespace character
    while (i < source.length - 1) {
        if (Character.isWhitespace(source[i]) && Character.isWhitespace(source[i + 1])) {
            newLen = i
            break
        }
        i++
    }

    if (newLen <= 1)
        newLen = source.length

    return source.subSequence(0, newLen)
}

/**
 * Format distance with Km unit
 */
fun formatDistance(distance: Double): String {
    return distanceFormat.format(distance)
}

/**
 * Format decimal number to specific decimal plates
 */
fun formatDecimal(decimal: Double, numbOfDecimalPlates: Int): String {
    return when (numbOfDecimalPlates) {
        1 -> oneDecimalFormat.format(decimal)

        2 -> twoDecimalFormat.format(decimal)

        3 -> threeDecimalFormat.format(decimal)

        4 -> fourDecimalFormat.format(decimal)

        else -> decimal.toString()
    }
}


/**
 * Normalize district query string
 */
fun standardizeDistrictQuery(queryString: String): List<String> {
    val result = ArrayList<String>()
    var trimmedQuery = queryString.toLowerCase().trim()

    if (trimmedQuery.isEmpty())
        return result

    if (trimmedQuery.startsWith("district"))
        trimmedQuery = trimmedQuery.substring(8, trimmedQuery.length).trim()

    if (trimmedQuery.startsWith("dist"))
        trimmedQuery = trimmedQuery.substring(4, trimmedQuery.length).trim()

    if (trimmedQuery.endsWith("district"))
        trimmedQuery = trimmedQuery.substring(0, trimmedQuery.length - 8).trim()

    if (trimmedQuery.endsWith("dist"))
        trimmedQuery = trimmedQuery.substring(0, trimmedQuery.length - 4).trim()

    if (trimmedQuery.startsWith("quan") || trimmedQuery.startsWith("quận"))
        trimmedQuery = trimmedQuery.substring(4, trimmedQuery.length).trim()

    if (trimmedQuery.startsWith("q"))
        trimmedQuery = trimmedQuery.substring(1, trimmedQuery.length).trim()

    if (trimmedQuery == "gò vấp" || trimmedQuery == "go vap" || trimmedQuery == "govap") {
        result.add("Gò Vấp")
        result.add("Go Vap")
        result.add("go vap")
        result.add("gò vấp")
    } else if (trimmedQuery == "tân bình" || trimmedQuery == "tan binh" || trimmedQuery == "tanbinh") {
        result.add("Tân Bình")
        result.add("Tan Binh")
        result.add("tan binh")
        result.add("tân bình")
    } else if (trimmedQuery == "tân phú" || trimmedQuery == "tan phu" || trimmedQuery == "tanphu") {
        result.add("Tân Phú")
        result.add("tân phú")
        result.add("Tan Phu")
        result.add("tan phu")
    } else if (trimmedQuery == "bình thạnh" || trimmedQuery == "binh thanh" || trimmedQuery == "binhthanh") {
        result.add("Bình Thạnh")
        result.add("bình thạnh")
        result.add("Binh Thanh")
        result.add("binh thanh")
    } else if (trimmedQuery == "phú nhuận" || trimmedQuery == "phu nhuan" || trimmedQuery == "phunhuan") {
        result.add("Phú Nhuận")
        result.add("phú nhuận")
        result.add("Phu Nhuan")
        result.add("phu nhuan")
    } else if (trimmedQuery == "bình chánh" || trimmedQuery == "binh chanh" || trimmedQuery == "binhchanh") {
        result.add("Bình Chánh")
        result.add("Binh Chanh")
        result.add("binh chanh")
        result.add("bình chánh")
    } else if (trimmedQuery == "thủ đức" || trimmedQuery == "thu duc" || trimmedQuery == "thuduc") {
        result.add("Thủ Đức")
        result.add("Thu Duc")
        result.add("thủ đức")
        result.add("thu duc")
    } else if (trimmedQuery == "bình tân" || trimmedQuery == "binh tan" || trimmedQuery == "binhtan") {
        result.add("Bình Tân")
        result.add("bình tân")
        result.add("Binh Tan")
        result.add("binh tan")
    } else if ((trimmedQuery >= "1" && trimmedQuery <= "9")
            || trimmedQuery == "10" || trimmedQuery == "11" || trimmedQuery == "12") {
        result.add("Quận $trimmedQuery")
        result.add("quận $trimmedQuery")
        result.add("Quan $trimmedQuery")
        result.add("quan $trimmedQuery")
        result.add("q$trimmedQuery")
        result.add("District $trimmedQuery")
        result.add("district $trimmedQuery")
    } else {
        result.add(trimmedQuery)
    }

    return result
}


/**
 * Normalize store name query string
 *
 * The query string should be lowercase & trimmed before calling this method
 */
fun getStoreTypeFromQuery(queryString: String): Int {
    val trimmedQuery = queryString.toLowerCase().trim()

    if (trimmedQuery == "circle k" || trimmedQuery == "circlek")
        return StoreType.TYPE_CIRCLE_K
    else if (trimmedQuery == "mini stop" || trimmedQuery == "ministop")
        return StoreType.TYPE_MINI_STOP
    else if (trimmedQuery == "family mart" || trimmedQuery == "familymart" || trimmedQuery == "famima")
        return StoreType.TYPE_FAMILY_MART
    else if (trimmedQuery == "shop and go" || trimmedQuery == "shopandgo"
            || trimmedQuery == "shop n go" || trimmedQuery == "shopngo")
        return StoreType.TYPE_SHOP_N_GO
    else if (trimmedQuery == "bsmart" || trimmedQuery == "b smart" || trimmedQuery == "bs mart"
            || trimmedQuery == "bmart" || trimmedQuery == "b'smart" || trimmedQuery == "b's mart"
            || trimmedQuery == "bs'mart")
        return StoreType.TYPE_BSMART

    return -1
}