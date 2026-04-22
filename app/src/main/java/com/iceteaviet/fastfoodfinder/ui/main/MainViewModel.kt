package com.iceteaviet.fastfoodfinder.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.iceteaviet.fastfoodfinder.ui.main.search.SearchEventBus
import com.iceteaviet.fastfoodfinder.ui.main.search.SearchEventResult
import com.iceteaviet.fastfoodfinder.utils.Constant
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainEvent {
    object Idle : MainEvent()
    object NavigateToProfile : MainEvent()
    object NavigateToLogin : MainEvent()
    object NavigateToAR : MainEvent()
    object NavigateToSettings : MainEvent()
    object NavigateToFavourite : MainEvent()
    object ShowSearchView : MainEvent()
    object HideSearchView : MainEvent()
    object HideKeyboard : MainEvent()
    object ClearFocus : MainEvent()
    object ShowSearchWarning : MainEvent()
    data class UpdateSearchQueryText(val query: String) : MainEvent()
}

data class MainUiState(
    val showSignInButton: Boolean = true,
    val userName: String? = null,
    val userEmail: String? = null,
    val userAvatarUrl: String? = null,
    val event: MainEvent = MainEvent.Idle
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val clientAuth: ClientAuth,
    private val userRepository: UserRepository,
    private val preferencesRepository: PreferencesRepository,
    private val searchEventBus: SearchEventBus
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var isBusRegistered = false

    fun start() {
        if (!isBusRegistered) {
            searchEventBus.events
                .onEach { onSearchResult(it) }
                .launchIn(viewModelScope)
            isBusRegistered = true
        }

        if (!clientAuth.isSignedIn()) {
            _uiState.value = _uiState.value.copy(showSignInButton = true)
        } else {
            val uid = clientAuth.getCurrentUserUid()
            if (isValidUserUid(uid)) {
                viewModelScope.launch {
                    try {
                        val currUser = userRepository.getUser(uid)
                        _uiState.value = _uiState.value.copy(
                            showSignInButton = false,
                            userName = currUser.name,
                            userEmail = currUser.email,
                            userAvatarUrl = if (currUser.photoUrl.isNotBlank()) currUser.photoUrl else null
                        )
                    } catch (e: Exception) {
                        _uiState.value = _uiState.value.copy(showSignInButton = true)
                    }
                }
            } else {
                _uiState.value = _uiState.value.copy(showSignInButton = true)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun onProfileMenuItemClick() {
        if (clientAuth.isSignedIn()) {
            _uiState.value = _uiState.value.copy(event = MainEvent.NavigateToProfile)
        } else {
            _uiState.value = _uiState.value.copy(event = MainEvent.NavigateToLogin)
        }
    }

    fun onARLiveSightMenuItemClick() {
        _uiState.value = _uiState.value.copy(event = MainEvent.NavigateToAR)
    }

    fun onSettingsMenuItemClick() {
        _uiState.value = _uiState.value.copy(event = MainEvent.NavigateToSettings)
    }

    fun onFavouriteMenuItemClick() {
        _uiState.value = _uiState.value.copy(event = MainEvent.NavigateToFavourite)
    }

    fun onSignInMenuItemClick() {
        _uiState.value = _uiState.value.copy(event = MainEvent.NavigateToLogin)
    }

    fun onSearchMenuItemExpand() {
        _uiState.value = _uiState.value.copy(event = MainEvent.ShowSearchView)
    }

    fun onSearchMenuItemCollapse() {
        searchEventBus.emit(SearchEventResult(SearchEventResult.SEARCH_ACTION_COLLAPSE))
    }

    fun onSearchQuerySubmit(query: String) {
        searchEventBus.emit(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT, query))
    }

    private fun onSearchResult(searchEventResult: SearchEventResult) {
        _uiState.value = _uiState.value.copy(event = MainEvent.HideSearchView)
        _uiState.value = _uiState.value.copy(event = MainEvent.ClearFocus)

        when (searchEventResult.resultCode) {
            SearchEventResult.SEARCH_ACTION_QUICK -> {
                handleSearchQuickAction(searchEventResult.searchString)
            }
            SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT -> {
                if (searchEventResult.searchString.isNotBlank()) {
                    handleSearchQuerySubmitAction(searchEventResult.searchString)
                }
            }
            SearchEventResult.SEARCH_ACTION_COLLAPSE -> {
                handleSearchCollapseAction()
            }
            SearchEventResult.SEARCH_ACTION_STORE_CLICK -> {
                val store = searchEventResult.store
                if (store != null) {
                    handleSearchStoreClickAction(store)
                }
            }
            else -> {
                _uiState.value = _uiState.value.copy(event = MainEvent.ShowSearchWarning)
            }
        }
    }

    private fun handleSearchQuickAction(searchString: String) {
        _uiState.value = _uiState.value.copy(event = MainEvent.UpdateSearchQueryText(searchString))
    }

    private fun handleSearchQuerySubmitAction(searchString: String) {
        preferencesRepository.addSearchHistories(searchString)
        _uiState.value = _uiState.value.copy(event = MainEvent.UpdateSearchQueryText(searchString))
    }

    private fun handleSearchCollapseAction() {
        _uiState.value = _uiState.value.copy(event = MainEvent.HideKeyboard)
    }

    private fun handleSearchStoreClickAction(store: Store) {
        _uiState.value = _uiState.value.copy(event = MainEvent.UpdateSearchQueryText(store.title))
        preferencesRepository.addSearchHistories(Constant.SEARCH_STORE_PREFIX + store.id)
    }

    fun markEventConsumed() {
        _uiState.value = _uiState.value.copy(event = MainEvent.Idle)
    }
}
