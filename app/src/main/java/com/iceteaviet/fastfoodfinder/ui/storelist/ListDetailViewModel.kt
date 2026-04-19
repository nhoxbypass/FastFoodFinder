package com.iceteaviet.fastfoodfinder.ui.storelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreListIconDrawableRes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListDetailUiState(
    val stores: List<Store> = emptyList(),
    val listName: String = "",
    val storeIconResId: Int = 0,
    val event: ListDetailEvent = ListDetailEvent.Idle
)

sealed class ListDetailEvent {
    object Idle : ListDetailEvent()
    object Exit : ListDetailEvent()
    object ShowGeneralErrorMessage : ListDetailEvent()
}

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListDetailUiState())
    val uiState: StateFlow<ListDetailUiState> = _uiState.asStateFlow()

    private var userStoreList: UserStoreList = UserStoreList()
    private var photoUrl: String = ""

    fun handleExtras(userStoreList: UserStoreList?, photoUrl: String?) {
        if (photoUrl == null && userStoreList == null) {
            _uiState.value = _uiState.value.copy(event = ListDetailEvent.Exit)
            return
        }

        this.userStoreList = userStoreList ?: UserStoreList()
        this.photoUrl = photoUrl ?: ""
    }

    fun start() {
        _uiState.value = _uiState.value.copy(
            listName = userStoreList.listName,
            storeIconResId = getStoreListIconDrawableRes(userStoreList.iconId)
        )

        viewModelScope.launch {
            try {
                val storeList = storeRepository.findStoresByIds(userStoreList.getStoreIdList())
                _uiState.value = _uiState.value.copy(stores = storeList)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(event = ListDetailEvent.ShowGeneralErrorMessage)
            }
        }
    }

    fun markEventConsumed() {
        _uiState.value = _uiState.value.copy(event = ListDetailEvent.Idle)
    }
}
