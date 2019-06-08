package com.iceteaviet.fastfoodfinder.ui.login.emaillogin

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.isValidEmail
import com.iceteaviet.fastfoodfinder.utils.isValidPassword
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

/**
 * Created by tom on 2019-04-18.
 */
class EmailLoginPresenter : BasePresenter<EmailLoginContract.Presenter>, EmailLoginContract.Presenter {

    private val emailLoginView: EmailLoginContract.View

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, emailLoginView: EmailLoginContract.View) : super(dataManager, schedulerProvider) {
        this.emailLoginView = emailLoginView
    }

    override fun subscribe() {
    }

    // TODO: Optimize checking logic
    override fun onSignInButtonClicked(email: String, password: String) {
        setLoginProgressState(1)

        if (isValidEmail(email)) {
            if (isValidPassword(password)) {
                dataManager.signInWithEmailAndPassword(email, password)
                        .subscribeOn(schedulerProvider.io())
                        .observeOn(schedulerProvider.ui())
                        .subscribe(object : SingleObserver<User> {
                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable.add(d)
                            }

                            override fun onSuccess(user: User) {
                                setLoginProgressState(2)
                                emailLoginView.notifyLoginSuccess(user)
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                                setLoginProgressState(-1)
                                emailLoginView.notifyLoginError(e)
                            }
                        })
            } else {
                emailLoginView.showInvalidPasswordError()
                setLoginProgressState(0)
            }
        } else {
            emailLoginView.showInvalidEmailError()
            setLoginProgressState(0)
        }
    }

    private fun setLoginProgressState(state: Int) {
        when (state) {
            -1 -> {
                emailLoginView.setLoginButtonProgress(-1)
                emailLoginView.setInputEnabled(true)
            }

            0 -> {
                emailLoginView.setLoginButtonProgress(0)
                emailLoginView.setInputEnabled(true)
            }

            1 -> {
                emailLoginView.setLoginButtonProgress(1)
                emailLoginView.setInputEnabled(false)
            }

            2 -> {
                emailLoginView.setLoginButtonProgress(100)
                emailLoginView.setInputEnabled(false)
            }
        }
    }
}