@file:JvmName("TimeUtils")

package com.iceteaviet.fastfoodfinder.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Created by nhoxbypass on 14/04/2019.
 */

/**
 * Get relative time in the past of given timestamp
 *
 * TODO: Support localize, search for other better solution
 */
fun getRelativeTimeAgo(ts: Long): String {
    return getRelativeTimeAgo(ts, System.currentTimeMillis())
}

fun getRelativeTimeAgo(ts: Long, currentTs: Long): String {
    if (ts < 0 || currentTs < 0)
        return ""

    if (ts > currentTs)
        return ""

    val twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy"
    val date = Date(ts)
    val diff = currentTs - ts
    val diffMinutes = diff / (60 * 1000)
    val diffHours = diff / (60 * 60 * 1000)
    val diffDays = diff / (24 * 60 * 60 * 1000)
    if (diffMinutes <= 0) {
        return "just now"
    } else if (diffMinutes < 60) {
        return diffMinutes.toString() + "m"
    } else if (diffHours < 24) {
        return diffHours.toString() + "h"
    } else if (diffDays < 7) {
        if (diffDays == 1L)
            return "yesterday"
        return diffDays.toString() + "d"
    } else {
        var sf = SimpleDateFormat(twitterFormat, Locale.ENGLISH)
        sf.isLenient = true
        sf = SimpleDateFormat("dd MMM", Locale.ENGLISH)
        return sf.format(date)
    }
}