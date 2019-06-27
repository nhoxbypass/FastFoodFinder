package com.iceteaviet.fastfoodfinder.ui.main.search

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.service.eventbus.SearchEventResult
import com.iceteaviet.fastfoodfinder.service.eventbus.core.IBus
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.ui.main.search.model.SearchStoreItem
import com.iceteaviet.fastfoodfinder.utils.Constant
import com.iceteaviet.fastfoodfinder.utils.getStoreSearchString
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

/**
 * Created by tom on 2019-04-18.
 */
class SearchPresenter : BasePresenter<SearchContract.Presenter>, SearchContract.Presenter {

    private val searchView: SearchContract.View
    private val bus: IBus

    private var searchString: String = ""

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, bus: IBus, searchView: SearchContract.View) : super(dataManager, schedulerProvider) {
        this.searchView = searchView
        this.bus = bus
    }

    override fun subscribe() {
        val searchHistories = dataManager.getSearchHistories().toList().asReversed()
        if (searchHistories.isNotEmpty())
            searchView.setSearchHistory(searchHistories, getStoresFromIds(searchHistories))
    }

    override fun onStoreSearchClick(store: Store) {
        if (store.id == -1)
            bus.post(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT, store.title, store))
        else
            bus.post(SearchEventResult(SearchEventResult.SEARCH_ACTION_STORE_CLICK, store.title, store))
    }

    override fun onQuickSearchItemClick(storeType: Int) {
        searchString = getStoreSearchString(storeType)
        bus.post(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, searchString, storeType))
    }

    override fun onUpdateSearchList(searchText: String) {
        dataManager.findStores(searchText)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        searchView.setSearchStores(storeListToSearchItems(storeList))
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        searchView.showGeneralErrorMessage()
                    }
                })
    }

    override fun onTopStoreButtonClick() {
    }

    override fun onNearestStoreButtonClick() {
    }

    override fun onTrendingStoreButtonClick() {
    }

    override fun onConvenienceStoreButtonClick() {
    }

    private fun getStoresFromIds(searchHistories: List<String>): List<SearchStoreItem> {
        val searchItems: MutableList<SearchStoreItem> = ArrayList()
        for (history in searchHistories) {
            if (history.contains(Constant.SEARCH_STORE_PREFIX)) {
                try {
                    val store = dataManager.findStoreById(history.substring(Constant.SEARCH_STORE_PREFIX_LEN).toInt())
                            .blockingGet()
                    searchItems.add(SearchStoreItem(store, ""))
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            } else {
                // Add fake search item that represent a search query
                searchItems.add(SearchStoreItem(null, history))
            }
        }

        return searchItems
    }

    private fun storeListToSearchItems(stores: List<Store>): List<SearchStoreItem> {
        val searchItems: MutableList<SearchStoreItem> = ArrayList()
        for (store in stores) {
            searchItems.add(SearchStoreItem(store, ""))
        }
        return searchItems
    }
}