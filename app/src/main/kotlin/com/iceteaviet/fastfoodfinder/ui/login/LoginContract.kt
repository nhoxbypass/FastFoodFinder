package com.iceteaviet.fastfoodfinder.ui.login

import android.content.Intent
import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface LoginContract {
    interface View : BaseView<Presenter> {
        fun exit()
        fun showMainView()
        fun showSignInFailMessage()
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onSkipButtonClick()
        fun onEmailRegisterSuccess(user: User)
        fun onLoginSuccess(user: User)
        fun onRequestGoogleAccountSuccess(account: GoogleSignInAccount, fromLastSignIn: Boolean)
        fun onRequestGoogleAccountResult(data: Intent)
        fun onRequestFacebookAccountSuccess(accessToken: AccessToken)
    }
}