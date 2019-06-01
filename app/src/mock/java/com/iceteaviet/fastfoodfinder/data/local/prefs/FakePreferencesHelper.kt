package com.iceteaviet.fastfoodfinder.data.local.prefs

/**
 * Created by tom on 7/24/18.
 *
 *
 * Entry point contains methods that help interacting with application's SharedPreferences
 */

class FakePreferencesHelper : PreferencesWrapper {

    private val preferenceMap: HashMap<String, Any>

    init {
        preferenceMap = HashMap()
    }

    override fun putString(key: String, value: String) {
        preferenceMap.put(key, value)
    }

    override fun getString(key: String, defaultValue: String): String {
        val value = preferenceMap.get(key) as String?
        return value ?: defaultValue
    }

    override fun putBoolean(key: String, value: Boolean) {
        preferenceMap.put(key, value)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val value = preferenceMap.get(key) as Boolean?
        return value ?: defaultValue
    }

    override fun putInt(key: String, value: Int) {
        preferenceMap.put(key, value)
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        val value = preferenceMap.get(key) as Int?
        return value ?: defaultValue
    }

    override fun setStringSet(key: String, set: MutableSet<String>) {
        // Small hack to fix bug cannot update StringSet in SharedPreferences
        // The getStringSet() returns a reference of the stored HashSet object inside the SharedPreferences
        // @see: https://stackoverflow.com/questions/14034803/misbehavior-when-trying-to-store-a-string-set-using-sharedpreferences
        putInt(key + "_size", set.size)

        // Then store the StringSet itself
        preferenceMap.put(key, set)
    }

    override fun getStringSet(key: String, defaultValue: MutableSet<String>): MutableSet<String> {
        val value = preferenceMap.get(key) as MutableSet<String>?
        return value ?: defaultValue
    }

    companion object {
        private const val PREFS_NAME = "fastfood_finder_android"
    }
}
