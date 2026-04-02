package com.iceteaviet.fastfoodfinder.ui.storelist

import androidx.lifecycle.ViewModel
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class StoreListUiState(
    val stores: List<Store> = emptyList()
)

@HiltViewModel
class StoreListViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(StoreListUiState())
    val uiState: StateFlow<StoreListUiState> = _uiState.asStateFlow()

    fun start() {
        _uiState.value = StoreListUiState(stores = getFakeStoreList())
    }
}
