package com.iceteaviet.fastfoodfinder.ui.profile

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreIds
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreList
import com.iceteaviet.fastfoodfinder.utils.getFakeUserMultiStoreLists
import com.iceteaviet.fastfoodfinder.utils.getFakeUserStoreLists
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.anyArray
import com.nhaarman.mockitokotlin2.anyOrNull
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.ArrayList
import org.mockito.ArgumentCaptor



class ProfilePresenterTest {
    @Mock
    private lateinit var profileView: ProfileContract.View

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var profilePresenter : ProfilePresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter(){
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()
        profilePresenter = ProfilePresenter(dataManager, schedulerProvider, profileView)
    }

    @Test
    fun subscribeTest_Not_SignedIn() {
        `when`(dataManager.isSignedIn()).thenReturn(false)

        profilePresenter.subscribe()

        verify(profileView).openLoginActivity()
    }

    @Test
    fun subscribe_SignedIn_InvalidUserId() {
        `when`(dataManager.isSignedIn()).thenReturn(true)
        `when`(dataManager.getCurrentUserUid()).thenReturn("")

        profilePresenter.subscribe()

        verify(dataManager).getCurrentUserUid()
        verify(dataManager, never()).getUser("")
    }

    @Test
    fun subscribe_SignedIn_ValidUserId_Failed() {
        `when`(dataManager.isSignedIn()).thenReturn(true)
        `when`(dataManager.getCurrentUserUid()).thenReturn(USER_UID)
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.error(NotFoundException()))

        profilePresenter.subscribe()

