package com.iceteaviet.fastfoodfinder.ui.main.recently

import androidx.lifecycle.ViewModel
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed class MainRecentlyEvent {
    object Idle : MainRecentlyEvent()
    data class SetStores(val stores: ArrayList<Store>) : MainRecentlyEvent()
    data class ShowStoreDetailView(val store: Store) : MainRecentlyEvent()
}

@HiltViewModel
class MainRecentlyViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<MainRecentlyEvent>(MainRecentlyEvent.Idle)
    val uiState: StateFlow<MainRecentlyEvent> = _uiState.asStateFlow()

    fun start() {
        val stores = ArrayList<Store>()
        //TODO: Load recently store from Realm

        _uiState.value = MainRecentlyEvent.SetStores(stores)
    }

    fun onStoreItemClick(store: Store) {
        _uiState.value = MainRecentlyEvent.ShowStoreDetailView(store)
    }

    fun markEventConsumed() {
        _uiState.value = MainRecentlyEvent.Idle
    }
}
