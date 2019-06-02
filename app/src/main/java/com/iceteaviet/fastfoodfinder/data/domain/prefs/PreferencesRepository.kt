package com.iceteaviet.fastfoodfinder.data.domain.prefs

/**
 * Created by tom on 7/24/18.
 *
 * Main entry point for accessing preferences data.
 */
interface PreferencesRepository {
    fun getAppLaunchFirstTime(): Boolean

    fun setAppLaunchFirstTime(isFirstTime: Boolean)

    fun getNumberOfStores(): Int

    fun setNumberOfStores(numberOfStores: Int)

    fun getSearchHistories(): MutableSet<String>

    fun setSearchHistories(set: MutableSet<String>)

    fun addSearchHistories(searchContent: String)

    fun getIfLanguageIsVietnamese(): Boolean

    fun setIfLanguageIsVietnamese(isVietnamese: Boolean)
}
