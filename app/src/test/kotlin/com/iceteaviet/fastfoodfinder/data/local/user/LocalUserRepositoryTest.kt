package com.iceteaviet.fastfoodfinder.data.local.user

import org.junit.Before
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-05-29.
 */
class LocalUserRepositoryTest {
    private var localUserRepository: LocalUserRepository? = null

    @Before
    fun setupPreferencesHelper() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        localUserRepository = LocalUserRepository()
    }
}