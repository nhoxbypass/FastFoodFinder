package com.iceteaviet.fastfoodfinder.data.local.prefs

import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-05-29.
 */
class AppPreferencesHelperTest {
    private var appPreferencesHelper: AppPreferencesHelper? = null

    @Mock
    private lateinit var preferencesWrapper: AppPreferencesWrapper

    @Before
    fun setupPreferencesHelper() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        appPreferencesHelper = AppPreferencesHelper(preferencesWrapper)
    }
}