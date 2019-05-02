package com.iceteaviet.fastfoodfinder.data.auth.provider

import android.content.Intent
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.iceteaviet.fastfoodfinder.App

/**
 * Created by tom on 2019-05-03.
 */
class FacebookAuthHelper(private var loginButton: LoginButton) : AbsAuthHelper<AuthCredential>(), AuthHelper<AuthCredential> {

    private var callBackManager: CallbackManager? = null

    init {
        FacebookSdk.sdkInitialize(App.getContext())
    }

    override fun setupAuthProvider() {
        callBackManager = setupFacebookSignInClient()
        loginButton.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                listener?.onSuccess(FacebookAuthProvider.getCredential(loginResult.accessToken.token), false)
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException) {
                listener?.onFailed()
            }
        })
    }

    override fun startRequestAuthCredential() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        callBackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupFacebookSignInClient(): CallbackManager {
        val callbackManager = CallbackManager.Factory.create()
        return callbackManager
    }
}