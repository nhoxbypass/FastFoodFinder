package com.iceteaviet.fastfoodfinder.ui.profile.cover

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class UpdateCoverUiState(
    val selectedImage: Drawable? = null,
    val event: UpdateCoverEvent = UpdateCoverEvent.Idle
)

sealed class UpdateCoverEvent {
    object Idle : UpdateCoverEvent()
    object OpenImageFilePicker : UpdateCoverEvent()
    data class DismissWithResult(val selectedImage: Drawable?) : UpdateCoverEvent()
    object Cancel : UpdateCoverEvent()
}

@HiltViewModel
class UpdateCoverViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateCoverUiState())
    val uiState: StateFlow<UpdateCoverUiState> = _uiState.asStateFlow()

    fun onImageBrowserButtonClick() {
        _uiState.value = _uiState.value.copy(event = UpdateCoverEvent.OpenImageFilePicker)
    }

    fun onCoverImageSelect(selectedImage: Drawable) {
        _uiState.value = _uiState.value.copy(
            selectedImage = selectedImage
        )
    }

    fun onDoneButtonClick() {
        _uiState.value = _uiState.value.copy(
            event = UpdateCoverEvent.DismissWithResult(_uiState.value.selectedImage)
        )
    }
    
    fun onCancelButtonClick() {
        _uiState.value = _uiState.value.copy(event = UpdateCoverEvent.Cancel)
    }

    fun markEventConsumed() {
        _uiState.value = _uiState.value.copy(event = UpdateCoverEvent.Idle)
    }
}
