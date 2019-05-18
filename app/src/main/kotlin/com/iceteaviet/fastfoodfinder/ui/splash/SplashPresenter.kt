package com.iceteaviet.fastfoodfinder.ui.splash

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.filterInvalidData
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers


/**
 * Created by tom on 2019-04-18.
 */
class SplashPresenter : BasePresenter<SplashContract.Presenter>, SplashContract.Presenter {

    private val splashView: SplashContract.View
    private var startTime: Long = 0L

    constructor(dataManager: DataManager, splashView: SplashContract.View) : super(dataManager) {
        this.splashView = splashView
        this.splashView.presenter = this
    }

    override fun subscribe() {
        startTime = System.currentTimeMillis()

        if (dataManager.getPreferencesHelper().getAppLaunchFirstTime() || dataManager.getPreferencesHelper().getNumberOfStores() == 0) {
            // Download data from Firebase and store in Realm
            loadStoresFromServer()
        } else {
            if (dataManager.isSignedIn()) {
                val uid = dataManager.getCurrentUserUid()
                if (isValidUserUid(uid)) {
                    // User still signed in, fetch newest user data from server
                    // TODO: Support timeout
                    Single.zip(dataManager.getRemoteUserDataSource().getUser(uid),
                            dataManager.getLocalStoreDataSource().getAllStores(),
                            BiFunction<User, List<Store>, Pair<User, List<Store>>> { user, storeList ->
                                Pair(user, storeList)
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(object : SingleObserver<Pair<User, List<Store>>> {
                                override fun onSubscribe(d: Disposable) {
                                    compositeDisposable.add(d)
                                }

                                override fun onSuccess(pair: Pair<User, List<Store>>) {
                                    dataManager.setCurrentUser(pair.first)

                                    if (pair.second.isEmpty())
                                        loadStoresFromServer()
                                    else
                                        splashView.openMainActivityWithDelay(getSplashRemainingTime())
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                    splashView.openMainActivityWithDelay(getSplashRemainingTime())
                                }
                            })
                }
            } else {
                // Warm up store data
                dataManager.getLocalStoreDataSource().getAllStores()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(object : SingleObserver<List<Store>> {
                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable.add(d)
                            }

                            override fun onSuccess(storeList: List<Store>) {
                                if (storeList.isEmpty())
                                    loadStoresFromServer()
                                else
                                    splashView.openLoginScreen()
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                                splashView.openLoginScreen()
                            }
                        })
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

                        val filteredStoreList = filterInvalidData(storeList.toMutableList())
                        dataManager.getPreferencesHelper().setNumberOfStores(filteredStoreList.size)
                        dataManager.getLocalStoreDataSource().setStores(filteredStoreList)

                        if (dataManager.isSignedIn())
                            splashView.openMainActivityWithDelay(getSplashRemainingTime())
                        else
                            splashView.openLoginScreen()
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()

                        splashView.showRetryDialog()
                    }
                })
    }

    private fun getSplashRemainingTime(): Long {
        return SplashActivity.SPLASH_DELAY_TIME - (System.currentTimeMillis() - startTime)
    }
}