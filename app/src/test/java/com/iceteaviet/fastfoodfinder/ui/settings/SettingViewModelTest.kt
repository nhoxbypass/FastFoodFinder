package com.iceteaviet.fastfoodfinder.ui.settings

import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRefreshService
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
class SettingViewModelTest {

    private val clientAuth: ClientAuth = mock()
    private val preferencesRepository: PreferencesRepository = mock()
    private val storeRefreshService: StoreRefreshService = mock()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: SettingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SettingViewModel(clientAuth, preferencesRepository, storeRefreshService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun start_setsVietnameseFromPrefs() {
        whenever(preferencesRepository.getIfLanguageIsVietnamese()).thenReturn(true)
        whenever(clientAuth.isSignedIn()).thenReturn(true)

        viewModel.start()

        assertThat(viewModel.uiState.value.isVietnamese).isTrue()
    }

    @Test
    fun start_setsEnglishFromPrefs() {
        whenever(preferencesRepository.getIfLanguageIsVietnamese()).thenReturn(false)
        whenever(clientAuth.isSignedIn()).thenReturn(true)

        viewModel.start()

        assertThat(viewModel.uiState.value.isVietnamese).isFalse()
    }

    @Test
    fun start_showsSignOutButton_whenSignedIn() {
        whenever(preferencesRepository.getIfLanguageIsVietnamese()).thenReturn(true)
        whenever(clientAuth.isSignedIn()).thenReturn(true)

        viewModel.start()

        assertThat(viewModel.uiState.value.showSignOutButton).isTrue()
    }

    @Test
    fun start_hidesSignOutButton_whenNotSignedIn() {
        whenever(preferencesRepository.getIfLanguageIsVietnamese()).thenReturn(true)
        whenever(clientAuth.isSignedIn()).thenReturn(false)

        viewModel.start()

        assertThat(viewModel.uiState.value.showSignOutButton).isFalse()
    }

    @Test
    fun onLanguageChanged_togglesFromVietnameseToEnglish() {
        whenever(preferencesRepository.getIfLanguageIsVietnamese()).thenReturn(true)
        viewModel.start()

        viewModel.onLanguageChanged()

        assertThat(viewModel.uiState.value.isVietnamese).isFalse()
        assertThat((viewModel.uiState.value.event as SettingEvent.LoadLanguage).languageCode).isEqualTo("en")
        verify(preferencesRepository).setIfLanguageIsVietnamese(false)
    }

    @Test
    fun onLanguageChanged_togglesFromEnglishToVietnamese() {
        whenever(preferencesRepository.getIfLanguageIsVietnamese()).thenReturn(false)
        viewModel.start()

        viewModel.onLanguageChanged()

        assertThat(viewModel.uiState.value.isVietnamese).isTrue()
        assertThat((viewModel.uiState.value.event as SettingEvent.LoadLanguage).languageCode).isEqualTo("vi")
        verify(preferencesRepository).setIfLanguageIsVietnamese(true)
    }

    @Test
    fun onSignOutClicked_signsOutAndEmitsOpenLogin() {
        viewModel.onSignOutClicked()

        verify(clientAuth).signOut()
        assertThat(viewModel.uiState.value.event).isEqualTo(SettingEvent.OpenLogin)
    }

    @Test
    fun onLoadStoreFromServer_showsSuccessOnSuccess() = runTest {
        whenever(storeRefreshService.refreshStoresFromRemote()).thenReturn(listOf(
            Store(id = 1, title = "Test", address = "Addr", lat = 10.0, lng = 106.0)
        ))

        val states = mutableListOf<SettingUiState>()
        val collectJob = launch { viewModel.uiState.collect { states.add(it) } }

        viewModel.onLoadStoreFromServer()
        advanceUntilIdle()

        assertThat(states.any { it.event is SettingEvent.ShowSuccessLoadingToast }).isTrue()
        assertThat(states.last().showLoadingProgressIndicator).isFalse()
        collectJob.cancel()
    }

    @Test
    fun onLoadStoreFromServer_showsFailureOnError() = runTest {
        whenever(storeRefreshService.refreshStoresFromRemote()).thenThrow(RuntimeException("fail"))

        val states = mutableListOf<SettingUiState>()
        val collectJob = launch { viewModel.uiState.collect { states.add(it) } }

        viewModel.onLoadStoreFromServer()
        advanceUntilIdle()

        assertThat(states.any { it.event is SettingEvent.ShowFailedLoadingToast }).isTrue()
        assertThat(states.last().showLoadingProgressIndicator).isFalse()
        collectJob.cancel()
    }

    @Test
    fun markEventConsumed_setsIdle() {
        viewModel.onSignOutClicked()
        assertThat(viewModel.uiState.value.event).isEqualTo(SettingEvent.OpenLogin)

        viewModel.markEventConsumed()

        assertThat(viewModel.uiState.value.event).isEqualTo(SettingEvent.Idle)
    }
}
