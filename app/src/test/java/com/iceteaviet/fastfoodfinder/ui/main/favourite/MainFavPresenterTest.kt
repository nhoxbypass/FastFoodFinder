package com.iceteaviet.fastfoodfinder.ui.main.favourite

import androidx.core.util.Pair
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreEvent
import com.iceteaviet.fastfoodfinder.utils.exception.UnknownException
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreList
import com.iceteaviet.fastfoodfinder.utils.getFakeUserStoreLists
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class MainFavPresenterTest {
    @Mock
    private lateinit var mainFavView: MainFavContract.View

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var mainFavPresenter: MainFavPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()

        mainFavPresenter = MainFavPresenter(dataManager, schedulerProvider, mainFavView)
    }

    @Test
    fun subscribeTest_notSignedIn() {
        // Preconditions
        `when`(dataManager.getCurrentUser()).thenReturn(null)

        mainFavPresenter.subscribe()

        verifyZeroInteractions(mainFavView)
    }

    @Test
    fun subscribeTest_signedIn_emptyStoreList() {
        // Preconditions
        `when`(dataManager.getCurrentUser()).thenReturn(user)

        // Mocks
        `when`(dataManager.subscribeFavouriteStoresOfUser(user.getUid())).thenReturn(Observable.never())

        mainFavPresenter.subscribe()

        verifyZeroInteractions(mainFavView)
    }

    @Test
    fun subscribeTest_signedIn_getStoresError() {
        // Preconditions
        `when`(dataManager.getCurrentUser()).thenReturn(userFull)

        // Mocks
        `when`(dataManager.findStoresByIds(userFull.getFavouriteStoreList().getStoreIdList())).thenReturn(Single.error(UnknownException()))
        `when`(dataManager.subscribeFavouriteStoresOfUser(user.getUid())).thenReturn(Observable.never())

        mainFavPresenter.subscribe()

        verify(mainFavView).showGeneralErrorMessage()
    }

    @Test
    fun subscribeTest_signedIn_getStoresError_listenStoresError() {
        // Preconditions
        `when`(dataManager.getCurrentUser()).thenReturn(userFull)

        // Mocks
        val ex = UnknownException()
        `when`(dataManager.findStoresByIds(userFull.getFavouriteStoreList().getStoreIdList())).thenReturn(Single.error(UnknownException()))
        `when`(dataManager.subscribeFavouriteStoresOfUser(userFull.getUid())).thenReturn(Observable.error(ex))

        mainFavPresenter.subscribe()

        verify(mainFavView).showGeneralErrorMessage()
        verify(mainFavView).showWarningMessage(ex.message)
    }

    @Test
    fun subscribeTest_signedIn_getStoresSuccess_listenStoresError() {
        // Preconditions
        `when`(dataManager.getCurrentUser()).thenReturn(userFull)

        // Mocks
        val ex = UnknownException()
        `when`(dataManager.findStoresByIds(userFull.getFavouriteStoreList().getStoreIdList())).thenReturn(Single.just(stores))
        `when`(dataManager.subscribeFavouriteStoresOfUser(userFull.getUid())).thenReturn(Observable.error(ex))

        mainFavPresenter.subscribe()

        verify(mainFavView).setStores(stores)
        verify(mainFavView).showWarningMessage(ex.message)
    }

    @Test
    fun subscribeTest_signedIn_getStoresSuccess_listenStoresSuccess_added() {
        // Preconditions
        `when`(dataManager.getCurrentUser()).thenReturn(userFull)

        // Mocks
        val store = stores.get(0)
        `when`(dataManager.findStoresByIds(userFull.getFavouriteStoreList().getStoreIdList())).thenReturn(Single.just(stores))
        `when`(dataManager.subscribeFavouriteStoresOfUser(userFull.getUid())).thenReturn(
                Observable.just(Pair(store.id, UserStoreEvent.ACTION_ADDED))
        )
        `when`(dataManager.findStoresById(store.id)).thenReturn(Single.just(arrayListOf(store)))

        mainFavPresenter.subscribe()

        verify(mainFavView).setStores(stores)
        verify(mainFavView).addStore(store)
    }

    @Test
    fun subscribeTest_signedIn_getStoresSuccess_listenStoresSuccess_changed() {
        // Preconditions
        `when`(dataManager.getCurrentUser()).thenReturn(userFull)

        // Mocks
        val store = stores.get(0)
        `when`(dataManager.findStoresByIds(userFull.getFavouriteStoreList().getStoreIdList())).thenReturn(Single.just(stores))
        `when`(dataManager.subscribeFavouriteStoresOfUser(user.getUid())).thenReturn(
                Observable.just(Pair(store.id, UserStoreEvent.ACTION_CHANGED))
        )
        `when`(dataManager.findStoresById(store.id)).thenReturn(Single.just(arrayListOf(store)))

        mainFavPresenter.subscribe()

        verify(mainFavView).setStores(stores)
        verify(mainFavView).updateStore(store)
    }

    @Test
    fun subscribeTest_signedIn_getStoresSuccess_listenStoresSuccess_removed() {
        // Preconditions
        `when`(dataManager.getCurrentUser()).thenReturn(userFull)

        // Mocks
        val store = stores.get(0)
        `when`(dataManager.findStoresByIds(userFull.getFavouriteStoreList().getStoreIdList())).thenReturn(Single.just(stores))
        `when`(dataManager.subscribeFavouriteStoresOfUser(user.getUid())).thenReturn(
                Observable.just(Pair(store.id, UserStoreEvent.ACTION_REMOVED))
        )
        `when`(dataManager.findStoresById(store.id)).thenReturn(Single.just(arrayListOf(store)))

        mainFavPresenter.subscribe()

        verify(mainFavView).setStores(stores)
        verify(mainFavView).removeStore(store)
    }

    companion object {
        private const val USER_UID = "123"
        private const val USER_NAME = "My name"
        private const val USER_EMAIL = "myemail@gmail.com"
        private const val USER_PHOTO_URL = "photourl.jpg"

        private val user = User(USER_UID, "", "", "", ArrayList())
        private val userFull = User(USER_UID, USER_NAME, USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())

        private val stores = getFakeStoreList()
    }
}