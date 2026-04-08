package com.iceteaviet.fastfoodfinder.ui.login.emailregister

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

data class EmailRegisterUiState(
    val isInputEnabled: Boolean = true,
    val registerButtonProgress: Int = 0,
    val showInvalidEmailError: Boolean = false,
    val showInvalidPasswordError: Boolean = false,
    val showInvalidRePasswordError: Boolean = false,
    val successUser: User? = null,
    val error: Throwable? = null
)

@HiltViewModel
class EmailRegisterViewModel @Inject constructor(
    private val clientAuth: ClientAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmailRegisterUiState())
    val uiState: StateFlow<EmailRegisterUiState> = _uiState.asStateFlow()

    fun onSignUpButtonClicked(email: String, password: String, rePassword: String) {
        setRegisterProgressState(1)

        if (isValidEmail(email)) {
            if (isValidPassword(password)) {
                if (password == rePassword) {
                    startRegister(email, password)
                } else {
                    _uiState.value = _uiState.value.copy(showInvalidRePasswordError = true)
                    setRegisterProgressState(0)
                }
            } else {
                _uiState.value = _uiState.value.copy(showInvalidPasswordError = true)
                setRegisterProgressState(0)
            }
        } else {
            _uiState.value = _uiState.value.copy(showInvalidEmailError = true)
            setRegisterProgressState(0)
        }
    }

    private fun startRegister(email: String, password: String) {
        viewModelScope.launch {
            try {
                val user = clientAuth.signUpWithEmailAndPassword(email, password).await()
                setRegisterProgressState(2)
                _uiState.value = _uiState.value.copy(successUser = user)
            } catch (e: Exception) {
                e.printStackTrace()
                setRegisterProgressState(-1)
                _uiState.value = _uiState.value.copy(error = e)
            }
        }
    }

    private fun setRegisterProgressState(state: Int) {
        when (state) {
            -1 -> {
                _uiState.value = _uiState.value.copy(
                    registerButtonProgress = -1,
                    isInputEnabled = true
                )
            }
            0 -> {
                _uiState.value = _uiState.value.copy(
                    registerButtonProgress = 0,
                    isInputEnabled = true
                )
            }
            1 -> {
                _uiState.value = _uiState.value.copy(
                    registerButtonProgress = 1,
                    isInputEnabled = false,
                    showInvalidEmailError = false,
                    showInvalidPasswordError = false,
                    showInvalidRePasswordError = false
                )
            }
            2 -> {
                _uiState.value = _uiState.value.copy(
                    registerButtonProgress = 100,
                    isInputEnabled = false
                )
            }
        }
    }

    fun markEventConsumed() {
        _uiState.value = _uiState.value.copy(
            showInvalidEmailError = false,
            showInvalidPasswordError = false,
            showInvalidRePasswordError = false,
            successUser = null,
            error = null
        )
    }
}
