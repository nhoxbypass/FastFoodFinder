package com.iceteaviet.fastfoodfinder.ui.main

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.service.eventbus.SearchEventResult
import com.iceteaviet.fastfoodfinder.service.eventbus.core.Event
import com.iceteaviet.fastfoodfinder.service.eventbus.core.IBus
import com.iceteaviet.fastfoodfinder.utils.Constant
import com.iceteaviet.fastfoodfinder.utils.StoreType
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
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-06-20.
 */
class MainPresenterTest {
    @Mock
    private lateinit var mainView: MainContract.View

    @Mock
    private lateinit var dataManager: DataManager

    @Mock
    private lateinit var bus: IBus

    private lateinit var mainPresenter: MainPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()

        mainPresenter = MainPresenter(dataManager, schedulerProvider, bus, mainView)
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
    fun onProfileMenuItemClickTest_notSignedIn() {
        // Preconditions
        `when`(dataManager.isSignedIn()).thenReturn(false)

        mainPresenter.onProfileMenuItemClick()

        verify(mainView).showLoginView()
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

    @Test
    fun onSearchResultTest_actionQuick_emptyQuery() {
        // Preconditions
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, StoreType.TYPE_CIRCLE_K)

        mainPresenter.onSearchResult(searchEventResult)

        verify(mainView).hideSearchView()
        verify(mainView).clearFocus()
        verify(mainView).setSearchQueryText("")
    }

    @Test
    fun onSearchResultTest_actionQuick() {
        // Preconditions
        val queryStr = "circle K"
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, queryStr, StoreType.TYPE_CIRCLE_K)

        mainPresenter.onSearchResult(searchEventResult)

        verify(mainView).hideSearchView()
        verify(mainView).clearFocus()
        verify(mainView).setSearchQueryText(queryStr)
    }

    @Test
    fun onSearchResultTest_actionQuerySubmit_emptyQuery() {
        // Preconditions
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT, "")

        mainPresenter.onSearchResult(searchEventResult)

        verify(mainView).hideSearchView()
        verify(mainView).clearFocus()
        verify(dataManager, never()).addSearchHistories(ArgumentMatchers.anyString())
        verify(mainView, never()).setSearchQueryText(ArgumentMatchers.anyString())
    }

    @Test
    fun onSearchResultTest_actionQuerySubmit() {
        // Preconditions
        val queryStr = "circle K"
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT, queryStr)

        mainPresenter.onSearchResult(searchEventResult)

        verify(mainView).hideSearchView()
        verify(mainView).clearFocus()
        verify(dataManager).addSearchHistories(queryStr)
        verify(mainView).setSearchQueryText(queryStr)
    }

    @Test
    fun onSearchResultTest_actionCollapse() {
        // Preconditions
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_COLLAPSE)

        mainPresenter.onSearchResult(searchEventResult)

        verify(mainView).hideSearchView()
        verify(mainView).clearFocus()
        verify(mainView).hideKeyboard()
    }

    @Test
    fun onSearchResultTest_actionStoreClick_nullStore() {
        // Preconditions
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_STORE_CLICK, "")

        mainPresenter.onSearchResult(searchEventResult)

        verify(mainView).hideSearchView()
        verify(mainView).clearFocus()
        verifyNoMoreInteractions(mainView)
    }

    @Test
    fun onSearchResultTest_actionStoreClick() {
        // Preconditions
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_STORE_CLICK, store.title, store)

        mainPresenter.onSearchResult(searchEventResult)

        verify(mainView).hideSearchView()
        verify(mainView).clearFocus()
        verify(mainView).setSearchQueryText(STORE_TITLE)
        verify(dataManager).addSearchHistories(Constant.SEARCH_STORE_PREFIX + store.id)
    }

    @Test
    fun onSearchResultTest_invalidAction() {
        // Preconditions
        val searchEventResult = SearchEventResult(-1)

        mainPresenter.onSearchResult(searchEventResult)

        verify(mainView).hideSearchView()
        verify(mainView).clearFocus()
        verify(mainView).showSearchWarningMessage()
    }


    companion object {
        private const val USER_UID = "123"
        private const val USER_NAME = "My name"
        private const val USER_EMAIL = "myemail@gmail.com"
        private const val USER_PHOTO_URL = "photourl.jpg"

        private const val STORE_ID = 123
        private const val STORE_TITLE = "store_title"
        private const val STORE_ADDRESS = "store_address"
        private const val STORE_LAT = "10.773996"
        private const val STORE_LNG = "106.6898035"
        private const val STORE_TEL = "012345678965"
        private const val STORE_TYPE = StoreType.TYPE_CIRCLE_K

        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)
    }
}