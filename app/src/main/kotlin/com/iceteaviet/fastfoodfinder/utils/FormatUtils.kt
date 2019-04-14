@file:JvmName("FormatUtils")

package com.iceteaviet.fastfoodfinder.utils

import android.text.SpannableStringBuilder
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
 * Trim all whitespace
 */
fun trimWhitespace(source: CharSequence?): CharSequence {
    if (source == null)
        return ""

    val builder = SpannableStringBuilder(source)
    var c: Char

    for (i in 0 until source.length) {
        c = source[i]
        if (Character.isWhitespace(c)) {
            try {
                if (i < source.length - 1 && Character.isWhitespace(source[i + 1]))
                //Ignore next char
                //Because it is a whitespace again
                    builder.delete(i, i + 1)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }

    if (Character.isWhitespace(builder[builder.length - 1]))
        builder.delete(builder.length - 1, builder.length)

    return builder.subSequence(0, builder.length)
}

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
 *
 * TODO: Support normalize store type query
 */
fun normalizeDistrictQuery(queryString: String): List<String> {
    val result = ArrayList<String>()
    val trimmedQuery = queryString.toLowerCase().trim { it <= ' ' }

    if (trimmedQuery == "gò vấp" || trimmedQuery == "go vap" || trimmedQuery == "govap") {
        result.add("Gò Vấp")
        result.add("Go Vap")
    } else if (trimmedQuery == "tân bình" || trimmedQuery == "tan binh" || trimmedQuery == "tanbinh") {
        result.add("Tân Bình")
        result.add("Tan Binh")
    } else if (trimmedQuery == "tân phú" || trimmedQuery == "tan phu" || trimmedQuery == "tanphu") {
        result.add("Tân Phú")
        result.add("Tan Phu")
    } else if (trimmedQuery == "bình thạnh" || trimmedQuery == "binh thanh" || trimmedQuery == "binhthanh") {
        result.add("Bình Thạnh")
        result.add("Binh Thanh")
    } else if (trimmedQuery == "phú nhuận" || trimmedQuery == "phu nhuan" || trimmedQuery == "phunhuan") {
        result.add("Phú Nhuận")
        result.add("Phu Nhuan")
    } else if (trimmedQuery == "quận 9" || trimmedQuery == "quan 9" || trimmedQuery == "q9") {
        result.add("Quận 9")
        result.add("Quan 9")
        result.add("District 9")
    } else if (trimmedQuery == "quận 1" || trimmedQuery == "quan 1" || trimmedQuery == "q1") {
        result.add("Quận 1")
        result.add("Quan 1")
        result.add("District 1")
    } else if (trimmedQuery == "quận 2" || trimmedQuery == "quan 2" || trimmedQuery == "q2") {
        result.add("Quận 2")
        result.add("Quan 2")
        result.add("District 2")
    } else if (trimmedQuery == "quận 3" || trimmedQuery == "quan 3" || trimmedQuery == "q3") {
        result.add("Quận 3")
        result.add("Quan 3")
        result.add("District 3")
    } else if (trimmedQuery == "quận 4" || trimmedQuery == "quan 4" || trimmedQuery == "q4") {
        result.add("Quận 4")
        result.add("Quan 4")
        result.add("District 4")
    } else if (trimmedQuery == "quận 5" || trimmedQuery == "quan 5" || trimmedQuery == "q5") {
        result.add("Quận 5")
        result.add("Quan 5")
        result.add("District 5")
    } else if (trimmedQuery == "quận 6" || trimmedQuery == "quan 6" || trimmedQuery == "q6") {
        result.add("Quận 6")
        result.add("Quan 6")
        result.add("District 6")
    } else if (trimmedQuery == "quận 7" || trimmedQuery == "quan 7" || trimmedQuery == "q7") {
        result.add("Quận 7")
        result.add("Quan 7")
        result.add("District 7")
    } else if (trimmedQuery == "quận 8" || trimmedQuery == "quan 8" || trimmedQuery == "q8") {
        result.add("Quận 8")
        result.add("Quan 8")
        result.add("District 8")
    } else if (trimmedQuery == "quận 10" || trimmedQuery == "quan 10" || trimmedQuery == "q10") {
        result.add("Quận 10")
        result.add("Quan 10")
        result.add("District 10")
    } else if (trimmedQuery == "quận 11" || trimmedQuery == "quan 11" || trimmedQuery == "q11") {
        result.add("Quận 11")
        result.add("Quan 11")
        result.add("District 11")
    } else if (trimmedQuery == "quận 12" || trimmedQuery == "quan 12" || trimmedQuery == "q12") {
        result.add("Quận 12")
        result.add("Quan 12")
        result.add("District 12")
    } else {
        result.add(trimmedQuery)
    }

    return result
}