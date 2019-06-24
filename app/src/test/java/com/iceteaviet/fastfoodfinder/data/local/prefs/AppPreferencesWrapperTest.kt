package com.iceteaviet.fastfoodfinder.data.local.prefs

import android.content.SharedPreferences
import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito.`when`


/**
 * Created by tom on 2019-06-24.
 */
class AppPreferencesWrapperTest {
    private lateinit var mockPreferencesWrapper: PreferencesWrapper

    private lateinit var mockBrokenPreferencesWrapper: PreferencesWrapper

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockBrokenSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    @Mock
    private lateinit var mockBrokenEditor: SharedPreferences.Editor

    @Before
    fun initMocks() {
        // Create a mocked SharedPreferences.
        mockPreferencesWrapper = createMockPreference()

        // Create a mocked SharedPreferences that fails at saving data.
        mockBrokenPreferencesWrapper = createBrokenMockPreference()
    }

    /**
     * Creates a mocked Preferences.
     */
    private fun createMockPreference(): PreferencesWrapper {
        // Mocking reading the SharedPreferences as if mMockSharedPreferences was previously written
        // correctly.
        /*`when`(mockSharedPreferences.getString(eq(SharedPreferencesHelper.KEY_NAME), anyString()))
                .thenReturn(mSharedPreferenceEntry.getName())
        `when`(mockSharedPreferences.getString(eq(SharedPreferencesHelper.KEY_EMAIL), anyString()))
                .thenReturn(mSharedPreferenceEntry.getEmail())
        `when`(mockSharedPreferences.getLong(eq(SharedPreferencesHelper.KEY_DOB), anyLong()))
                .thenReturn(mSharedPreferenceEntry.getDateOfBirth().getTimeInMillis())*/

        // Mocking a successful commit.
        `when`(mockEditor.commit()).thenReturn(true)

        // Return the MockEditor when requesting it.
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        return AppPreferencesWrapper(mockSharedPreferences)
    }

    /**
     * Creates a mocked SharedPreferences that fails when writing.
     */
    private fun createBrokenMockPreference(): PreferencesWrapper {
        // Mocking a commit that fails.
        `when`(mockBrokenEditor.commit()).thenReturn(false)

        // Return the broken MockEditor when requesting it.
        `when`(mockBrokenSharedPreferences.edit()).thenReturn(mockBrokenEditor)
        return AppPreferencesWrapper(mockBrokenSharedPreferences)
    }
}