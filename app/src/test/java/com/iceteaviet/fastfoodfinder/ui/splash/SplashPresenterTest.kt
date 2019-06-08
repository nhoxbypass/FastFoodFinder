package com.iceteaviet.fastfoodfinder.ui.splash

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
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
    fun subscribeTest_appLaunchFirstTime_loadDataSuccess() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(true)

        // Mocks
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(stores))

        splashPresenter.subscribe()

        verify(dataManager).loadStoresFromServer()
        verify(dataManager).setAppLaunchFirstTime(false)
        verify(splashView).openLoginScreen()
    }

    @Test
    fun subscribeTest_appLaunchFirstTime_loadDataFailed() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(true)

        // Mocks
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(ArrayList()))

        splashPresenter.subscribe()

        verify(dataManager).loadStoresFromServer()
        verify(dataManager).setAppLaunchFirstTime(false)
        verify(splashView).showRetryDialog()
    }

    @Test
    fun subscribeTest_signedIn_validUser_emptyStoreData_loadDataSuccess() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(false)
        `when`(dataManager.isSignedIn()).thenReturn(true)
        `when`(dataManager.getCurrentUserUid()).thenReturn(USER_UID)
        `when`(dataManager.getAllStores()).thenReturn(Single.just(ArrayList()))

        // Mocks
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(user))
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(stores))

        splashPresenter.subscribe()

        verify(dataManager).getUser(USER_UID)
        verify(dataManager).setCurrentUser(ArgumentMatchers.any())
        verify(dataManager).loadStoresFromServer()
        verify(splashView).openMainActivityWithDelay(ArgumentMatchers.anyLong())
    }

    @Test
    fun subscribeTest_signedIn_validUser_emptyStoreData_loadDataFailed() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(false)
        `when`(dataManager.isSignedIn()).thenReturn(true)
        `when`(dataManager.getCurrentUserUid()).thenReturn(USER_UID)
        `when`(dataManager.getAllStores()).thenReturn(Single.just(ArrayList()))

        // Mocks
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(user))
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(ArrayList()))

        splashPresenter.subscribe()

        verify(dataManager).getUser(USER_UID)
        verify(dataManager).setCurrentUser(ArgumentMatchers.any())
        verify(dataManager).loadStoresFromServer()
        verify(splashView).showRetryDialog()
    }

    @Test
    fun subscribeTest_signedIn_validUser_haveStoreData() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(false)
        `when`(dataManager.isSignedIn()).thenReturn(true)
        `when`(dataManager.getCurrentUserUid()).thenReturn(USER_UID)
        `when`(dataManager.getAllStores()).thenReturn(Single.just(stores))

        // Mocks
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(user))
        `when`(dataManager.getCurrentUserUid()).thenReturn(USER_UID)

        splashPresenter.subscribe()

        verify(dataManager).getUser(USER_UID)
        verify(dataManager).setCurrentUser(ArgumentMatchers.any())
        verify(dataManager, never()).loadStoresFromServer()
        verify(splashView).openMainActivityWithDelay(ArgumentMatchers.anyLong())
    }

    @Test
    fun subscribeTest_notSignedIn_emptyStoreData_loadDataFailed() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(false)
        `when`(dataManager.isSignedIn()).thenReturn(false)
        `when`(dataManager.getAllStores()).thenReturn(Single.just(ArrayList()))

        // Mocks
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(ArrayList()))

        splashPresenter.subscribe()

        verify(dataManager).loadStoresFromServer()
        verify(splashView).showRetryDialog()
    }

    @Test
    fun subscribeTest_notSignedIn_emptyStoreData_loadStoreDataSuccess() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(false)
        `when`(dataManager.isSignedIn()).thenReturn(false)
        `when`(dataManager.getAllStores()).thenReturn(Single.just(ArrayList()))

        // Mocks
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(stores))

        splashPresenter.subscribe()

        verify(dataManager).loadStoresFromServer()
        verify(splashView).openLoginScreen()
    }

    @Test
    fun subscribeTest_notSignedIn_haveStoreData() {
        // Preconditions
        `when`(dataManager.getAppLaunchFirstTime()).thenReturn(false)
        `when`(dataManager.isSignedIn()).thenReturn(false)
        `when`(dataManager.getAllStores()).thenReturn(Single.just(stores))

        splashPresenter.subscribe()

        verify(dataManager, never()).loadStoresFromServer()
        verify(splashView).openLoginScreen()
    }

    @Test
    fun loadStoreFromServerTest_loadDataSuccess_notSignedIn() {
        // Preconditions
        `when`(dataManager.isSignedIn()).thenReturn(false)

        // Mocks
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(stores))

        splashPresenter.loadStoresFromServer()

        verify(splashView).openLoginScreen()
    }

    @Test
    fun loadStoreFromServerTest_loadDataSuccess_signedIn() {
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
    fun loadStoreFromServerTest_loadDataFailed() {
        // Preconditions
        `when`(dataManager.isSignedIn()).thenReturn(false)

        // Mocks
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(ArrayList()))

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