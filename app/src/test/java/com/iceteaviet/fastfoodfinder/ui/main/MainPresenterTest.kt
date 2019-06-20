package com.iceteaviet.fastfoodfinder.ui.main

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.getFakeUserStoreLists
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-06-20.
 */
class MainPresenterTest {
    @Mock
    private lateinit var mainView: MainContract.View

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var mainPresenter: MainPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()

        mainPresenter = MainPresenter(dataManager, schedulerProvider, mainView)
    }

    @Test
    fun subscribeTest_notSignedIn() {
        `when`(dataManager.isSignedIn()).thenReturn(false)

        mainPresenter.subscribe()

        verify(mainView).updateProfileHeader(true)
    }

    @Test
    fun subscribeTest_signedIn_currentUserNull() {
        `when`(dataManager.isSignedIn()).thenReturn(true)
        `when`(dataManager.getCurrentUser()).thenReturn(null)

        mainPresenter.subscribe()

        verify(mainView).updateProfileHeader(true)
    }

    @Test
    fun subscribeTest_signedIn_validCurrentUser_emptyAvatar() {
        // Preconditions
        val user = User(USER_UID, USER_NAME, USER_EMAIL, "", getFakeUserStoreLists())
        `when`(dataManager.isSignedIn()).thenReturn(true)
        `when`(dataManager.getCurrentUser()).thenReturn(user)

        mainPresenter.subscribe()

        verify(mainView).updateProfileHeader(false)
        verify(mainView).setProfileHeaderNameText(USER_NAME)
        verify(mainView).setProfileHeaderEmailText(USER_EMAIL)
        verify(mainView, never()).loadProfileHeaderAvatar(ArgumentMatchers.anyString())
    }

    @Test
    fun subscribeTest_signedIn_validCurrentUser_haveAvatar() {
        // Preconditions
        val user = User(USER_UID, USER_NAME, USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
        `when`(dataManager.isSignedIn()).thenReturn(true)
        `when`(dataManager.getCurrentUser()).thenReturn(user)

        mainPresenter.subscribe()

        verify(mainView).updateProfileHeader(false)
        verify(mainView).setProfileHeaderNameText(USER_NAME)
        verify(mainView).setProfileHeaderEmailText(USER_EMAIL)
        verify(mainView).loadProfileHeaderAvatar(USER_PHOTO_URL)
    }

    @Test
    fun onProfileMenuItemClickTest_signedIn() {
        // Preconditions
        `when`(dataManager.isSignedIn()).thenReturn(true)

        mainPresenter.onProfileMenuItemClick()

        verify(mainView).showProfileView()
    }

    @Test
    fun onARLiveSightMenuItemClickTest() {
        mainPresenter.onARLiveSightMenuItemClick()

        verify(mainView).showARLiveSightView()
    }

    @Test
    fun onSettingsMenuItemClickTest() {
        mainPresenter.onSettingsMenuItemClick()

        verify(mainView).showSettingsView()
    }

    @Test
    fun onSignInMenuItemClickTest() {
        mainPresenter.onSignInMenuItemClick()

        verify(mainView).showLoginView()
    }

    @Test
    fun onSearchMenuItemExpandTest() {
        mainPresenter.onSearchMenuItemExpand()

        verify(mainView).showSearchView()
    }


    companion object {
        private const val USER_UID = "123"
        private const val USER_NAME = "My name"
        private const val USER_EMAIL = "myemail@gmail.com"
        private const val USER_PHOTO_URL = "photourl.jpg"
    }
}