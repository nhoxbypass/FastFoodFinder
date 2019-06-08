package com.iceteaviet.fastfoodfinder.data.domain.prefs

import com.iceteaviet.fastfoodfinder.data.local.prefs.PreferencesHelper
import java.util.*

/**
 * Created by tom on 7/24/18.
 *
 *
 * Entry point contains methods that help interacting with application's SharedPreferences
 */

class AppPreferencesRepository(private val preferencesHelper: PreferencesHelper) : PreferencesRepository {

    private var searchHistory: MutableSet<String> = TreeSet()

    override fun getAppLaunchFirstTime(): Boolean {
        return preferencesHelper.getAppLaunchFirstTime()
    }

    override fun setAppLaunchFirstTime(isFirstTime: Boolean) {
        preferencesHelper.setAppLaunchFirstTime(isFirstTime)
    }

    override fun getSearchHistories(): MutableSet<String> {
        if (searchHistory.isEmpty())
            searchHistory = preferencesHelper.getSearchHistories()

        return searchHistory
    }

    override fun setSearchHistories(set: MutableSet<String>) {
        preferencesHelper.setSearchHistories(searchHistory)
    }

    override fun addSearchHistories(searchContent: String) {
        if (searchHistory.isEmpty())
            searchHistory = preferencesHelper.getSearchHistories()

        if (searchHistory.contains(searchContent)) {
            // Remove old element to push the most recent search to the top of the list
            searchHistory.remove(searchContent)
        }
        searchHistory.add(searchContent)
        setSearchHistories(searchHistory)
    }

    override fun getIfLanguageIsVietnamese(): Boolean {
        return preferencesHelper.getIfLanguageIsVietnamese()
    }

    override fun setIfLanguageIsVietnamese(isVietnamese: Boolean) {
        preferencesHelper.setIfLanguageIsVietnamese(isVietnamese)
    }
}
