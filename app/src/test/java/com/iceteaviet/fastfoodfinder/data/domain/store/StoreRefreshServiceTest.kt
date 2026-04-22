package com.iceteaviet.fastfoodfinder.data.domain.store

import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class StoreRefreshServiceTest {

    private val storeRepository: StoreRepository = mock()
    private val clientAuth: ClientAuth = mock()

    private lateinit var service: StoreRefreshService

    private val testStores = listOf(
        Store(id = 1, title = "Test", address = "Addr", lat = 10.0, lng = 106.0)
    )

    @Before
    fun setUp() {
        service = StoreRefreshService(
            storeRepository, clientAuth,
            botEmail = "bot@test.com", botPassword = "botpass"
        )
    }

    @Test
    fun refreshStoresFromRemote_refreshesDirectly_whenAlreadySignedIn() = runTest {
        whenever(clientAuth.isSignedIn()).thenReturn(true)
        whenever(storeRepository.refreshStores()).thenReturn(testStores)

        val result = service.refreshStoresFromRemote()

        assertThat(result).isEqualTo(testStores)
        verify(storeRepository).refreshStores()
        verify(clientAuth, never()).signInWithEmailAndPassword(org.mockito.kotlin.any(), org.mockito.kotlin.any())
        verify(clientAuth, never()).signOut()
    }

    @Test
    fun refreshStoresFromRemote_signsInRefreshesAndSignsOut_whenNotSignedIn() = runTest {
        whenever(clientAuth.isSignedIn()).thenReturn(false)
        whenever(storeRepository.refreshStores()).thenReturn(testStores)

        val result = service.refreshStoresFromRemote()

        assertThat(result).isEqualTo(testStores)
        verify(clientAuth).signInWithEmailAndPassword("bot@test.com", "botpass")
        verify(storeRepository).refreshStores()
        verify(clientAuth).signOut()
    }

    @Test
    fun refreshStoresFromRemote_signsOutInFinally_whenRefreshThrows() = runTest {
        whenever(clientAuth.isSignedIn()).thenReturn(false)
        whenever(storeRepository.refreshStores()).thenThrow(RuntimeException("network error"))

        try {
            service.refreshStoresFromRemote()
        } catch (_: RuntimeException) {
        }

        verify(clientAuth).signOut()
    }

    @Test
    fun refreshStoresFromRemote_doesNotSignIn_whenAlreadySignedIn() = runTest {
        whenever(clientAuth.isSignedIn()).thenReturn(true)
        whenever(storeRepository.refreshStores()).thenReturn(testStores)

        service.refreshStoresFromRemote()

        verify(clientAuth, never()).signInWithEmailAndPassword(org.mockito.kotlin.any(), org.mockito.kotlin.any())
    }
}
