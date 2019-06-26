package com.iceteaviet.fastfoodfinder.data.local.prefs

import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.eq
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


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
        MockitoAnnotations.initMocks(this)

        // Create a mocked SharedPreferences.
        mockPreferencesWrapper = createMockPreference()

        // Create a mocked SharedPreferences that fails at saving data.
        mockBrokenPreferencesWrapper = createBrokenMockPreference()
    }

    @Test
    fun sharedPreferencesWrapper_StringSet() {
        // Save the data to SharedPreferences
        mockPreferencesWrapper.setStringSet("key_set", searchHistory)

        // Read data from SharedPreferences
        val entry = mockPreferencesWrapper.getStringSet("key_set", LinkedHashSet())

        // Make sure both written and retrieved data are equal.
        assertThat(entry).isEqualTo(searchHistory)
    }

    @Test
    fun sharedPreferencesWrapper_Boolean() {
        // Save the data to SharedPreferences
        mockPreferencesWrapper.putBoolean("key_boolean", true)

        // Read data from SharedPreferences
        val entry = mockPreferencesWrapper.getBoolean("key_boolean", true)

        // Make sure both written and retrieved data are equal.
        assertThat(entry).isTrue()
    }

    @Test
    fun sharedPreferencesWrapper_String() {
        // Save the data to SharedPreferences
        mockPreferencesWrapper.putString("key_string", "test string")

        // Read data from SharedPreferences
        val entry = mockPreferencesWrapper.getString("key_string", "test string")

        // Make sure both written and retrieved data are equal.
        assertThat(entry).isEqualTo("test string")
    }

    @Test
    fun sharedPreferencesWrapper_Error() {
        Assertions.assertThatThrownBy { mockBrokenPreferencesWrapper.getString("key_string", "test string") }
                .isInstanceOf(ClassCastException::class.java)

        Assertions.assertThatThrownBy { mockBrokenPreferencesWrapper.getBoolean("key_boolean", true) }
                .isInstanceOf(ClassCastException::class.java)

        Assertions.assertThatThrownBy { mockBrokenPreferencesWrapper.getStringSet("key_set", LinkedHashSet()) }
                .isInstanceOf(ClassCastException::class.java)
    }

    /**
     * Creates a mocked Preferences.
     */
    private fun createMockPreference(): PreferencesWrapper {
        // Mocking reading the SharedPreferences as if mMockSharedPreferences was previously written
        // correctly.
        `when`(mockSharedPreferences.getString(eq("key_string"), anyString()))
                .thenReturn("test string")
        `when`(mockSharedPreferences.getBoolean(eq("key_boolean"), anyBoolean()))
                .thenReturn(true)
        `when`(mockSharedPreferences.getStringSet(eq("key_set"), anySet()))
                .thenReturn(searchHistory)

        `when`(mockEditor.putInt(eq("key_set_size"), anyInt()))
                .thenReturn(mockEditor)
        `when`(mockEditor.putString(eq("key_string"), anyString()))
                .thenReturn(mockEditor)
        `when`(mockEditor.putBoolean(eq("key_boolean"), anyBoolean()))
                .thenReturn(mockEditor)
        `when`(mockEditor.putStringSet(eq("key_set"), anySet()))
                .thenReturn(mockEditor)

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

        Mockito.`when`(mockBrokenSharedPreferences.getString(anyString(), anyString()))
                .thenThrow(ClassCastException())

        Mockito.`when`(mockBrokenSharedPreferences.getBoolean(anyString(), anyBoolean()))
                .thenThrow(ClassCastException())

        Mockito.`when`(mockBrokenSharedPreferences.getStringSet(anyString(), anySet()))
                .thenThrow(ClassCastException())

        return AppPreferencesWrapper(mockBrokenSharedPreferences)
    }

    companion object {
        private val searchHistory = linkedSetOf("circle K", "bsmart quan 8")
    }
}