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
class LoginPresenter(
    private val clientAuth: com.iceteaviet.fastfoodfinder.data.auth.ClientAuth,
    private val userRepository: com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository,
    schedulerProvider: SchedulerProvider,
    private val loginView: LoginContract.View
) : BasePresenter<LoginContract.Presenter>(schedulerProvider), LoginContract.Presenter {

    override fun subscribe() {
        // Initialize Firebase Auth
        if (clientAuth.isSignedIn()) {
            // User is signed in
            loginView.exit()
            return
        }
    }

    override fun onSkipButtonClick() {
        loginView.showMainView()
    }

    override fun onRegisterSuccess(user: User) {
        ensureBasicUserData(user)
        userRepository.insertOrUpdateUser(user)
        loginView.showMainView()
    }

    override fun onLoginSuccess(baseUser: User) {
        userRepository.getUser(baseUser.getUid())
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe(object : SingleObserver<User> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(user: User) {
                    userRepository.insertOrUpdateUser(user)
                    loginView.showMainView()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    loginView.showGeneralErrorMessage()
                    loginView.showMainView()
                }
            })
    }

    override fun onRequestGoogleAccountSuccess(authCredential: AuthCredential, fromLastSignIn: Boolean) {
        clientAuth.signInWithCredential(authCredential)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe(object : SingleObserver<User> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(user: User) {
                    if (!fromLastSignIn) {
                        // New user registering
                        onRegisterSuccess(user)
                    } else {
                        userRepository.insertOrUpdateUser(user)
                        onLoginSuccess(user)
                    }
                }

                override fun onError(e: Throwable) {
                    loginView.showSignInFailMessage()
                }
            })
    }

    // TODO: Check is new account
    override fun onRequestFacebookAccountSuccess(authCredential: AuthCredential) {
        // DO Nothing
        /*dataManager.signInWithCredential(authCredential)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe(object : SingleObserver<User> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(user: User) {
                    ensureBasicUserData(user)
                    dataManager.updateCurrentUser(user)
                    onLoginSuccess(user)
                }

                override fun onError(e: Throwable) {
                    loginView.showSignInFailMessage()
                }
            })*/
    }

    private fun ensureBasicUserData(user: User) {
        if (user.name.isBlank()) {
            user.name = getNameFromEmail(user.email)
        }

        if (user.getUserStoreLists().isEmpty())
            user.setUserStoreLists(getDefaultUserStoreLists().toMutableList())
    }
}