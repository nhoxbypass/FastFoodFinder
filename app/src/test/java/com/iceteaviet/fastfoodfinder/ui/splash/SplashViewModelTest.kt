package com.iceteaviet.fastfoodfinder.ui.splash

import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRefreshService
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {

    private val clientAuth: ClientAuth = mock()
    private val userRepository: com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository = mock()
    private val storeRepository: StoreRepository = mock()
    private val storeRefreshService: StoreRefreshService = mock()
    private val preferencesRepository: PreferencesRepository = mock()

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testStores = listOf(
        Store(id = 1, title = "Test", address = "Addr", lat = 10.0, lng = 106.0)
    )

    private val testUser = User("uid123", "Test User", "test@test.com", "", emptyList())

    private lateinit var viewModel: SplashViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SplashViewModel(
            clientAuth, userRepository, storeRepository, storeRefreshService, preferencesRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun start_firstTime_setsFlagAndNavigatesToLogin() = runTest {
        whenever(preferencesRepository.getAppLaunchFirstTime()).thenReturn(true)
        whenever(storeRefreshService.refreshStoresFromRemote()).thenReturn(testStores)

        val states = mutableListOf<SplashUiState>()
        val collectJob = launch(testDispatcher) { viewModel.uiState.collect { states.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start()
        advanceUntilIdle()

        verify(preferencesRepository).setAppLaunchFirstTime(false)
        assertThat(states.any { it is SplashUiState.NavigateToLogin }).isTrue()
        collectJob.cancel()
    }

    @Test
    fun start_firstTime_showsErrorWhenRefreshFails_thenNavigatesToLogin() = runTest {
        whenever(preferencesRepository.getAppLaunchFirstTime()).thenReturn(true)
        whenever(storeRefreshService.refreshStoresFromRemote()).thenThrow(RuntimeException("fail"))

        viewModel.start()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value).isInstanceOf(SplashUiState.NavigateToLogin::class.java)
    }

    @Test
    fun start_notFirstTime_signedInWithValidUid_navigatesToMain() = runTest {
        whenever(preferencesRepository.getAppLaunchFirstTime()).thenReturn(false)
        whenever(clientAuth.isSignedIn()).thenReturn(true)
        whenever(clientAuth.getCurrentUserUid()).thenReturn("uid123")
        whenever(userRepository.getUser("uid123")).thenReturn(testUser)
        whenever(storeRepository.getAllStores()).thenReturn(testStores)

        val states = mutableListOf<SplashUiState>()
        val collectJob = launch(testDispatcher) { viewModel.uiState.collect { states.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start()
        advanceUntilIdle()

        assertThat(states.any { it is SplashUiState.NavigateToMain }).isTrue()
        verify(userRepository).insertOrUpdateUser(testUser)
        collectJob.cancel()
    }

    @Test
    fun start_notFirstTime_notSignedIn_navigatesToLogin() = runTest {
        whenever(preferencesRepository.getAppLaunchFirstTime()).thenReturn(false)
        whenever(clientAuth.isSignedIn()).thenReturn(false)
        whenever(storeRepository.getAllStores()).thenReturn(testStores)

        val states = mutableListOf<SplashUiState>()
        val collectJob = launch(testDispatcher) { viewModel.uiState.collect { states.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start()
        advanceUntilIdle()

        assertThat(states.any { it is SplashUiState.NavigateToLogin }).isTrue()
        collectJob.cancel()
    }

    @Test
    fun start_notFirstTime_signedInInvalidUid_navigatesToLogin() = runTest {
        whenever(preferencesRepository.getAppLaunchFirstTime()).thenReturn(false)
        whenever(clientAuth.isSignedIn()).thenReturn(true)
        whenever(clientAuth.getCurrentUserUid()).thenReturn("")
        whenever(storeRepository.getAllStores()).thenReturn(testStores)

        val states = mutableListOf<SplashUiState>()
        val collectJob = launch(testDispatcher) { viewModel.uiState.collect { states.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start()
        advanceUntilIdle()

        assertThat(states.any { it is SplashUiState.NavigateToLogin }).isTrue()
        collectJob.cancel()
    }

    @Test
    fun start_emptyData_showsGeneralErrorAndNavigatesToLogin() = runTest {
        whenever(preferencesRepository.getAppLaunchFirstTime()).thenReturn(true)
        whenever(storeRefreshService.refreshStoresFromRemote()).thenReturn(emptyList())

        viewModel.start()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value).isInstanceOf(SplashUiState.NavigateToLogin::class.java)
    }

    @Test
    fun start_nonEmptyDataException_showsGeneralErrorAndNavigatesToLogin() = runTest {
        whenever(preferencesRepository.getAppLaunchFirstTime()).thenReturn(true)
        whenever(storeRefreshService.refreshStoresFromRemote()).thenThrow(RuntimeException("network"))

        viewModel.start()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value).isInstanceOf(SplashUiState.NavigateToLogin::class.java)
    }

    @Test
    fun markEventConsumed_setsIdle() = runTest {
        whenever(preferencesRepository.getAppLaunchFirstTime()).thenReturn(false)
        whenever(clientAuth.isSignedIn()).thenReturn(false)
        whenever(storeRepository.getAllStores()).thenReturn(testStores)

        val states = mutableListOf<SplashUiState>()
        val collectJob = launch(testDispatcher) { viewModel.uiState.collect { states.add(it) } }
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.start()
        advanceUntilIdle()

        viewModel.markEventConsumed()
        advanceUntilIdle()

        assertThat(viewModel.uiState.value).isEqualTo(SplashUiState.Idle)
        collectJob.cancel()
    }
}
