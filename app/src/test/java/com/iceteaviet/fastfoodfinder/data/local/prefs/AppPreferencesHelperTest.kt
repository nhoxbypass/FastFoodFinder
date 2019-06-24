package com.iceteaviet.fastfoodfinder.data.local.prefs

import com.nhaarman.mockitokotlin2.eq
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations


/**
 * Created by tom on 2019-05-29.
 */
class AppPreferencesHelperTest {
    private lateinit var mockPreferencesHelper: PreferencesHelper

    private lateinit var mockBrokenPreferencesHelper: PreferencesHelper

    @Mock
    private lateinit var mockPreferencesWrapper: PreferencesWrapper

    @Mock
    private lateinit var mockBrokenPreferencesWrapper: PreferencesWrapper

    @Before
    fun initMocks() {
        MockitoAnnotations.initMocks(this)

        // Create a mocked SharedPreferences.
        mockPreferencesHelper = createMockPreferenceHelper()

        // Create a mocked SharedPreferences that fails at saving data.
        mockBrokenPreferencesHelper = createBrokenMockPreferenceHelper()
    }

    @Test
    fun sharedPreferencesHelper_SaveAndReadAppLaunchFirstTime() {
        // Save the data to SharedPreferences
        mockPreferencesHelper.setAppLaunchFirstTime(true)

        // Read data from SharedPreferences
        val entry = mockPreferencesHelper.getAppLaunchFirstTime()

        // Make sure both written and retrieved data are equal.
        assertThat(entry).isTrue()
    }

    @Test
    fun sharedPreferencesHelper_SaveAppLaunchFirstTimeFailed() {
        // Read personal information from a broken SharedPreferencesHelper
        assertThatThrownBy { mockBrokenPreferencesHelper.setAppLaunchFirstTime(true) }
                .isInstanceOf(ClassCastException::class.java)
    }

    @Test
    fun sharedPreferencesHelper_GetAppLaunchFirstTimeFailed() {
        // Read personal information from a broken SharedPreferencesHelper
        assertThatThrownBy { mockBrokenPreferencesHelper.getAppLaunchFirstTime() }
                .isInstanceOf(ClassCastException::class.java)
    }

    @Test
    fun sharedPreferencesHelper_SaveAndReadIfLanguageIsVietnamese() {
        // Save the data to SharedPreferences
        mockPreferencesHelper.setIfLanguageIsVietnamese(true)

        // Read data from SharedPreferences
        val entry = mockPreferencesHelper.getIfLanguageIsVietnamese()

        // Make sure both written and retrieved data are equal.
        assertThat(entry).isTrue()
    }

    @Test
    fun sharedPreferencesHelper_SaveIfLanguageIsVietnamese() {
        // Read personal information from a broken SharedPreferencesHelper
        assertThatThrownBy { mockBrokenPreferencesHelper.setIfLanguageIsVietnamese(true) }
                .isInstanceOf(ClassCastException::class.java)
    }

    @Test
    fun sharedPreferencesHelper_GetIfLanguageIsVietnamese() {
        // Read personal information from a broken SharedPreferencesHelper
        assertThatThrownBy { mockBrokenPreferencesHelper.getIfLanguageIsVietnamese() }
                .isInstanceOf(ClassCastException::class.java)
    }

    @Test
    fun sharedPreferencesHelper_SaveAndReadSearchHistories_empty() {
        // Save the data to SharedPreferences
        mockPreferencesHelper.setSearchHistories(LinkedHashSet())

        // Read data from SharedPreferences
        val entry = mockPreferencesHelper.getSearchHistories()

        // Make sure both written and retrieved data are equal.
        assertThat(entry).isEmpty()
    }

    @Test
    fun sharedPreferencesHelper_SaveSearchHistories() {
        // Read personal information from a broken SharedPreferencesHelper
        assertThatThrownBy { mockBrokenPreferencesHelper.setSearchHistories(LinkedHashSet()) }
                .isInstanceOf(ClassCastException::class.java)
    }

    @Test
    fun sharedPreferencesHelper_GetSearchHistories() {
        // Read personal information from a broken SharedPreferencesHelper
        assertThatThrownBy { mockBrokenPreferencesHelper.getSearchHistories() }
                .isInstanceOf(ClassCastException::class.java)
    }

    /**
     * Creates a mocked Preferences.
     */
    private fun createMockPreferenceHelper(): PreferencesHelper {
        // Mocking reading the SharedPreferences as if mMockSharedPreferences was previously written
        // correctly.
        Mockito.`when`(mockPreferencesWrapper.getBoolean(eq(AppPreferencesHelper.KEY_APP_LAUNCH_FIRST_TIME), ArgumentMatchers.anyBoolean()))
                .thenReturn(true)
        Mockito.`when`(mockPreferencesWrapper.getStringSet(eq(AppPreferencesHelper.KEY_SEARCH_HISTORIES), ArgumentMatchers.anySet()))
                .thenReturn(LinkedHashSet())
        Mockito.`when`(mockPreferencesWrapper.getBoolean(eq(AppPreferencesHelper.KEY_LANGUAGE), ArgumentMatchers.anyBoolean()))
                .thenReturn(true)

        return AppPreferencesHelper(mockPreferencesWrapper)
    }

    /**
     * Creates a mocked SharedPreferences that fails when writing.
     */
    private fun createBrokenMockPreferenceHelper(): PreferencesHelper {
        // Mocking a get data that fails.
        Mockito.`when`(mockBrokenPreferencesWrapper.getBoolean(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean()))
                .thenThrow(ClassCastException())

        Mockito.`when`(mockBrokenPreferencesWrapper.putBoolean(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean()))
                .thenThrow(ClassCastException())

        Mockito.`when`(mockBrokenPreferencesWrapper.getStringSet(ArgumentMatchers.anyString(), ArgumentMatchers.anySet()))
                .thenThrow(ClassCastException())

        Mockito.`when`(mockBrokenPreferencesWrapper.setStringSet(ArgumentMatchers.anyString(), ArgumentMatchers.anySet()))
                .thenThrow(ClassCastException())

        return AppPreferencesHelper(mockBrokenPreferencesWrapper)
    }
}