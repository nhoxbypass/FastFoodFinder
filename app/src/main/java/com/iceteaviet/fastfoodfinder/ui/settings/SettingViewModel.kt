package com.iceteaviet.fastfoodfinder.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.utils.filterInvalidData
import com.iceteaviet.fastfoodfinder.utils.loadStoresFromServerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingUiState(
    val isVietnamese: Boolean = true,
    val showSignOutButton: Boolean = false,
    val showLoadingProgressIndicator: Boolean = false,
    val event: SettingEvent = SettingEvent.Idle
)

sealed class SettingEvent {
    object Idle : SettingEvent()
    data class LoadLanguage(val languageCode: String) : SettingEvent()
    data class ShowSuccessLoadingToast(val message: String) : SettingEvent()
    data class ShowFailedLoadingToast(val message: String?) : SettingEvent()
    object OpenLogin : SettingEvent()
}

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val clientAuth: ClientAuth,
    private val preferencesRepository: PreferencesRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    fun start() {
        val isViet = preferencesRepository.getIfLanguageIsVietnamese()
        _uiState.value = _uiState.value.copy(
            isVietnamese = isViet,
            showSignOutButton = clientAuth.isSignedIn()
        )
    }

    fun onSignOutClicked() {
        clientAuth.signOut()
        _uiState.value = _uiState.value.copy(event = SettingEvent.OpenLogin)
    }

    fun onLanguageChanged() {
        val currentIsViet = _uiState.value.isVietnamese
        val nextIsViet = !currentIsViet
        
        val langCode = if (nextIsViet) "vi" else "en"
        
        _uiState.value = _uiState.value.copy(
            isVietnamese = nextIsViet,
            event = SettingEvent.LoadLanguage(langCode)
        )
        
        preferencesRepository.setIfLanguageIsVietnamese(nextIsViet)
    }

    fun onLoadStoreFromServer() {
        _uiState.value = _uiState.value.copy(showLoadingProgressIndicator = true)
        
        viewModelScope.launch {
            try {
                val storeList = loadStoresFromServerHelper(App.getContext(), clientAuth, storeRepository)
                val filteredStoreList = filterInvalidData(storeList.toMutableList())
                storeRepository.setStores(filteredStoreList)
                
                _uiState.value = _uiState.value.copy(
                    showLoadingProgressIndicator = false,
                    event = SettingEvent.ShowSuccessLoadingToast("")
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    showLoadingProgressIndicator = false,
                    event = SettingEvent.ShowFailedLoadingToast(e.message)
                )
            }
        }
    }

    fun markEventConsumed() {
        _uiState.value = _uiState.value.copy(event = SettingEvent.Idle)
    }
}
