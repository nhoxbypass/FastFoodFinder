package com.iceteaviet.fastfoodfinder.data.local.prefs

import androidx.annotation.VisibleForTesting

/**
 * Created by tom on 7/24/18.
 *
 *
 * Entry point contains methods that help interacting with application's SharedPreferences
 */

class AppPreferencesHelper(private val preferences: PreferencesWrapper) : PreferencesHelper {

    override fun getAppLaunchFirstTime(): Boolean {
        return preferences.getBoolean(KEY_APP_LAUNCH_FIRST_TIME, true)
    }

    override fun setAppLaunchFirstTime(isFirstTime: Boolean) {
        preferences.putBoolean(KEY_APP_LAUNCH_FIRST_TIME, isFirstTime)
    }

    override fun getSearchHistories(): MutableSet<String> {
        return preferences.getStringSet(KEY_SEARCH_HISTORIES, LinkedHashSet())
    }

    override fun setSearchHistories(set: MutableSet<String>) {
        preferences.setStringSet(KEY_SEARCH_HISTORIES, set)
    }

    override fun getIfLanguageIsVietnamese(): Boolean {
        return preferences.getBoolean(KEY_LANGUAGE, false)
    }

    override fun setIfLanguageIsVietnamese(isVietnamese: Boolean) {
        preferences.putBoolean(KEY_LANGUAGE, isVietnamese)
    }

    override fun getLastKnownLocation(): Pair<Double, Double>? {
        val latStr = preferences.getString(KEY_LAST_KNOWN_LAT, "")
        val lngStr = preferences.getString(KEY_LAST_KNOWN_LNG, "")
        if (latStr.isEmpty() || lngStr.isEmpty()) return null
        return try {
            Pair(latStr.toDouble(), lngStr.toDouble())
        } catch (e: NumberFormatException) {
            null
        }
    }

    override fun setLastKnownLocation(lat: Double, lng: Double) {
        preferences.putString(KEY_LAST_KNOWN_LAT, lat.toString())
        preferences.putString(KEY_LAST_KNOWN_LNG, lng.toString())
    }

    companion object {
        @VisibleForTesting
        const val KEY_APP_LAUNCH_FIRST_TIME = "app_launch_first_time"

        @VisibleForTesting
        const val KEY_SEARCH_HISTORIES = "search_histories"

        @VisibleForTesting
        const val KEY_SEARCH_HISTORIES_SIZE = "search_histories_size"

        @VisibleForTesting
        const val KEY_LANGUAGE = "lang"

        @VisibleForTesting
        const val KEY_LAST_KNOWN_LAT = "last_known_lat"

        @VisibleForTesting
        const val KEY_LAST_KNOWN_LNG = "last_known_lng"
    }
}
