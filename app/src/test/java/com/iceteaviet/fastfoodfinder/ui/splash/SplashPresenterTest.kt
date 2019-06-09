package com.iceteaviet.fastfoodfinder.ui.splash

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.exception.EmptyDataException
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreList
import com.iceteaviet.fastfoodfinder.utils.getFakeUserStoreLists
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.never
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-05-29.
 */
class SplashPresenterTest {

    @Mock
    private lateinit var splashView: SplashContract.View

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var splashPresenter: SplashPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        schedulerProvider = TrampolineSchedulerProvider()

        // Get a reference to the class under test
        splashPresenter = SplashPresenter(dataManager, schedulerProvider, splashView)
    }

    @Test
    fun subscribeTest_appLaunchFirstTime_loadStoresServerSuccess() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(true)

        // Mocks
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(stores))

        splashPresenter.subscribe()

        verify(dataManager).loadStoresFromServer()
        verify(dataManager).setStores(stores)
        verify(dataManager).setAppLaunchFirstTime(false)
        verify(splashView).openLoginScreen()
    }

    @Test
    fun subscribeTest_appLaunchFirstTime_loadStoresServerFailed() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(true)

        // Mocks
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(ArrayList()))

        splashPresenter.subscribe()

        verify(dataManager).loadStoresFromServer()
        verify(dataManager, never()).setStores(ArgumentMatchers.anyList())
        verify(dataManager).setAppLaunchFirstTime(false)
        verify(splashView).showRetryDialog()
    }

    @Test
    fun subscribeTest_signedIn_validUser_emptyStoresLocalData_loadStoresServerSuccess() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(false)
        `when`(dataManager.isSignedIn()).thenReturn(true)
        `when`(dataManager.getCurrentUserUid()).thenReturn(USER_UID)
        `when`(dataManager.getAllStores()).thenReturn(Single.just(ArrayList()))

        // Mocks
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(user))
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(stores))

        splashPresenter.subscribe()

        verify(dataManager).setCurrentUser(user)
        verify(dataManager).loadStoresFromServer()
        verify(dataManager).setStores(stores)
        verify(splashView).openMainActivityWithDelay(ArgumentMatchers.anyLong())
    }

    @Test
    fun subscribeTest_signedIn_validUser_emptyStoresLocalData_loadStoresServerFailed() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(false)
        `when`(dataManager.isSignedIn()).thenReturn(true)
        `when`(dataManager.getCurrentUserUid()).thenReturn(USER_UID)
        `when`(dataManager.getAllStores()).thenReturn(Single.just(ArrayList()))

        // Mocks
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(user))
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(ArrayList()))

        splashPresenter.subscribe()

        verify(dataManager).setCurrentUser(user)
        verify(dataManager).loadStoresFromServer()
        verify(dataManager, never()).setStores(ArgumentMatchers.anyList())
        verify(splashView).showRetryDialog()
    }

    @Test
    fun subscribeTest_signedIn_validUser_haveStoresLocalData() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(false)
        `when`(dataManager.isSignedIn()).thenReturn(true)
        `when`(dataManager.getCurrentUserUid()).thenReturn(USER_UID)
        `when`(dataManager.getAllStores()).thenReturn(Single.just(stores))

        // Mocks
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(user))
        `when`(dataManager.getCurrentUserUid()).thenReturn(USER_UID)

        splashPresenter.subscribe()

        verify(dataManager).setCurrentUser(user)
        verify(dataManager, never()).loadStoresFromServer()
        verify(splashView).openMainActivityWithDelay(ArgumentMatchers.anyLong())
    }

    @Test
    fun subscribeTest_notSignedIn_emptyStoresLocalData_loadStoresServerFailed() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(false)
        `when`(dataManager.isSignedIn()).thenReturn(false)
        `when`(dataManager.getAllStores()).thenReturn(Single.just(ArrayList()))

        // Mocks
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(ArrayList()))

        splashPresenter.subscribe()

        verify(dataManager).loadStoresFromServer()
        verify(dataManager, never()).setStores(ArgumentMatchers.anyList())
        verify(splashView).showRetryDialog()
    }

    @Test
    fun subscribeTest_notSignedIn_emptyStoresLocalData_loadStoresServerSuccess() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(false)
        `when`(dataManager.isSignedIn()).thenReturn(false)
        `when`(dataManager.getAllStores()).thenReturn(Single.just(ArrayList()))

        // Mocks
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(stores))

        splashPresenter.subscribe()

        verify(dataManager).loadStoresFromServer()
        verify(dataManager).setStores(stores)
        verify(splashView).openLoginScreen()
    }

    @Test
    fun subscribeTest_notSignedIn_haveStoresLocalData() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(false)
        `when`(dataManager.isSignedIn()).thenReturn(false)
        `when`(dataManager.getAllStores()).thenReturn(Single.just(stores))

        splashPresenter.subscribe()

        verify(dataManager, never()).loadStoresFromServer()
        verify(splashView).openLoginScreen()
    }

    @Test
    fun subscribeTest_notSignedIn_loadStoresLocalError() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(false)
        `when`(dataManager.isSignedIn()).thenReturn(false)
        `when`(dataManager.getAllStores()).thenReturn(Single.error(EmptyDataException()))

        splashPresenter.subscribe()

        verify(dataManager, never()).loadStoresFromServer()
        verify(splashView).openLoginScreen()
    }

    @Test
    fun loadStoresFromServerTest_success_notSignedIn() {
        // Preconditions
        `when`(dataManager.isSignedIn()).thenReturn(false)

        // Mocks
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(stores))

        splashPresenter.loadStoresFromServer()

        verify(splashView).openLoginScreen()
    }

    @Test
    fun loadStoresFromServerTest_success_signedIn() {
        // Preconditions
        `when`(dataManager.isSignedIn()).thenReturn(true)
        `when`(dataManager.getCurrentUserUid()).thenReturn(USER_UID)

        // Mocks
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(stores))
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(user))

        splashPresenter.loadStoresFromServer()

        verify(splashView).openMainActivityWithDelay(ArgumentMatchers.anyLong())
    }

    @Test
    fun loadStoresFromServerTest_failed() {
        // Preconditions
        `when`(dataManager.isSignedIn()).thenReturn(false)

        // Mocks
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(ArrayList()))

        splashPresenter.loadStoresFromServer()

        verify(splashView).showRetryDialog()
    }

    @Test
    fun loadStoresFromServerTest_error() {
        // Preconditions
        `when`(dataManager.isSignedIn()).thenReturn(false)

        // Mocks
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.error(EmptyDataException()))

        splashPresenter.loadStoresFromServer()

        verify(splashView).showRetryDialog()
    }

    companion object {
        private const val USER_UID = "123"
        private const val USER_NAME = "My name"
        private const val USER_EMAIL = "myemail@gmail.com"
        private const val USER_PHOTO_URL = "photourl.jpg"

        private val stores = getFakeStoreList()

        private val user = User(USER_UID, USER_NAME, USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
    }
}