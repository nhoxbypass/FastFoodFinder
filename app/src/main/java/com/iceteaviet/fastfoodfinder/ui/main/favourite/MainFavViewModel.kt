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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.await
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
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val currUser = userRepository.getUser(uid).await()
                    launch(Dispatchers.Main) {
                        loadStoreListsFromIds(currUser.getFavouriteStoreList().getStoreIdList())
                        listenFavStoresOfUser(currUser.getUid())
                    }
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

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val storeList = storeRepository.findStoresByIds(storeIdList).await()
                launch(Dispatchers.Main) {
                    _uiState.value = MainFavEvent.SetStores(storeList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                launch(Dispatchers.Main) {
                    _uiState.value = MainFavEvent.ShowGeneralErrorMessage
                }
            }
        }
    }

    private fun listenFavStoresOfUser(userUid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userRepository.subscribeFavouriteStoresOfUser(userUid)
                    .asFlow()
                    .collect { storeIdPair ->
                        val id = storeIdPair.first
                        val eventAC = storeIdPair.second

                        if (id == null || eventAC == null || eventAC < 0) {
                            throw EmptyParamsException()
                        }

                        val store = storeRepository.findStoreById(id).blockingGet()
                        val userStoreEvent = UserStoreEvent(store, eventAC)
                        
                        launch(Dispatchers.Main) {
                            handleUserStoreEvent(userStoreEvent)
                        }
                    }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    _uiState.value = MainFavEvent.ShowWarningMessage(e.message)
                }
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
