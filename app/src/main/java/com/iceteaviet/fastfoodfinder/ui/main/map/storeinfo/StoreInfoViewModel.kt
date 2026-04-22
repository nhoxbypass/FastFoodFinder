package com.iceteaviet.fastfoodfinder.ui.main.map.storeinfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.iceteaviet.fastfoodfinder.ui.store.StoreDetailActivity.Companion.KEY_STORE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed class StoreInfoEvent {
    object Idle : StoreInfoEvent()
    object Exit : StoreInfoEvent()
    data class OpenStoreDetail(val store: Store) : StoreInfoEvent()
    data class MakeNativeCall(val tel: String) : StoreInfoEvent()
    object ShowEmptyTelToast : StoreInfoEvent()
    data class AddStoreToFavorite(val store: Store) : StoreInfoEvent()
    data class DirectionChange(val store: Store) : StoreInfoEvent()
}

@HiltViewModel
class StoreInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<StoreInfoEvent>(StoreInfoEvent.Idle)
    val uiState: StateFlow<StoreInfoEvent> = _uiState.asStateFlow()

    val store: Store? = savedStateHandle.get<Store>(KEY_STORE)

    init {
        if (store == null) {
            _uiState.value = StoreInfoEvent.Exit
        }
    }

    fun setOnDetailTextViewClick() {
        store?.let {
            _uiState.value = StoreInfoEvent.OpenStoreDetail(it)
        }
    }

    fun onMakeCallWithPermission() {
        if (store != null) {
            if (store.tel.isNotEmpty()) {
                _uiState.value = StoreInfoEvent.MakeNativeCall(store.tel)
            } else {
                _uiState.value = StoreInfoEvent.ShowEmptyTelToast
            }
        }
    }

    fun onAddToFavoriteButtonClick() {
        store?.let {
            _uiState.value = StoreInfoEvent.AddStoreToFavorite(it)
        }
    }

    fun onDirectionButtonClick() {
        store?.let {
            _uiState.value = StoreInfoEvent.DirectionChange(it)
        }
    }

    fun markEventConsumed() {
        _uiState.value = StoreInfoEvent.Idle
    }
}
