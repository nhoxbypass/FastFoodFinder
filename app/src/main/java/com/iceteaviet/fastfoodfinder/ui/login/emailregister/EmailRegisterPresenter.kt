package com.iceteaviet.fastfoodfinder.ui.login.emailregister

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
class EmailRegisterPresenter : BasePresenter<EmailRegisterContract.Presenter>, EmailRegisterContract.Presenter {

    private val emailRegisterView: EmailRegisterContract.View

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, emailRegisterView: EmailRegisterContract.View) : super(dataManager, schedulerProvider) {
        this.emailRegisterView = emailRegisterView
        this.emailRegisterView.presenter = this
    }

    override fun subscribe() {
    }

    // TODO: Optimize checking logic
    override fun onSignUpButtonClicked(email: String, password: String, rePassword: String) {
        setRegisterProgressState(1)

        if (isValidEmail(email)) {
            if (isValidPassword(password)) {
                if (password.equals(rePassword)) {
                    // Begin account register
                    startRegister(email, password)
                } else {
                    emailRegisterView.showInvalidRePasswordError()
                    setRegisterProgressState(0)
                }
            } else {
                emailRegisterView.showInvalidPasswordError()
                setRegisterProgressState(0)
            }
        } else {
            emailRegisterView.showInvalidEmailError()
            setRegisterProgressState(0)
        }
    }

    private fun startRegister(email: String, password: String) {
        dataManager.signUpWithEmailAndPassword(email, password)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<User> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(user: User) {
                        setRegisterProgressState(2)
                        emailRegisterView.notifyRegisterSuccess(user)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        setRegisterProgressState(-1)
                        emailRegisterView.notifyLoginError(e)
                    }
                })
    }

    private fun setRegisterProgressState(state: Int) {
        when (state) {
            -1 -> {
                emailRegisterView.setRegisterButtonProgress(-1)
                emailRegisterView.setInputEnabled(true)
            }

            0 -> {
                emailRegisterView.setRegisterButtonProgress(0)
                emailRegisterView.setInputEnabled(true)
            }

            1 -> {
                emailRegisterView.setRegisterButtonProgress(1)
                emailRegisterView.setInputEnabled(false)
            }

            2 -> {
                emailRegisterView.setRegisterButtonProgress(100)
                emailRegisterView.setInputEnabled(false)
            }
        }
    }
}