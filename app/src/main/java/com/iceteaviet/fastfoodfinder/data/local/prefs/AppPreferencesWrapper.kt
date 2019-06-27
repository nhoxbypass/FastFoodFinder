package com.iceteaviet.fastfoodfinder.data.local.prefs

import android.content.SharedPreferences

/**
 * Created by tom on 7/24/18.
 *
 *
 * Entry point contains methods that help interacting with application's SharedPreferences
 */

class AppPreferencesWrapper(private val sharedPreferences: SharedPreferences) : PreferencesWrapper {

    override fun putString(key: String, value: String) {
        sharedPreferences.edit()
                .putString(key, value)
                .apply()
    }

    override fun getString(key: String, defaultValue: String): String {
        val value = sharedPreferences.getString(key, defaultValue)
        return value ?: defaultValue
    }

    override fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit()
                .putBoolean(key, value)
                .apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    override fun putInt(key: String, value: Int) {
        sharedPreferences.edit()
                .putInt(key, value)
                .apply()
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    override fun setStringSet(key: String, set: MutableSet<String>) {
        // Small hack to fix bug cannot update StringSet in SharedPreferences
        // The getStringSet() returns a reference of the stored HashSet object inside the SharedPreferences
        // @see: https://stackoverflow.com/questions/14034803/misbehavior-when-trying-to-store-a-string-set-using-sharedpreferences
        putInt(key + "_size", set.size)

        // Then store the StringSet itself
        sharedPreferences.edit()
                .putStringSet(key, set)
                .apply()
    }

    override fun getStringSet(key: String, defaultValue: MutableSet<String>): MutableSet<String> {
        val value = sharedPreferences.getStringSet(key, defaultValue)
        return value ?: defaultValue
    }

    companion object {
        public const val PREFS_NAME = "fastfood_finder_android"
    }
}
