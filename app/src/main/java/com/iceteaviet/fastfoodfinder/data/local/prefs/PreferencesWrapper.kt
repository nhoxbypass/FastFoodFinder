package com.iceteaviet.fastfoodfinder.data.local.prefs

/**
 * Created by tom on 7/24/18.
 */
interface PreferencesWrapper {
    fun putString(key: String, value: String)

    fun getString(key: String, defaultValue: String): String

    fun putBoolean(key: String, value: Boolean)

    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    fun putInt(key: String, value: Int)

    fun getInt(key: String, defaultValue: Int): Int

    fun setStringSet(key: String, set: MutableSet<String>)

    fun getStringSet(key: String, defaultValue: MutableSet<String>): MutableSet<String>
}
