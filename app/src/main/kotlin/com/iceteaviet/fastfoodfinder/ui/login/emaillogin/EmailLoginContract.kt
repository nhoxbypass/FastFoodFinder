package com.iceteaviet.fastfoodfinder.ui.login.emaillogin

import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface EmailLoginContract {
    interface View : BaseView<Presenter> {
        fun setLoginButtonProgress(progress: Int)
        fun setInputEnabled(enabled: Boolean)
        fun showInvalidPasswordError()
        fun showInvalidEmailError()
        fun notifyLoginSuccess(user: User)
        fun notifyLoginError(e: Throwable)
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onSignInButtonClicked(email: String, password: String)
    }
}