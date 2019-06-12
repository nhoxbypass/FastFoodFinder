package com.iceteaviet.fastfoodfinder.ui.login.emailregister

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
class EmailRegisterPresenterTest {
    @Mock
    private lateinit var emailRegisterView: EmailRegisterContract.View

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var emailRegisterPresenter: EmailRegisterPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()

        emailRegisterPresenter = EmailRegisterPresenter(dataManager, schedulerProvider, emailRegisterView)
    }

    @Test
    fun subscribeTest() {
        emailRegisterPresenter.subscribe()
    }

    @Test
    fun onSignUpButtonClickedTest_invalidEmail() {
        emailRegisterPresenter.onSignUpButtonClicked(INVALID_EMAIL, PWD, RE_PWD)

        verify(emailRegisterView).setInputEnabled(false)
        verify(emailRegisterView).setRegisterButtonProgress(1)

        verify(emailRegisterView).showInvalidEmailError()

        verify(emailRegisterView).setInputEnabled(true)
        verify(emailRegisterView).setRegisterButtonProgress(0)
    }

    @Test
    fun onSignUpButtonClickedTest_invalidPassword() {
        emailRegisterPresenter.onSignUpButtonClicked(EMAIL, INVALID_PWD, RE_PWD)

        verify(emailRegisterView).setInputEnabled(false)
        verify(emailRegisterView).setRegisterButtonProgress(1)

        verify(emailRegisterView).showInvalidPasswordError()

        verify(emailRegisterView).setInputEnabled(true)
        verify(emailRegisterView).setRegisterButtonProgress(0)
    }

    @Test
    fun onSignUpButtonClickedTest_invalidRePassword() {
        emailRegisterPresenter.onSignUpButtonClicked(EMAIL, PWD, INVALID_RE_PWD)

        verify(emailRegisterView).setInputEnabled(false)
        verify(emailRegisterView).setRegisterButtonProgress(1)

        verify(emailRegisterView).showInvalidRePasswordError()

        verify(emailRegisterView).setInputEnabled(true)
        verify(emailRegisterView).setRegisterButtonProgress(0)
    }

    @Test
    fun onSignUpButtonClickedTest_validData_signUpError() {
        // Mocks
        val exception = UnknownException()
        `when`(dataManager.signUpWithEmailAndPassword(EMAIL, PWD)).thenReturn(Single.error(exception))

        emailRegisterPresenter.onSignUpButtonClicked(EMAIL, PWD, RE_PWD)

        verify(emailRegisterView).setInputEnabled(false)
        verify(emailRegisterView).setRegisterButtonProgress(1)

        verify(exception).printStackTrace()
        verify(emailRegisterView).notifyLoginError(exception)

        verify(emailRegisterView).setInputEnabled(true)
        verify(emailRegisterView).setRegisterButtonProgress(-1)
    }

    @Test
    fun onSignUpButtonClickedTest_validData_signUpSuccess() {
        // Mocks
        `when`(dataManager.signUpWithEmailAndPassword(EMAIL, PWD)).thenReturn(Single.just(user))

        emailRegisterPresenter.onSignUpButtonClicked(EMAIL, PWD, RE_PWD)

        verify(emailRegisterView).setRegisterButtonProgress(1)

        verify(emailRegisterView).notifyRegisterSuccess(user)

        verify(emailRegisterView, times(2)).setInputEnabled(false)
        verify(emailRegisterView).setRegisterButtonProgress(100)
    }

    companion object {
        private const val USER_UID = "123"

        private val user = User(USER_UID, "", "", "", ArrayList())

        private const val EMAIL = "my_email@domain.com"
        private const val PWD = "1234567890"
        private const val RE_PWD = "1234567890"

        private const val INVALID_EMAIL = ""
        private const val INVALID_PWD = ""
        private const val INVALID_RE_PWD = ""
    }
}