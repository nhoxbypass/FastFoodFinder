package com.iceteaviet.fastfoodfinder.ui.splash

import javax.inject.Inject
import dagger.hilt.android.AndroidEntryPoint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.utils.openLoginActivity
import com.iceteaviet.fastfoodfinder.utils.openMainActivity
import com.iceteaviet.fastfoodfinder.utils.openSplashActivity


@AndroidEntryPoint
class SplashActivity : BaseActivity(), SplashContract.View {
    @Inject
    lateinit var storeRepository: com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository

    @Inject
    lateinit var userRepository: com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository

    @Inject
    lateinit var preferencesRepository: com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository

    @Inject
    lateinit var clientAuth: com.iceteaviet.fastfoodfinder.data.auth.ClientAuth

    @Inject
    lateinit var schedulerProvider: com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider


    override lateinit var presenter: SplashContract.Presenter

    companion object {
        const val SPLASH_DELAY_TIME = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SplashPresenter(clientAuth, userRepository, storeRepository, preferencesRepository, schedulerProvider, this)
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
        finish()
    }

    override fun exit() {
        finish()
    }

    override fun openMainScreenWithDelay(delayTime: Long) {
        if (delayTime > 0) {
            Handler(Looper.getMainLooper())
                .postDelayed({
                    openMainActivity(this)
                    finish()
                }, delayTime)
        } else {
            openMainActivity(this)
            finish()
        }
    }

    override fun restartSplashScreen() {
        openSplashActivity(this)
        finish()
    }


    override fun showGeneralErrorMessage() {
        Toast.makeText(this, R.string.error_general_error_code, Toast.LENGTH_LONG).show()
    }

    override fun showRetryDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.title_retry_update_db))
            .setMessage(getString(R.string.msg_retry_update_db))
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                presenter.loadStoresFromServer()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
                exit()
            }
            .show()
    }

    override val layoutId: Int
        get() = R.layout.activity_splash
}
