package com.iceteaviet.fastfoodfinder.data.local.db.store

import org.junit.After
import org.junit.Before
import org.mockito.MockitoAnnotations

class StoreDAOTest {
    private lateinit var mockAnnotations: AutoCloseable

    private var storeDao: StoreDao? = null

    @Before
    fun setupPreferencesHelper() {
        mockAnnotations = MockitoAnnotations.openMocks(this)
        storeDao = org.mockito.Mockito.mock(StoreDao::class.java)
    }

    @After
    fun tearDown() {
        mockAnnotations.close()
    }
}
