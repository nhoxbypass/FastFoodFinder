package com.iceteaviet.fastfoodfinder.data

import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.routing.MapsRoutingRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import io.reactivex.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class AppDataManagerTest {

    @Mock
    private lateinit var storeRepository: StoreRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var clientAuth: ClientAuth

    @Mock
    private lateinit var mapsRoutingRepository: MapsRoutingRepository

    @Mock
    private lateinit var preferencesRepository: PreferencesRepository

    @Mock
    private lateinit var user: User

    private lateinit var dataManager: AppDataManager

    @Before
    fun setup() {
        dataManager = AppDataManager(
            storeRepository,
            userRepository,
            clientAuth,
            mapsRoutingRepository,
            preferencesRepository
        )
    }

    @Test
    fun `getCurrentUserUid returns currentUser uid if available`() {
        Mockito.`when`(user.getUid()).thenReturn("123")
        dataManager.updateCurrentUser(user)

        val result = dataManager.getCurrentUserUid()

        assertEquals("123", result)
    }

    @Test
    fun `getCurrentUserUid falls back to clientAuth when currentUser is null`() {
        Mockito.`when`(clientAuth.getCurrentUserUid()).thenReturn("456")

        val result = dataManager.getCurrentUserUid()

        assertEquals("456", result)
    }

    @Test
    fun `loadStoresFromServer returns store list when signed in`() {
        val mockStores = listOf(mock(Store::class.java))
        Mockito.`when`(clientAuth.isSignedIn()).thenReturn(true)
        Mockito.`when`(storeRepository.getAllStores()).thenReturn(Single.just(mockStores))

        val testObserver = dataManager.loadStoresFromServer().test()

        testObserver.assertValue(mockStores)
        testObserver.assertNoErrors()
    }

    @Test
    fun `signInWithEmailAndPassword delegates to clientAuth`() {
        val email = "test@example.com"
        val password = "password"
        val mockUser = mock(User::class.java)
        Mockito.`when`(clientAuth.signInWithEmailAndPassword(email, password)).thenReturn(Single.just(mockUser))

        val testObserver = dataManager.signInWithEmailAndPassword(email, password).test()

        testObserver.assertValue(mockUser)
    }

    @Test
    fun `getAppLaunchFirstTime returns expected value`() {
        Mockito.`when`(preferencesRepository.getAppLaunchFirstTime()).thenReturn(true)
        assertTrue(dataManager.getAppLaunchFirstTime())
    }
}