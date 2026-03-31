package com.iceteaviet.fastfoodfinder.ui.splash


import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.exception.EmptyDataException
import com.iceteaviet.fastfoodfinder.utils.filterInvalidData
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction


/**
 * Created by tom on 2019-04-18.
 */
class SplashPresenter(
    private val clientAuth: com.iceteaviet.fastfoodfinder.data.auth.ClientAuth,
    private val userRepository: com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository,
    private val storeRepository: com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository,
    private val preferencesRepository: com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository,
    schedulerProvider: SchedulerProvider,
    private val splashView: SplashContract.View
) : BasePresenter<SplashContract.Presenter>(schedulerProvider), SplashContract.Presenter {

    private var startTime: Long = 0L

    override fun subscribe() {
        startTime = System.currentTimeMillis()

        if (preferencesRepository.getAppLaunchFirstTime()) {
            onAppOpenFirstTime()
        } else {
            if (clientAuth.isSignedIn()) {
                val uid = clientAuth.getCurrentUserUid()
                if (isValidUserUid(uid)) {
                    loadDataAndOpenMainScreen(uid)
                    return
                }
            }
            loadDataAndOpenLoginScreen()
        }
    }

    /**
     * Load stores data from server
     * Then navigate directly to main screen or login screen base on current authentication status
     *
     * Note: It will open main screen directly of user sign in, do not fetch new data of current user,
     *      you should do it by yourself
     */
    override fun loadStoresFromServer() {
        loadStoresFromServerInternal()
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    if (clientAuth.isSignedIn() && isValidUserUid(clientAuth.getCurrentUserUid()))
                        splashView.openMainScreenWithDelay(getSplashRemainingTime())
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
        preferencesRepository.setAppLaunchFirstTime(false)

        loadStoresFromServerInternal()
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    splashView.openLoginScreen()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    splashView.showGeneralErrorMessage()
                    splashView.openLoginScreen()
                }

            })
    }

    /**
     * Download data from Firebase and store in Realm
     */
    private fun loadStoresFromServerInternal(): Completable {
        return Completable.create { emitter ->
            com.iceteaviet.fastfoodfinder.utils.loadStoresFromServerHelper(com.iceteaviet.fastfoodfinder.App.getContext(), clientAuth, storeRepository)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        emitter.setDisposable(d)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        if (storeList.isNotEmpty()) {
                            val filteredStoreList = filterInvalidData(storeList.toMutableList())
                            storeRepository.setStores(filteredStoreList)

                            emitter.onComplete()
                        } else {
                            splashView.showRetryDialog()
                            emitter.onError(EmptyDataException())
                        }
                    }

                    override fun onError(e: Throwable) {
                        splashView.showRetryDialog()
                        emitter.onError(e)
                    }
                })
        }
    }

    private fun loadDataAndOpenLoginScreen() {
        // Warm up store data
        storeRepository.getAllStores()
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
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
                    splashView.showGeneralErrorMessage()
                    splashView.openLoginScreen()
                }
            })
    }

    private fun loadDataAndOpenMainScreen(userUid: String) {
        // User still signed in, fetch newest user data from server
        // TODO: Support timeout
        Single.zip(userRepository.getUser(userUid),
            storeRepository.getAllStores(),
            BiFunction<User, List<Store>, Pair<User, List<Store>>> { user, storeList ->
                Pair(user, storeList)
            })
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe(object : SingleObserver<Pair<User, List<Store>>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(pair: Pair<User, List<Store>>) {
                    userRepository.insertOrUpdateUser(pair.first)

                    if (pair.second.isEmpty())
                        loadStoresFromServer()
                    else
                        splashView.openMainScreenWithDelay(getSplashRemainingTime())
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    splashView.showGeneralErrorMessage()
                    splashView.openMainScreenWithDelay(getSplashRemainingTime())
                }
            })
    }
}