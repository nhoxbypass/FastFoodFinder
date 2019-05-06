package com.iceteaviet.fastfoodfinder.ui.login.emailregister

import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface EmailRegisterContract {
    interface View : BaseView<Presenter> {
        fun setRegisterButtonProgress(progress: Int)
        fun setInputEnabled(enabled: Boolean)
        fun showInvalidPasswordError()
        fun showInvalidRePasswordError()
        fun showInvalidEmailError()
        fun notifyRegisterSuccess(user: User)
        fun notifyLoginError(e: Throwable)
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onSignUpButtonClicked(email: String, password: String, rePassword: String)
    }
}