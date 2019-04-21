package com.iceteaviet.fastfoodfinder.ui.splash

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.utils.openLoginActivity
import com.iceteaviet.fastfoodfinder.utils.openMainActivity


class SplashActivity : BaseActivity(), SplashContract.View {
    override lateinit var presenter: SplashContract.Presenter

    companion object {
        const val SPLASH_DELAY_TIME = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SplashPresenter(App.getDataManager(), this)
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    override fun openLoginScreen() {
        openLoginActivity(this)
    }

    override fun exit() {
        finish()
    }

    override fun openMainActivityWithDelay(startTime: Long) {
        val remainTime = SPLASH_DELAY_TIME - (System.currentTimeMillis() - startTime)
        if (remainTime > 0) {
            Handler(Looper.getMainLooper())
                    .postDelayed({
                        openMainActivity(this)
                    }, remainTime)
        } else {
            openMainActivity(this)
        }
    }

    override fun showRetryDialog() {
        AlertDialog.Builder(this@SplashActivity)
                .setTitle(getString(R.string.title_retry_update_db))
                .setMessage(getString(R.string.msg_retry_update_db))
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    dialog.dismiss()
                    presenter.loadStoresFromServer()
                }
                .setNegativeButton(android.R.string.no) { dialog, which ->
                    dialog.dismiss()
                    exit()
                }
                .show()
    }

    override val layoutId: Int
        get() = R.layout.activity_splash
}
