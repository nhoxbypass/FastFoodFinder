package com.iceteaviet.fastfoodfinder.ui.main.favourite

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreEvent
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by tom on 2019-04-18.
 */
class MainFavPresenter : BasePresenter<MainFavContract.Presenter>, MainFavContract.Presenter {

    private val mainFavView: MainFavContract.View

    constructor(dataManager: DataManager, mainFavView: MainFavContract.View) : super(dataManager) {
        this.mainFavView = mainFavView
        this.mainFavView.presenter = this
    }

    override fun subscribe() {
        val currUser = dataManager.getCurrentUser()
        if (currUser != null) {
            loadStoreListsFromIds(currUser.getFavouriteStoreList().getStoreIdList())
            listenFavStoresOfUser(dataManager.getCurrentUserUid())
        }
    }

    override fun unsubscribe() {
        dataManager.unsubscribeFavouriteStoresOfUser(dataManager.getCurrentUserUid())
        super.unsubscribe()
    }

    override fun onStoreItemClick(store: Store) {
        mainFavView.showStoreDetailView(store)
    }

    private fun loadStoreListsFromIds(storeIdList: MutableList<Int>) {
        dataManager.findStoresByIds(storeIdList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        mainFavView.setStores(storeList)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    private fun listenFavStoresOfUser(userUid: String) {
        dataManager.subscribeFavouriteStoresOfUser(userUid)
                .subscribeOn(Schedulers.io())
                .map { storeIdPair ->
                    val store = dataManager.findStoresById(storeIdPair.first).blockingGet()[0]
                    UserStoreEvent(store, storeIdPair.second)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<UserStoreEvent> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onNext(userStoreEvent: UserStoreEvent) {
                        handleUserStoreEvent(userStoreEvent)
                    }

                    override fun onError(e: Throwable) {
                        mainFavView.showWarningMessage(e.message)
                    }

                    override fun onComplete() {

                    }
                })
    }

    private fun handleUserStoreEvent(userStoreEvent: UserStoreEvent) {
        val store = userStoreEvent.store
        when (userStoreEvent.eventActionCode) {
            UserStoreEvent.ACTION_ADDED -> {
                val user = dataManager.getCurrentUser()
                if (user != null && !user.getFavouriteStoreList().getStoreIdList().contains(store.id)) {
                    mainFavView.addStore(store)
                    user.getFavouriteStoreList().getStoreIdList().add(store.id)
                }
            }

            UserStoreEvent.ACTION_CHANGED -> {
                val user = dataManager.getCurrentUser()
                if (user != null && user.getFavouriteStoreList().getStoreIdList().contains(store.id)) {
                    mainFavView.updateStore(store)
                }
            }

            UserStoreEvent.ACTION_REMOVED -> {
                val user = dataManager.getCurrentUser()
                if (user != null && user.getFavouriteStoreList().getStoreIdList().contains(store.id)) {
                    mainFavView.removeStore(store)
                    user.getFavouriteStoreList().removeStore(store.id)
                }
            }

            UserStoreEvent.ACTION_MOVED -> {
            }
        }
    }
}