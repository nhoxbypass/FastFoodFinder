package com.iceteaviet.fastfoodfinder.ui.storelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoreListUiState(
    val stores: List<Store> = emptyList()
)

@HiltViewModel
class StoreListViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreListUiState())
    val uiState: StateFlow<StoreListUiState> = _uiState.asStateFlow()

    fun start() {
        viewModelScope.launch {
            try {
                val stores = storeRepository.getAllStores()
                _uiState.value = StoreListUiState(stores = stores)
            } catch (e: Exception) {
                _uiState.value = StoreListUiState(stores = emptyList())
            }
        }
    }
}
