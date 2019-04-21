package com.iceteaviet.fastfoodfinder.ui.splash

import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface SplashContract {
    interface View : BaseView<Presenter> {
        fun openLoginScreen()
        fun openMainActivityWithDelay(startTime: Long)
        fun exit()
        fun showRetryDialog()
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun loadStoresFromServer()
    }
}