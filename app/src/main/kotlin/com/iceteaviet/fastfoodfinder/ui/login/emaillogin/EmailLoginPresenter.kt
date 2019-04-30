package com.iceteaviet.fastfoodfinder.ui.login.emaillogin

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.isValidEmail
import com.iceteaviet.fastfoodfinder.utils.isValidPassword
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by tom on 2019-04-18.
 */
class EmailLoginPresenter : BasePresenter<EmailLoginContract.Presenter>, EmailLoginContract.Presenter {

    private val emailLoginView: EmailLoginContract.View

    constructor(dataManager: DataManager, emailLoginView: EmailLoginContract.View) : super(dataManager) {
        this.emailLoginView = emailLoginView
        this.emailLoginView.presenter = this
    }

    override fun subscribe() {
    }

    // TODO: Optimize checking logic
    override fun onSignInButtonClicked(email: String, password: String) {
        setLoginProgressState(1)

        if (isValidEmail(email)) {
            if (isValidPassword(password)) {
                dataManager.signInWithEmailAndPassword(email, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : SingleObserver<User> {
                            override fun onSubscribe(d: Disposable) {
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