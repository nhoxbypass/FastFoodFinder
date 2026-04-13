package com.iceteaviet.fastfoodfinder.data.local.db.user

import org.junit.After
import org.junit.Before
import org.mockito.MockitoAnnotations

class UserDAOTest {
    private lateinit var mockAnnotations: AutoCloseable

    private var userDao: UserDao? = null

    @Before
    fun setupPreferencesHelper() {
        mockAnnotations = MockitoAnnotations.openMocks(this)
        userDao = org.mockito.Mockito.mock(UserDao::class.java)
    }

    @After
    fun tearDown() {
        mockAnnotations.close()
    }
}
