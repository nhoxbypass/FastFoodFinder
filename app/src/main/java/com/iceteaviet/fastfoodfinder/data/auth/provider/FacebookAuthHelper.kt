package com.iceteaviet.fastfoodfinder.data.auth.provider

/**
 * Created by tom on 2019-05-03.
 */
/*
class FacebookAuthHelper(private var loginButton: LoginButton) : AbsAuthHelper<AuthCredential>(), AuthHelper<AuthCredential> {

    private var callBackManager: CallbackManager? = null

    init {
        arrayOf<String?>("email", "public_profile")
        FacebookSdk.setClientToken(getString(R.string.facebook_app_id))
        FacebookSdk.sdkInitialize(App.getContext())
        setupAuthProvider()
    }

    override fun setupAuthProvider() {
        callBackManager = setupFacebookSignInClient()
        loginButton.registerCallback(callBackManager!!, object : FacebookCallback<LoginResult> {
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
        callBackManager?.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupFacebookSignInClient(): CallbackManager {
        val callbackManager = CallbackManager.Factory.create()
        return callbackManager
    }
}*/