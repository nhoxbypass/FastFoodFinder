package com.iceteaviet.fastfoodfinder.ui.splash

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.filterInvalidData
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.CompletableObserver
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

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, splashView: SplashContract.View) : super(dataManager, schedulerProvider) {
        this.splashView = splashView
        this.splashView.presenter = this
    }

    override fun subscribe() {
        startTime = System.currentTimeMillis()

        if (dataManager.getPreferencesHelper().getAppLaunchFirstTime()) {
            onAppOpenFirstTime()
        } else if (dataManager.getPreferencesHelper().getNumberOfStores() == 0) {
            loadStoresFromServer()
        } else {
            if (dataManager.isSignedIn()) {
                val uid = dataManager.getCurrentUserUid()
                if (isValidUserUid(uid)) {
                    onUserSignedIn(uid)
                }
            } else {
                onUserNotSignedIn()
            }
        }
    }

    override fun loadStoresFromServer() {
        loadStoresFromServerInternal()
                .subscribe(object : CompletableObserver {
                    override fun onComplete() {
                        if (dataManager.isSignedIn())
                            splashView.openMainActivityWithDelay(getSplashRemainingTime())
                        else
                            splashView.openLoginScreen()
                    }

                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                })
    }

    private fun getSplashRemainingTime(): Long {
        return SplashActivity.SPLASH_DELAY_TIME - (System.currentTimeMillis() - startTime)
    }

    private fun onAppOpenFirstTime() {
        val disposable = loadStoresFromServerInternal()
                .subscribe {
                    dataManager.getPreferencesHelper().setAppLaunchFirstTime(false)
                    splashView.openLoginScreen()
                }

        compositeDisposable.add(disposable)
    }

    /**
     * Download data from Firebase and store in Realm
     */
    private fun loadStoresFromServerInternal(): Completable {
        return Completable.create { emitter ->
            dataManager.loadStoresFromServer()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<List<Store>> {
                        override fun onSubscribe(d: Disposable) {
                            emitter.setDisposable(d)
                        }

                        override fun onSuccess(storeList: List<Store>) {
                            val filteredStoreList = filterInvalidData(storeList.toMutableList())
                            dataManager.getPreferencesHelper().setNumberOfStores(filteredStoreList.size)
                            dataManager.setStores(filteredStoreList)

                            emitter.onComplete()
                        }

                        override fun onError(e: Throwable) {
                            splashView.showRetryDialog()
                            emitter.onError(e)
                        }
                    })
        }
    }

    private fun onUserNotSignedIn() {
        // Warm up store data
        dataManager.getAllStores()
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

    private fun onUserSignedIn(userUid: String) {
        // User still signed in, fetch newest user data from server
        // TODO: Support timeout
        Single.zip(dataManager.getUser(userUid),
                dataManager.getAllStores(),
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
}