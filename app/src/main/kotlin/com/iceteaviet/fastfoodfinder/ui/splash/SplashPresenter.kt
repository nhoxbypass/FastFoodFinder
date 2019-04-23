package com.iceteaviet.fastfoodfinder.ui.splash

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.filterInvalidData
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by tom on 2019-04-18.
 */
class SplashPresenter : BasePresenter<SplashContract.Presenter>, SplashContract.Presenter {

    private val splashView: SplashContract.View

    constructor(dataManager: DataManager, splashView: SplashContract.View) : super(dataManager) {
        this.splashView = splashView
        this.splashView.presenter = this
    }

    override fun subscribe() {
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
                                    compositeDisposable.add(d)
                                }

                                override fun onSuccess(user: User) {
                                    dataManager.setCurrentUser(user)
                                    splashView.openMainActivityWithDelay(startTime)
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                    splashView.openMainActivityWithDelay(startTime)
                                }
                            })
                }
            } else {
                splashView.openLoginScreen()
            }
        }
    }

    override fun loadStoresFromServer() {
        dataManager.loadStoresFromServer()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        dataManager.getPreferencesHelper().setAppLaunchFirstTime(false)
                        dataManager.getPreferencesHelper().setNumberOfStores(storeList.size)
                        dataManager.getLocalStoreDataSource().setStores(filterInvalidData(storeList.toMutableList()))

                        splashView.openLoginScreen()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()

                        splashView.showRetryDialog()
                    }
                })
    }
}