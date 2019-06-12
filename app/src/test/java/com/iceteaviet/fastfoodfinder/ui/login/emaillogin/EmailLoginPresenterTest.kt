package com.iceteaviet.fastfoodfinder.ui.login.emaillogin

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.exception.UnknownException
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-06-12.
 */
class EmailLoginPresenterTest {
    @Mock
    private lateinit var emailLoginView: EmailLoginContract.View

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var emailLoginPresenter: EmailLoginPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()

        emailLoginPresenter = EmailLoginPresenter(dataManager, schedulerProvider, emailLoginView)
    }

    @Test
    fun subscribeTest() {
        emailLoginPresenter.subscribe()
    }

    @Test
    fun onSignInButtonClickedTest_invalidEmail() {
        emailLoginPresenter.onSignInButtonClicked(INVALID_EMAIL, PWD)

        verify(emailLoginView).setInputEnabled(false)
        verify(emailLoginView).setLoginButtonProgress(1)

        verify(emailLoginView).showInvalidEmailError()

        verify(emailLoginView).setInputEnabled(true)
        verify(emailLoginView).setLoginButtonProgress(0)
    }

    @Test
    fun onSignInButtonClickedTest_invalidPassword() {
        emailLoginPresenter.onSignInButtonClicked(EMAIL, INVALID_PWD)

        verify(emailLoginView).setInputEnabled(false)
        verify(emailLoginView).setLoginButtonProgress(1)

        verify(emailLoginView).showInvalidPasswordError()

        verify(emailLoginView).setInputEnabled(true)
        verify(emailLoginView).setLoginButtonProgress(0)
    }

    @Test
    fun onSignUpButtonClickedTest_validData_signInError() {
        // Mocks
        val exception = UnknownException()
        `when`(dataManager.signInWithEmailAndPassword(EMAIL, PWD)).thenReturn(Single.error(exception))

        emailLoginPresenter.onSignInButtonClicked(EMAIL, PWD)

        verify(emailLoginView).setInputEnabled(false)
        verify(emailLoginView).setLoginButtonProgress(1)

        verify(emailLoginView).notifyLoginError(exception)

        verify(emailLoginView).setInputEnabled(true)
        verify(emailLoginView).setLoginButtonProgress(-1)
    }

    @Test
    fun onSignUpButtonClickedTest_validData_signInSuccess() {
        // Mocks
        `when`(dataManager.signInWithEmailAndPassword(EMAIL, PWD)).thenReturn(Single.just(user))

        emailLoginPresenter.onSignInButtonClicked(EMAIL, PWD)

        verify(emailLoginView).setLoginButtonProgress(1)

        verify(emailLoginView).notifyLoginSuccess(user)

        verify(emailLoginView, times(2)).setInputEnabled(false)
        verify(emailLoginView).setLoginButtonProgress(100)
    }

    companion object {
        private const val USER_UID = "123"

        private val user = User(USER_UID, "", "", "", ArrayList())

        private const val EMAIL = "my_email@domain.com"
        private const val PWD = "1234567890"

        private const val INVALID_EMAIL = ""
        private const val INVALID_PWD = ""
    }
}