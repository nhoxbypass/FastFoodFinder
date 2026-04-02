package com.iceteaviet.fastfoodfinder.ui.profile.createlist

import androidx.lifecycle.ViewModel
import com.iceteaviet.fastfoodfinder.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class CreateListUiState(
    val selectedIconId: Int = 1,
    val event: CreateListEvent = CreateListEvent.Idle
)

sealed class CreateListEvent {
    object Idle : CreateListEvent()
    data class NotifyWithResult(val storeName: String, val iconId: Int) : CreateListEvent()
    object Cancel : CreateListEvent()
    object ShowEmptyNameWarning : CreateListEvent()
}

@HiltViewModel
class CreateListViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CreateListUiState())
    val uiState: StateFlow<CreateListUiState> = _uiState.asStateFlow()

    fun onDoneButtonClick(name: String) {
        if (name.isEmpty()) {
            _uiState.value = _uiState.value.copy(event = CreateListEvent.ShowEmptyNameWarning)
        } else {
            _uiState.value = _uiState.value.copy(
                event = CreateListEvent.NotifyWithResult(name, _uiState.value.selectedIconId)
            )
        }
    }

    fun onCancelButtonClick() {
        _uiState.value = _uiState.value.copy(event = CreateListEvent.Cancel)
    }

    fun onListIconSelect(iconId: Int) {
        _uiState.value = _uiState.value.copy(selectedIconId = iconId)
    }

    fun markEventConsumed() {
        _uiState.value = _uiState.value.copy(event = CreateListEvent.Idle)
    }
}
