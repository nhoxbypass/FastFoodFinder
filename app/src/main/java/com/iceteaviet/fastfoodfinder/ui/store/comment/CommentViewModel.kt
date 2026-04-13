package com.iceteaviet.fastfoodfinder.ui.store.comment

import androidx.lifecycle.ViewModel
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.utils.getCurrentUserHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

data class CommentUiState(
    val remainCharCount: String = CommentViewModel.MAX_CHAR.toString(),
    val isOverLimit: Boolean = false,
    val isPostButtonEnabled: Boolean = false,
    val event: CommentEvent = CommentEvent.Idle
)

sealed class CommentEvent {
    object Idle : CommentEvent()
    data class ExitWithResult(val comment: Comment) : CommentEvent()
    object Exit : CommentEvent()
    object ShowCloseConfirmDialog : CommentEvent()
    object ShowCommentPostFailedWarning : CommentEvent()
    object ShowGeneralErrorMessage : CommentEvent()
}

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val clientAuth: ClientAuth,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommentUiState())
    val uiState: StateFlow<CommentUiState> = _uiState.asStateFlow()

    fun afterCommentTextChanged(text: CharSequence) {
        val length = text.length
        val remainChars = MAX_CHAR - length
        
        _uiState.value = _uiState.value.copy(
            remainCharCount = remainChars.toString(),
            isOverLimit = remainChars < 0,
            isPostButtonEnabled = length > 0 && remainChars >= 0
        )
    }

    fun onPostButtonClick(commentText: CharSequence) {
        if (commentText.isEmpty() || commentText.length > MAX_CHAR) {
            _uiState.value = _uiState.value.copy(event = CommentEvent.ShowCommentPostFailedWarning)
            return
        }

        viewModelScope.launch {
            val currUser = getCurrentUserHelper(clientAuth, userRepository)
            if (currUser == null) {
                _uiState.value = _uiState.value.copy(event = CommentEvent.ShowGeneralErrorMessage)
                return@launch
            }

            val comment = Comment(
                currUser.name, 
                currUser.photoUrl,
                commentText.toString(), 
                "", 
                System.currentTimeMillis()
            )
            _uiState.value = _uiState.value.copy(event = CommentEvent.ExitWithResult(comment))
        }
    }

    fun onBackButtonClick(commentText: CharSequence) {
        if (commentText.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(event = CommentEvent.ShowCloseConfirmDialog)
        } else {
            _uiState.value = _uiState.value.copy(event = CommentEvent.Exit)
        }
    }

    fun markEventConsumed() {
        _uiState.value = _uiState.value.copy(event = CommentEvent.Idle)
    }

    companion object {
        const val MAX_CHAR = 140
    }
}
