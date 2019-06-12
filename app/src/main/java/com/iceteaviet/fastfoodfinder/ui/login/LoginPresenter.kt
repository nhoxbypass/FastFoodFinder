package com.iceteaviet.fastfoodfinder.ui.login

import com.google.firebase.auth.AuthCredential
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.getDefaultUserStoreLists
import com.iceteaviet.fastfoodfinder.utils.getNameFromEmail
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

/**
 * Created by tom on 2019-04-18.
 */
class LoginPresenter : BasePresenter<LoginContract.Presenter>, LoginContract.Presenter {

    private val loginView: LoginContract.View

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, loginView: LoginContract.View) : super(dataManager, schedulerProvider) {
        this.loginView = loginView
    }

    override fun subscribe() {
        // Initialize Firebase Auth
        if (dataManager.isSignedIn()) {
            // User is signed in
            loginView.exit()
            return
        }
    }

    override fun onSkipButtonClick() {
        loginView.showMainView()
    }

    override fun onEmailRegisterSuccess(user: User) {
        ensureBasicUserData(user)

        dataManager.insertOrUpdateUser(user)
        dataManager.setCurrentUser(user)
        loginView.showMainView()
    }

    override fun onLoginSuccess(user: User) {
        dataManager.getUser(user.getUid())
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<User> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(user: User) {
                        dataManager.setCurrentUser(user)
                        loginView.showMainView()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        loginView.showGeneralErrorMessage()
                        dataManager.setCurrentUser(user)
                        loginView.showMainView()
                    }
                })
    }

    override fun onRequestGoogleAccountSuccess(authCredential: AuthCredential, fromLastSignIn: Boolean) {
        dataManager.signInWithCredential(authCredential)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<User> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(user: User) {
                        if (!fromLastSignIn) {
                            ensureBasicUserData(user)
                            dataManager.insertOrUpdateUser(user)
                        }

                        onLoginSuccess(user)
                    }

                    override fun onError(e: Throwable) {
                        loginView.showSignInFailMessage()
                    }
                })
    }

    // TODO: Check is new account
    override fun onRequestFacebookAccountSuccess(authCredential: AuthCredential) {
        dataManager.signInWithCredential(authCredential)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<User> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(user: User) {
                        ensureBasicUserData(user)
                        dataManager.insertOrUpdateUser(user)
                        onLoginSuccess(user)
                    }

                    override fun onError(e: Throwable) {
                        loginView.showSignInFailMessage()
                    }
                })
    }

    private fun ensureBasicUserData(user: User) {
        if (user.name.isBlank()) {
            user.name = getNameFromEmail(user.email)
        }

        if (user.getUserStoreLists().isEmpty())
            user.setUserStoreLists(getDefaultUserStoreLists().toMutableList())
    }
}