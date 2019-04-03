package com.iceteaviet.fastfoodfinder.data.prefs

/**
 * Created by tom on 7/24/18.
 */
interface PreferencesHelper {

    fun putString(key: String, value: String)

    fun getString(key: String, defaultValue: String): String

    fun putBoolean(key: String, value: Boolean)

    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    fun setStringSet(key: String, set: MutableSet<String>)

    fun getStringSet(key: String, defaultValue: MutableSet<String>): MutableSet<String>

    fun getAppLaunchFirstTime(): Boolean

    fun setAppLaunchFirstTime(isFirstTime: Boolean)

    fun getNumberOfStores(): Int

    fun setNumberOfStores(numberOfStores: Int)

    fun getSearchHistories(): MutableSet<String>

    fun setSearchHistories(set: MutableSet<String>)
}
