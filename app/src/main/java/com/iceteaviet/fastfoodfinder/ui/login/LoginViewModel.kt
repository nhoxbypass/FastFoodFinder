package com.iceteaviet.fastfoodfinder.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.getDefaultUserStoreLists
import com.iceteaviet.fastfoodfinder.utils.getNameFromEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUiState {
    object Idle : LoginUiState()
    object NavigateToMain : LoginUiState()
    object Exit : LoginUiState()
    object ShowSignInFailMessage : LoginUiState()
    object ShowGeneralErrorMessage : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val clientAuth: ClientAuth,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun start() {
        if (clientAuth.isSignedIn()) {
            _uiState.value = LoginUiState.Exit
        }
    }

    fun onSkipButtonClick() {
        _uiState.value = LoginUiState.NavigateToMain
    }

    fun onRegisterSuccess(user: User) {
        viewModelScope.launch {
            ensureBasicUserData(user)
            userRepository.insertOrUpdateUser(user)
            _uiState.value = LoginUiState.NavigateToMain
        }
    }

    fun onLoginSuccess(baseUser: User) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUser(baseUser.getUid())
                userRepository.insertOrUpdateUser(user)
                _uiState.value = LoginUiState.NavigateToMain
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = LoginUiState.ShowGeneralErrorMessage
                _uiState.value = LoginUiState.NavigateToMain
            }
        }
    }

    fun onRequestGoogleAccountSuccess(authCredential: AuthCredential, fromLastSignIn: Boolean) {
        viewModelScope.launch {
            try {
                val user = clientAuth.signInWithCredential(authCredential)
                if (!fromLastSignIn) {
                    onRegisterSuccess(user)
                } else {
                    userRepository.insertOrUpdateUser(user)
                    onLoginSuccess(user)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = LoginUiState.ShowSignInFailMessage
            }
        }
    }

    private fun ensureBasicUserData(user: User) {
        if (user.name.isBlank()) {
            user.name = getNameFromEmail(user.email)
        }

        if (user.getUserStoreLists().isEmpty()) {
            user.setUserStoreLists(getDefaultUserStoreLists().toMutableList())
        }
    }

    fun markEventConsumed() {
        _uiState.value = LoginUiState.Idle
    }
}