        verify(dataManager).getCurrentUserUid()
        verify(dataManager).getUser(USER_UID)
        verify(profileView).showGeneralErrorMessage()
    }

    @Test
    fun subscribe_SignedIn_ValidUserId_EmptyPhotoUrl() {
        var emptyPhotoUser = User(USER_UID, USER_NAME, USER_EMAIL, "", getFakeUserMultiStoreLists())
        `when`(dataManager.isSignedIn()).thenReturn(true)
        `when`(dataManager.getCurrentUserUid()).thenReturn(USER_UID)
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(emptyPhotoUser))

        profilePresenter.subscribe()

        verify(dataManager).getCurrentUserUid()
        verify(dataManager).getUser(USER_UID)
        verify(dataManager).updateCurrentUser(emptyPhotoUser)
        verify(profileView).setName(emptyPhotoUser.name)
        verify(profileView).setEmail(emptyPhotoUser.email)
        assertThat(profilePresenter.defaultList.size).isEqualTo(2)
        verify(profileView).setUserStoreLists(emptyPhotoUser.getUserStoreLists())
        verify(profileView).setStoreListCount(String.format("(%d)", 4))
        verify(profileView).setSavedStoreCount(emptyPhotoUser.getSavedStoreList().getStoreIdList().size)
        verify(profileView).setFavouriteStoreCount(emptyPhotoUser.getFavouriteStoreList().getStoreIdList().size)
    }

    @Test
    fun subscribe_SignedIn_ValidUserId_NotEmptyPhotoUrl() {
        val user = User(USER_UID, USER_NAME, USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
        `when`(dataManager.isSignedIn()).thenReturn(true)
        `when`(dataManager.getCurrentUserUid()).thenReturn(USER_UID)
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(user))

        profilePresenter.subscribe()

        verify(dataManager).getCurrentUserUid()
        verify(dataManager).getUser(USER_UID)
        verify(dataManager).updateCurrentUser(user)
        verify(profileView).loadAvatarPhoto(user.photoUrl)
        verify(profileView).setName(user.name)
        verify(profileView).setEmail(user.email)
        assertThat(profilePresenter.defaultList.size).isEqualTo(2)
        verify(profileView).setUserStoreLists(user.getUserStoreLists())
        verify(profileView).setStoreListCount(String.format("(%d)", 2))
        verify(profileView).setSavedStoreCount(user.getSavedStoreList().getStoreIdList().size)
        verify(profileView).setFavouriteStoreCount(user.getFavouriteStoreList().getStoreIdList().size)
    }

    @Test
    fun onCreateNewListButtonClickTest() {
        profilePresenter.onCreateNewListButtonClick()
        verify(profileView).showCreateNewListDialog()
    }

    @Test
    fun onCreateNewListTest_NullCurrentUser() {
        `when`(dataManager.getCurrentUser()).thenReturn(null)
        profilePresenter.onCreateNewList(NEW_LIST_NAME, NEW_LIST_ICON_ID)

        verify(dataManager, never()).updateStoreListForUser(ArgumentMatchers.anyString(), ArgumentMatchers.anyList())
        verify(profileView, never()).addUserStoreList(anyOrNull())
        verify(profileView, never() ).setStoreListCount(ArgumentMatchers.anyString())
        verify(profileView, never()).dismissCreateNewListDialog()
        verify(profileView, never()).warningListNameExisted()
    }

    @Test
    fun onCreateNewListTest_NotNullCurrentUser_ExistListName() {
        val user = User(USER_UID, USER_NAME, USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
        `when`(dataManager.getCurrentUser()).thenReturn(user)

        profilePresenter.onCreateNewList(user.getUserStoreLists().get(0).listName, user.getUserStoreLists().get(0).iconId)

        verify(profileView).warningListNameExisted()
    }

    @Test
    fun onCreateNewListTest_NotNullCurrentUser_NotExistListName() {
        val user = User(USER_UID, USER_NAME, USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
        `when`(dataManager.getCurrentUser()).thenReturn(user)

        profilePresenter.onCreateNewList(NEW_LIST_NAME, NEW_LIST_ICON_ID)

        val expect = getFakeUserStoreLists().toMutableList()
        val list = UserStoreList(2, ArrayList(), NEW_LIST_ICON_ID, NEW_LIST_NAME)
        expect.add(list)

        verify(dataManager).updateStoreListForUser(USER_UID, expect)
        verify(profileView).addUserStoreList(list)
        verify(profileView).setStoreListCount(String.format("(%d)", expect.size))
        verify(profileView).dismissCreateNewListDialog()
    }

    @Test
    fun onSavedListClickTest_NullUser() {
        `when`(dataManager.getCurrentUser()).thenReturn(null)

        profilePresenter.onSavedListClick()

        verify(profileView, never()).openListDetail(anyOrNull(), anyString())
    }

    @Test
    fun onSavedListClickTest_NotNullUser() {
        val user = User(USER_UID, USER_NAME, USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
        `when`(dataManager.getCurrentUser()).thenReturn(user)
        profilePresenter.defaultList = getFakeUserStoreLists().toMutableList()

        profilePresenter.onSavedListClick()

        verify(profileView).openListDetail(savedList, user.photoUrl)
    }

    @Test
    fun onFavouriteListClick_NullUser() {
        `when`(dataManager.getCurrentUser()).thenReturn(null)

        profilePresenter.onFavouriteListClick()

        verify(profileView, never()).openListDetail(anyOrNull(), anyString())
    }

    @Test
    fun onFavouriteListClick_NotNullUser() {
        val user = User(USER_UID, USER_NAME, USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
        `when`(dataManager.getCurrentUser()).thenReturn(user)
        profilePresenter.defaultList = getFakeUserStoreLists().toMutableList()

        profilePresenter.onFavouriteListClick()

        verify(profileView).openListDetail(favouriteList, user.photoUrl)
    }

    @Test
    fun onStoreListLongClickTest_NullUser() {
        `when`(dataManager.getCurrentUser()).thenReturn(null)

        profilePresenter.onStoreListLongClick(1)

        verify(dataManager, never()).updateStoreListForUser(ArgumentMatchers.anyString(), ArgumentMatchers.anyList())
        verify(profileView, never()).setStoreListCount(ArgumentMatchers.anyString())
    }

    @Test
    fun onStoreListLongClickTest_NotNullUser() {
        val user = User(USER_UID, USER_NAME, USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
        `when`(dataManager.getCurrentUser()).thenReturn(user)

        profilePresenter.onStoreListLongClick(1)

        var newUserList = getFakeUserStoreLists().toMutableList()
        newUserList.removeAt(1)
        verify(dataManager).updateStoreListForUser(user.getUid(), newUserList)
        verify(profileView).setStoreListCount(String.format("(%d)", newUserList.size))
    }
    companion object {
        private const val USER_UID = "123"
        private const val USER_NAME = "My name"
        private const val USER_EMAIL = "myemail@gmail.com"
        private const val USER_PHOTO_URL = "photourl.jpg"

        private const val NEW_LIST_NAME = "New list"
        private const val NEW_LIST_ICON_ID = 100

        private val savedList = getFakeUserStoreLists()[0]
        private val favouriteList = getFakeUserStoreLists()[1]
        private val user = User(USER_UID, USER_NAME, USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
    }
}