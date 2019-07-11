package com.iceteaviet.fastfoodfinder.ui.login

import com.google.firebase.auth.AuthCredential
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import com.iceteaviet.fastfoodfinder.utils.getFakeUserStoreLists
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.any
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class LoginPresenterTest {
    @Mock
    private lateinit var loginView: LoginContract.View

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var loginPresenter: LoginPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()

        loginPresenter = LoginPresenter(dataManager, schedulerProvider, loginView)
    }

    @Test
    fun subscribeTest_signedIn() {
        // Preconditions
        `when`(dataManager.isSignedIn()).thenReturn(true)

        loginPresenter.subscribe()

        verify(loginView).exit()
    }

    @Test
    fun subscribeTest_notSignedIn() {
        // Preconditions
        `when`(dataManager.isSignedIn()).thenReturn(false)

        loginPresenter.subscribe()

        verifyZeroInteractions(loginView)
    }

    @Test
    fun onSkipButtonClickTest() {
        loginPresenter.onSkipButtonClick()

        verify(loginView).showMainView()
    }

    @Test
    fun onEmailRegisterSuccessTest_emptyName() {
        val userFullEmptyName = User(USER_UID, "", USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
        val userFull = User(USER_UID, "myemail", USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())

        loginPresenter.onRegisterSuccess(userFullEmptyName)

        verify(dataManager).updateCurrentUser(userFull)
        verify(loginView).showMainView()
    }

    @Test
    fun onEmailRegisterSuccessTest() {
        loginPresenter.onRegisterSuccess(user)

        verify(dataManager).updateCurrentUser(user)
        verify(loginView).showMainView()
    }

    @Test
    fun onLoginSuccessTest_getUserSuccess() {
        // Preconditions
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(userFull))

        loginPresenter.onLoginSuccess(user)

        verify(dataManager).updateCurrentUser(userFull)
        verify(loginView).showMainView()
    }

    @Test
    fun onLoginSuccessTest_getUserError() {
        // Preconditions
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.error(NotFoundException()))

        loginPresenter.onLoginSuccess(user)

        verify(loginView).showGeneralErrorMessage()
        verify(dataManager, never()).updateCurrentUser(user)
        verify(loginView).showMainView()
    }

    @Test
    fun onRequestGoogleAccountSuccessTest_signInSuccess_newUser_emptyName() {
        // Preconditions
        val userFullEmptyName = User(USER_UID, "", USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
        val userFull = User(USER_UID, "myemail", USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
        `when`(dataManager.signInWithCredential(any())).thenReturn(Single.just(userFullEmptyName))

        loginPresenter.onRequestGoogleAccountSuccess(mock(AuthCredential::class.java), false)

        verify(dataManager, never()).getUser(USER_UID)
        verify(dataManager).updateCurrentUser(userFull)
        verify(loginView).showMainView()
    }

    @Test
    fun onRequestGoogleAccountSuccessTest_signInSuccess_newUser() {
        // Preconditions
        `when`(dataManager.signInWithCredential(any())).thenReturn(Single.just(user))

        loginPresenter.onRequestGoogleAccountSuccess(mock(AuthCredential::class.java), false)

        verify(dataManager, never()).getUser(USER_UID)
        verify(dataManager).updateCurrentUser(user)
        verify(loginView).showMainView()
    }

    @Test
    fun onRequestGoogleAccountSuccessTest_signInSuccess_fromLastSignIn_getUserSuccess() {
        // Preconditions
        `when`(dataManager.signInWithCredential(any())).thenReturn(Single.just(user))
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(userFull))

        loginPresenter.onRequestGoogleAccountSuccess(mock(AuthCredential::class.java), true)

        verify(dataManager).updateCurrentUser(userFull)
        verify(loginView).showMainView()
    }

    @Test
    fun onRequestGoogleAccountSuccessTest_signInSuccess_fromLastSignIn_getUserError() {
        // Preconditions
        `when`(dataManager.signInWithCredential(any())).thenReturn(Single.just(user))
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.error(NotFoundException()))

        loginPresenter.onRequestGoogleAccountSuccess(mock(AuthCredential::class.java), true)

        verify(loginView).showGeneralErrorMessage()
        verify(dataManager).updateCurrentUser(user)
        verify(loginView).showMainView()
    }


    @Test
    fun onRequestGoogleAccountSuccessTest_signInFailed() {
        // Preconditions
        `when`(dataManager.signInWithCredential(any())).thenReturn(Single.error(NotFoundException()))

        loginPresenter.onRequestGoogleAccountSuccess(mock(AuthCredential::class.java), false)

        verify(loginView).showSignInFailMessage()
    }

    @Test
    fun onRequestFacebookAccountSuccessTest_signInSuccess_emptyName_getUserSuccess() {
        val userFullEmptyName = User(USER_UID, "", USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
        val userFull = User(USER_UID, "myemail", USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())

        // Preconditions
        `when`(dataManager.signInWithCredential(any())).thenReturn(Single.just(userFullEmptyName))
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(userFull))

        loginPresenter.onRequestFacebookAccountSuccess(mock(AuthCredential::class.java))

        verify(dataManager, atLeastOnce()).updateCurrentUser(userFull)
        verify(loginView).showMainView()
    }

    @Test
    fun onRequestFacebookAccountSuccessTest_signInSuccess_getUserSuccess() {
        // Preconditions
        `when`(dataManager.signInWithCredential(any())).thenReturn(Single.just(user))
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(userFull))

        loginPresenter.onRequestFacebookAccountSuccess(mock(AuthCredential::class.java))

        verify(dataManager).updateCurrentUser(userFull)
        verify(loginView).showMainView()
    }

    @Test
    fun onRequestFacebookAccountSuccessTest_signInSuccess_fromLastSignIn_getUserError() {
        // Preconditions
        `when`(dataManager.signInWithCredential(any())).thenReturn(Single.just(user))
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.error(NotFoundException()))

        loginPresenter.onRequestFacebookAccountSuccess(mock(AuthCredential::class.java))

        verify(loginView).showGeneralErrorMessage()
        verify(dataManager).updateCurrentUser(user)
        verify(loginView).showMainView()
    }


    @Test
    fun onRequestFacebookAccountSuccessTest_signInFailed() {
        // Preconditions
        `when`(dataManager.signInWithCredential(any())).thenReturn(Single.error(NotFoundException()))

        loginPresenter.onRequestFacebookAccountSuccess(mock(AuthCredential::class.java))

        verify(loginView).showSignInFailMessage()
    }

    companion object {
        private const val USER_UID = "123"
        private const val USER_NAME = "My name"
        private const val USER_EMAIL = "myemail@gmail.com"
        private const val USER_PHOTO_URL = "photourl.jpg"

        private val user = User(USER_UID, "", "", "", ArrayList())
        private val userFull = User(USER_UID, USER_NAME, USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
    }
}