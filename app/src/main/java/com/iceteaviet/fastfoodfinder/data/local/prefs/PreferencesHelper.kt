package com.iceteaviet.fastfoodfinder.data.local.prefs

/**
 * Created by tom on 7/24/18.
 */
interface PreferencesHelper {
    fun getAppLaunchFirstTime(): Boolean

    fun setAppLaunchFirstTime(isFirstTime: Boolean)

    fun getNumberOfStores(): Int

    fun setNumberOfStores(numberOfStores: Int)

    fun getSearchHistories(): MutableSet<String>

    fun setSearchHistories(set: MutableSet<String>)

    fun getIfLanguageIsVietnamese(): Boolean

    fun setIfLanguageIsVietnamese(isVietnamese: Boolean)
}
