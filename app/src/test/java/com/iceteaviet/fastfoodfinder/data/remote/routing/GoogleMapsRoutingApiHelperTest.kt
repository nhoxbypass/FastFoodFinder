package com.iceteaviet.fastfoodfinder.data.remote.routing

import org.junit.After
import org.junit.Before
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-05-29.
 */
class GoogleMapsRoutingApiHelperTest {
    private lateinit var mockAnnotations: AutoCloseable
    
    private var googleMapsRoutingApiHelper: GoogleMapsRoutingApiHelper? = null

    @Before
    fun setupPreferencesHelper() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        mockAnnotations = MockitoAnnotations.openMocks(this)

        // Get a reference to the class under test
        googleMapsRoutingApiHelper = GoogleMapsRoutingApiHelper("api_key")
    }

    @After
    fun tearDown() {
        mockAnnotations.close()
    }
}