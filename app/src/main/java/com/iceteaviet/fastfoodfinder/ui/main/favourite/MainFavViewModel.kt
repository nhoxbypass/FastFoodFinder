package com.iceteaviet.fastfoodfinder.ui.main.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreEvent
import com.iceteaviet.fastfoodfinder.utils.exception.EmptyParamsException
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainFavEvent {
    object Idle : MainFavEvent()
    data class SetStores(val stores: List<Store>) : MainFavEvent()
    data class AddStore(val store: Store) : MainFavEvent()
    data class UpdateStore(val store: Store) : MainFavEvent()
    data class RemoveStore(val store: Store) : MainFavEvent()
    data class ShowWarningMessage(val message: String?) : MainFavEvent()
    data class ShowStoreDetailView(val store: Store) : MainFavEvent()
    object ShowGeneralErrorMessage : MainFavEvent()
}

@HiltViewModel
class MainFavViewModel @Inject constructor(
    private val clientAuth: ClientAuth,
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainFavEvent>(MainFavEvent.Idle)
    val uiState: StateFlow<MainFavEvent> = _uiState.asStateFlow()

    fun start() {
        val uid = clientAuth.getCurrentUserUid()
        if (isValidUserUid(uid)) {
            viewModelScope.launch {
                try {
                    val currUser = userRepository.getUser(uid)
                    loadStoreListsFromIds(currUser.getFavouriteStoreList().getStoreIdList())
                    listenFavStoresOfUser(currUser.getUid())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCleared() {
        val uid = clientAuth.getCurrentUserUid()
        if (isValidUserUid(uid)) {
            userRepository.unsubscribeFavouriteStoresOfUser(uid)
        }
        super.onCleared()
    }

    fun onStoreItemClick(store: Store) {
        _uiState.value = MainFavEvent.ShowStoreDetailView(store)
    }

    private fun loadStoreListsFromIds(storeIdList: List<Int>) {
        if (storeIdList.isEmpty()) return

        viewModelScope.launch {
            try {
                val storeList = storeRepository.findStoresByIds(storeIdList)
                _uiState.value = MainFavEvent.SetStores(storeList)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = MainFavEvent.ShowGeneralErrorMessage
            }
        }
    }

    private fun listenFavStoresOfUser(userUid: String) {
        viewModelScope.launch {
            try {
                userRepository.subscribeFavouriteStoresOfUser(userUid)
                    .collect { storeIdPair ->
                        val id = storeIdPair.first
                        val eventAC = storeIdPair.second

                        if (eventAC < 0) {
                            throw EmptyParamsException()
                        }

                        try {
                            val store = storeRepository.findStoreById(id)
                            val userStoreEvent = UserStoreEvent(store, eventAC)
                            handleUserStoreEvent(userStoreEvent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
            } catch (e: Exception) {
                _uiState.value = MainFavEvent.ShowWarningMessage(e.message)
            }
        }
    }

    private fun handleUserStoreEvent(userStoreEvent: UserStoreEvent) {
        val store = userStoreEvent.store
        when (userStoreEvent.eventActionCode) {
            UserStoreEvent.ACTION_ADDED -> {
                _uiState.value = MainFavEvent.AddStore(store)
            }
            UserStoreEvent.ACTION_CHANGED -> {
                _uiState.value = MainFavEvent.UpdateStore(store)
            }
            UserStoreEvent.ACTION_REMOVED -> {
                _uiState.value = MainFavEvent.RemoveStore(store)
            }
            UserStoreEvent.ACTION_MOVED -> {
            }
        }
    }

    fun markEventConsumed() {
        _uiState.value = MainFavEvent.Idle
    }
}
