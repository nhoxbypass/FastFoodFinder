package com.iceteaviet.fastfoodfinder.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.utils.getCurrentUserHelper
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val avatarPhotoUrl: String = "",
    val name: String = "",
    val email: String = "",
    val storeListCount: String = "(0)",
    val savedStoreCount: Int = 0,
    val favouriteStoreCount: Int = 0,
    val userStoreLists: List<UserStoreList> = emptyList(),
    val event: ProfileEvent = ProfileEvent.Idle
)

sealed class ProfileEvent {
    object Idle : ProfileEvent()
    object OpenLoginActivity : ProfileEvent()
    object ShowCreateNewListDialog : ProfileEvent()
    object DismissCreateNewListDialog : ProfileEvent()
    object WarningListNameExisted : ProfileEvent()
    object ShowGeneralErrorMessage : ProfileEvent()
    data class OpenListDetail(val userStoreList: UserStoreList, val photoUrl: String) : ProfileEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val clientAuth: ClientAuth,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var defaultList: MutableList<UserStoreList> = ArrayList()

    fun start() {
        if (!clientAuth.isSignedIn()) {
            _uiState.value = _uiState.value.copy(event = ProfileEvent.OpenLoginActivity)
        } else {
            loadCurrentUserData()
        }
    }

    private fun loadCurrentUserData() {
        val uid = clientAuth.getCurrentUserUid()
        if (!isValidUserUid(uid)) return

        viewModelScope.launch {
            try {
                val user = userRepository.getUser(uid)
                userRepository.insertOrUpdateUser(user)

                loadStoreLists(user)

                _uiState.value = _uiState.value.copy(
                    avatarPhotoUrl = user.photoUrl,
                    name = user.name,
                    email = user.email,
                    savedStoreCount = user.getSavedStoreList().getStoreIdList().size,
                    favouriteStoreCount = user.getFavouriteStoreList().getStoreIdList().size
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(event = ProfileEvent.ShowGeneralErrorMessage)
            }
        }
    }

    private fun loadStoreLists(currentUser: User) {
        defaultList.clear()
        for (i in 0 until currentUser.getUserStoreLists().size) {
            if (i <= 1) {
                defaultList.add(currentUser.getUserStoreLists()[i])
            } else {
                break
            }
        }

        _uiState.value = _uiState.value.copy(
            userStoreLists = currentUser.getUserStoreLists().toList(),
            storeListCount = String.format("(%d)", currentUser.getUserStoreLists().size)
        )
    }

    fun onCreateNewListButtonClick() {
        _uiState.value = _uiState.value.copy(event = ProfileEvent.ShowCreateNewListDialog)
    }

    fun onCreateNewList(listName: String, iconId: Int) {
        viewModelScope.launch {
            val currentUser = getCurrentUserHelper(clientAuth, userRepository) ?: return@launch
    
            if (!isListNameExisted(listName, currentUser)) {
                val id = currentUser.getUserStoreLists().size
                val list = UserStoreList(id, ArrayList(), iconId, listName)
                currentUser.addStoreList(list)
                userRepository.updateStoreListForUser(currentUser.getUid(), currentUser.getUserStoreLists())
    
                val updatedLists = currentUser.getUserStoreLists().toList()
                
                _uiState.value = _uiState.value.copy(
                    userStoreLists = updatedLists,
                    storeListCount = String.format("(%d)", updatedLists.size),
                    event = ProfileEvent.DismissCreateNewListDialog
                )
            } else {
                _uiState.value = _uiState.value.copy(event = ProfileEvent.WarningListNameExisted)
            }
        }
    }

    private fun isListNameExisted(listName: String, user: User): Boolean {
        for (storeList in user.getUserStoreLists()) {
            if (listName == storeList.listName) {
                return true
            }
        }
        return false
    }

    fun onSavedListClick() {
        if (UserStoreList.ID_SAVED < defaultList.size) {
            onStoreListClick(defaultList[UserStoreList.ID_SAVED])
        }
    }

    fun onFavouriteListClick() {
        if (UserStoreList.ID_FAVOURITE < defaultList.size) {
            onStoreListClick(defaultList[UserStoreList.ID_FAVOURITE])
        }
    }

    fun onStoreListClick(listPacket: UserStoreList) {
        viewModelScope.launch {
            val user = getCurrentUserHelper(clientAuth, userRepository)
            if (user != null) {
                _uiState.value = _uiState.value.copy(
                    event = ProfileEvent.OpenListDetail(listPacket, user.photoUrl)
                )
            }
        }
    }

    fun onStoreListLongClick(position: Int) {
        viewModelScope.launch {
            val currentUser = getCurrentUserHelper(clientAuth, userRepository) ?: return@launch
    
            currentUser.removeStoreList(position)
            userRepository.updateStoreListForUser(currentUser.getUid(), currentUser.getUserStoreLists())
    
            val updatedLists = currentUser.getUserStoreLists().toList()
            
            _uiState.value = _uiState.value.copy(
                userStoreLists = updatedLists,
                storeListCount = String.format("(%d)", updatedLists.size)
            )
        }
    }

    fun markEventConsumed() {
        _uiState.value = _uiState.value.copy(event = ProfileEvent.Idle)
    }
}
