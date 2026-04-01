package com.iceteaviet.fastfoodfinder.ui.login.emaillogin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.isValidEmail
import com.iceteaviet.fastfoodfinder.utils.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import javax.inject.Inject

data class EmailLoginUiState(
    val isInputEnabled: Boolean = true,
    val loginButtonProgress: Int = 0,
    val showInvalidEmailError: Boolean = false,
    val showInvalidPasswordError: Boolean = false,
    val successUser: User? = null,
    val error: Throwable? = null
)

@HiltViewModel
class EmailLoginViewModel @Inject constructor(
    private val clientAuth: ClientAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailLoginUiState())
    val uiState: StateFlow<EmailLoginUiState> = _uiState.asStateFlow()

    fun onSignInButtonClicked(email: String, password: String) {
        setLoginProgressState(1)

        if (isValidEmail(email)) {
            if (isValidPassword(password)) {
                viewModelScope.launch {
                    try {
                        val user = clientAuth.signInWithEmailAndPassword(email, password).await()
                        setLoginProgressState(2)
                        _uiState.value = _uiState.value.copy(successUser = user)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        setLoginProgressState(-1)
                        _uiState.value = _uiState.value.copy(error = e)
                    }
                }
            } else {
                _uiState.value = _uiState.value.copy(showInvalidPasswordError = true)
                setLoginProgressState(0)
            }
        } else {
            _uiState.value = _uiState.value.copy(showInvalidEmailError = true)
            setLoginProgressState(0)
        }
    }

    private fun setLoginProgressState(state: Int) {
        when (state) {
            -1 -> {
                _uiState.value = _uiState.value.copy(
                    loginButtonProgress = -1,
                    isInputEnabled = true
                )
            }
            0 -> {
                _uiState.value = _uiState.value.copy(
                    loginButtonProgress = 0,
                    isInputEnabled = true
                )
            }
            1 -> {
                _uiState.value = _uiState.value.copy(
                    loginButtonProgress = 1,
                    isInputEnabled = false,
                    showInvalidEmailError = false,
                    showInvalidPasswordError = false
                )
            }
            2 -> {
                _uiState.value = _uiState.value.copy(
                    loginButtonProgress = 100,
                    isInputEnabled = false
                )
            }
        }
    }

    fun markEventConsumed() {
        _uiState.value = _uiState.value.copy(
            showInvalidEmailError = false,
            showInvalidPasswordError = false,
            successUser = null,
            error = null
        )
    }
}
