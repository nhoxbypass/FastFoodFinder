package com.iceteaviet.fastfoodfinder.ui.splash

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.utils.filterInvalidData
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import com.iceteaviet.fastfoodfinder.utils.openLoginActivity
import com.iceteaviet.fastfoodfinder.utils.openMainActivity
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class SplashActivity : BaseActivity() {

    companion object {
        const val SPLASH_DELAY_TIME = 500
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataManager = App.getDataManager()

        if (dataManager.getPreferencesHelper().getAppLaunchFirstTime() || dataManager.getPreferencesHelper().getNumberOfStores() == 0) {
            // Download data from Firebase and store in Realm
            loadStoresFromServer()
        } else {
            if (dataManager.isSignedIn()) {
                val startTime = System.currentTimeMillis()
                val uid = dataManager.getCurrentUserUid()
                if (isValidUserUid(uid)) {
                    // User still signed in, fetch newest user data from server
                    // TODO: Support timeout
                    dataManager.getRemoteUserDataSource().getUser(uid)
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .subscribe(object : SingleObserver<User> {
                                override fun onSubscribe(d: Disposable) {

                                }

                                override fun onSuccess(user: User) {
                                    dataManager.setCurrentUser(user)
                                    openMainActivityWithDelay(startTime)
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                    openMainActivityWithDelay(startTime)
                                }
                            })
                }
            } else {
                openLoginActivity(this@SplashActivity)
            }
        }
    }

    private fun openMainActivityWithDelay(startTime: Long) {
        val remainTime = SPLASH_DELAY_TIME - (System.currentTimeMillis() - startTime)
        if (remainTime > 0) {
            Handler(Looper.getMainLooper())
                    .postDelayed({
                        openMainActivity(this@SplashActivity)
                    }, remainTime)
        } else {
            openMainActivity(this@SplashActivity)
        }
    }

    private fun loadStoresFromServer() {
        dataManager.loadStoresFromServer(this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(storeList: List<Store>) {
                        dataManager.getPreferencesHelper().setAppLaunchFirstTime(false)
                        dataManager.getPreferencesHelper().setNumberOfStores(storeList.size)
                        dataManager.getLocalStoreDataSource().setStores(filterInvalidData(storeList.toMutableList()))

                        openLoginActivity(this@SplashActivity)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()

                        AlertDialog.Builder(this@SplashActivity)
                                .setTitle(getString(com.iceteaviet.fastfoodfinder.R.string.title_retry_update_db))
                                .setMessage(getString(com.iceteaviet.fastfoodfinder.R.string.msg_retry_update_db))
                                .setPositiveButton(android.R.string.yes) { dialog, which ->
                                    dialog.dismiss()
                                    loadStoresFromServer()
                                }
                                .setNegativeButton(android.R.string.no) { dialog, which ->
                                    dialog.dismiss()
                                    finish()
                                }
                                .show()
                    }
                })
    }

    override val layoutId: Int
        get() = R.layout.activity_splash
}
