package com.iceteaviet.fastfoodfinder.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.exception.EmptyDataException
import com.iceteaviet.fastfoodfinder.utils.filterInvalidData
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import com.iceteaviet.fastfoodfinder.utils.loadStoresFromServerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.rx2.await
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
                loadStoresFromServerInternal().await()
                if (clientAuth.isSignedIn() && isValidUserUid(clientAuth.getCurrentUserUid())) {
                    _uiState.value = SplashUiState.NavigateToMain(getSplashRemainingTime())
                } else {
                    _uiState.value = SplashUiState.NavigateToLogin
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // loadStoresFromServerInternal already triggers ShowRetryDialog internally if validation fails, 
                // but if an unexpected error occurs here:
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
                loadStoresFromServerInternal().await()
                _uiState.value = SplashUiState.NavigateToLogin
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = SplashUiState.ShowGeneralError
                _uiState.value = SplashUiState.NavigateToLogin
            }
        }
    }

    private fun loadStoresFromServerInternal(): Completable {
        return Completable.create { emitter ->
            loadStoresFromServerHelper(App.getContext(), clientAuth, storeRepository)
                .subscribe({ storeList ->
                    if (storeList.isNotEmpty()) {
                        val filteredStoreList = filterInvalidData(storeList.toMutableList())
                        storeRepository.setStores(filteredStoreList)
                        emitter.onComplete()
                    } else {
                        _uiState.value = SplashUiState.ShowRetryDialog
                        emitter.onError(EmptyDataException())
                    }
                }, { error ->
                    _uiState.value = SplashUiState.ShowRetryDialog
                    emitter.onError(error)
                })
        }
    }

    private fun loadDataAndOpenLoginScreen() {
        viewModelScope.launch {
            try {
                val storeList = storeRepository.getAllStores().await()
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
                val user = userRepository.getUser(userUid).await()
                val storeList = storeRepository.getAllStores().await()

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
