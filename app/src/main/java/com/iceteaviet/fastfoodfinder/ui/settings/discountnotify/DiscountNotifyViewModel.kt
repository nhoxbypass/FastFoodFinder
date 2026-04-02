package com.iceteaviet.fastfoodfinder.ui.settings.discountnotify

import androidx.lifecycle.ViewModel
import com.iceteaviet.fastfoodfinder.utils.getStoreNameByKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class DiscountNotifyUiState(
    val event: DiscountNotifyEvent = DiscountNotifyEvent.Idle
)

sealed class DiscountNotifyEvent {
    object Idle : DiscountNotifyEvent()
    object CancelDialog : DiscountNotifyEvent()
    object DoneDialog : DiscountNotifyEvent()
}

@HiltViewModel
class DiscountNotifyViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DiscountNotifyUiState())
    val uiState: StateFlow<DiscountNotifyUiState> = _uiState.asStateFlow()

    fun onCancelButtonClick() {
        _uiState.value = _uiState.value.copy(event = DiscountNotifyEvent.CancelDialog)
    }

    fun onDoneButtonClick() {
        _uiState.value = _uiState.value.copy(event = DiscountNotifyEvent.DoneDialog)
    }

    fun getStoreName(key: String): String {
        return getStoreNameByKey(key)
    }

    fun markEventConsumed() {
        _uiState.value = _uiState.value.copy(event = DiscountNotifyEvent.Idle)
    }

    fun getStoreList(): Array<String> {
        return LIST_STORES
    }

    companion object {
        const val KEY_CIRCLE_K = "circle_k"
        const val KEY_MINI_STOP = "mini_stop"
        const val KEY_FAMILY_MART = "family_mark"
        const val KEY_BSMART = "bsmart"
        const val KEY_SHOP_N_GO = "shop_n_go"

        val LIST_STORES = arrayOf(KEY_BSMART, KEY_CIRCLE_K, KEY_FAMILY_MART, KEY_MINI_STOP, KEY_SHOP_N_GO)
    }
}
