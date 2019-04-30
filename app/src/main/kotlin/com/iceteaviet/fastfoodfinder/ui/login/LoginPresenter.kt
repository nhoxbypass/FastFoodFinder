package com.iceteaviet.fastfoodfinder.ui.login

import android.content.Intent
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by tom on 2019-04-18.
 */
class LoginPresenter : BasePresenter<LoginContract.Presenter>, LoginContract.Presenter {

    private val loginView: LoginContract.View

    constructor(dataManager: DataManager, loginView: LoginContract.View) : super(dataManager) {
        this.loginView = loginView
        this.loginView.presenter = this
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
        dataManager.getRemoteUserDataSource().insertOrUpdate(user)
        dataManager.setCurrentUser(user)
        loginView.showMainView()
    }

    override fun onLoginSuccess(user: User) {
        dataManager.getRemoteUserDataSource().getUser(user.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<User> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(user: User) {
                        dataManager.setCurrentUser(user)
                        loginView.showMainView()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        dataManager.setCurrentUser(user)
                        loginView.showMainView()
                    }
                })
    }

    override fun onRequestGoogleAccountResult(data: Intent) {
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        if (task.isSuccessful) {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)
            if (account != null)
                onRequestGoogleAccountSuccess(account, false)
            else
                loginView.showSignInFailMessage()
        } else {
            loginView.showSignInFailMessage()
        }
    }

    override fun onRequestGoogleAccountSuccess(account: GoogleSignInAccount, fromLastSignIn: Boolean) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        dataManager.signInWithCredential(credential)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<User> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(user: User) {
                        if (!fromLastSignIn) {
                            dataManager.getRemoteUserDataSource().insertOrUpdate(user)
                        }

                        onLoginSuccess(user)
                    }

                    override fun onError(e: Throwable) {
                        loginView.showSignInFailMessage()
                    }
                })
    }

    // TODO: Check is new account
    override fun onRequestFacebookAccountSuccess(accessToken: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(accessToken.token)
        dataManager.signInWithCredential(credential)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<User> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(user: User) {
                        dataManager.getRemoteUserDataSource().insertOrUpdate(user)
                        dataManager.setCurrentUser(user)
                        loginView.showMainView()
                    }

                    override fun onError(e: Throwable) {
                        loginView.showSignInFailMessage()
                    }
                })
    }
}