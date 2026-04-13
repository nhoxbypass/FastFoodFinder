package com.iceteaviet.fastfoodfinder.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.utils.exception.EmptyDataException
import com.iceteaviet.fastfoodfinder.utils.filterInvalidData
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import com.iceteaviet.fastfoodfinder.utils.loadStoresFromServerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashUiState {
    object Idle : SplashUiState()
    object NavigateToLogin : SplashUiState()
    data class NavigateToMain(val delayTime: Long) : SplashUiState()
    object ShowGeneralError : SplashUiState()
    object ShowRetryDialog : SplashUiState()
    object Exit : SplashUiState()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val clientAuth: ClientAuth,
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Idle)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private var startTime: Long = 0L

    fun start() {
        startTime = System.currentTimeMillis()

        if (preferencesRepository.getAppLaunchFirstTime()) {
            onAppOpenFirstTime()
        } else {
            if (clientAuth.isSignedIn()) {
                val uid = clientAuth.getCurrentUserUid()
                if (isValidUserUid(uid)) {
                    loadDataAndOpenMainScreen(uid)
                    return
                }
            }
            loadDataAndOpenLoginScreen()
        }
    }

    fun loadStoresFromServer() {
        viewModelScope.launch {
            try {
                loadStoresFromServerInternal()
                if (clientAuth.isSignedIn() && isValidUserUid(clientAuth.getCurrentUserUid())) {
                    _uiState.value = SplashUiState.NavigateToMain(getSplashRemainingTime())
                } else {
                    _uiState.value = SplashUiState.NavigateToLogin
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getSplashRemainingTime(): Long {
        return SplashActivity.SPLASH_DELAY_TIME - (System.currentTimeMillis() - startTime)
    }

    private fun onAppOpenFirstTime() {
        preferencesRepository.setAppLaunchFirstTime(false)
        viewModelScope.launch {
            try {
                loadStoresFromServerInternal()
                _uiState.value = SplashUiState.NavigateToLogin
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SplashUiState.ShowGeneralError
                _uiState.value = SplashUiState.NavigateToLogin
            }
        }
    }

    private suspend fun loadStoresFromServerInternal() {
        try {
            val storeList = loadStoresFromServerHelper(App.getContext(), clientAuth, storeRepository)
            if (storeList.isNotEmpty()) {
                val filteredStoreList = filterInvalidData(storeList.toMutableList())
                storeRepository.setStores(filteredStoreList)
            } else {
                _uiState.value = SplashUiState.ShowRetryDialog
                throw EmptyDataException()
            }
        } catch (e: Exception) {
            if (e !is EmptyDataException) {
                _uiState.value = SplashUiState.ShowRetryDialog
            }
            throw e
        }
    }

    private fun loadDataAndOpenLoginScreen() {
        viewModelScope.launch {
            try {
                val storeList = storeRepository.getAllStores()
                if (storeList.isEmpty()) {
                    loadStoresFromServer()
                } else {
                    _uiState.value = SplashUiState.NavigateToLogin
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SplashUiState.ShowGeneralError
                _uiState.value = SplashUiState.NavigateToLogin
            }
        }
    }

    private fun loadDataAndOpenMainScreen(userUid: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUser(userUid)
                val storeList = storeRepository.getAllStores()

                userRepository.insertOrUpdateUser(user)

                if (storeList.isEmpty()) {
                    loadStoresFromServer()
                } else {
                    _uiState.value = SplashUiState.NavigateToMain(getSplashRemainingTime())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SplashUiState.ShowGeneralError
                _uiState.value = SplashUiState.NavigateToMain(getSplashRemainingTime())
            }
        }
    }

    fun markEventConsumed() {
        _uiState.value = SplashUiState.Idle
    }
}
