package com.iceteaviet.fastfoodfinder.ui.login

import com.google.firebase.auth.AuthCredential
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
        fun showGeneralErrorMessage()
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onSkipButtonClick()
        fun onRegisterSuccess(user: User)
        fun onLoginSuccess(baseUser: User)
        fun onRequestGoogleAccountSuccess(authCredential: AuthCredential, fromLastSignIn: Boolean)
        fun onRequestFacebookAccountSuccess(authCredential: AuthCredential)
    }
}