package com.iceteaviet.fastfoodfinder.ui.main.search

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.transport.model.SearchEventResult
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.Constant
import com.iceteaviet.fastfoodfinder.utils.getStoreSearchString
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus

/**
 * Created by tom on 2019-04-18.
 */
class SearchPresenter : BasePresenter<SearchContract.Presenter>, SearchContract.Presenter {

    private val searchView: SearchContract.View

    private var searchString: String = ""

    constructor(dataManager: DataManager, searchView: SearchContract.View) : super(dataManager) {
        this.searchView = searchView
        this.searchView.presenter = this
    }

    override fun subscribe() {
        val searchHistories = dataManager.getSearchHistories().toList().asReversed()
        searchView.setSearchHistory(searchHistories, getStoresFromIds(searchHistories))
    }

    override fun onStoreSearchClick(store: Store) {
        if (store.id == -1)
            EventBus.getDefault().post(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT, store.title, store))
        else
            EventBus.getDefault().post(SearchEventResult(SearchEventResult.SEARCH_ACTION_STORE_CLICK, store.title, store))
    }

    override fun onQuickSearchItemClick(storeType: Int) {
        searchString = getStoreSearchString(storeType)
        EventBus.getDefault().post(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, searchString, storeType))
    }

    override fun onUpdateSearchList(searchText: String) {
        dataManager.getLocalStoreDataSource()
                .findStores(searchText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        searchView.setSearchStores(storeList)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    override fun onTopStoreButtonClick() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNearestStoreButtonClick() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTrendingStoreButtonClick() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConvenienceStoreButtonClick() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getStoresFromIds(searchHistories: List<String>): List<Store> {
        val stores: MutableList<Store> = ArrayList()
        for (history in searchHistories) {
            if (history.contains(Constant.SEARCH_STORE_PREFIX)) {
                stores.addAll(dataManager.getLocalStoreDataSource()
                        .findStoresById(history.substring(Constant.SEARCH_STORE_PREFIX_LEN).toInt())
                        .blockingGet())
            } else {
                // Add fake store that represent a search query
                stores.add(Store(-1, history, "", "0", "0", "", -1))
            }
        }

        return stores
    }
}