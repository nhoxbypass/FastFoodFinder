package com.iceteaviet.fastfoodfinder.ui.main.search

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BaseView
import com.iceteaviet.fastfoodfinder.ui.main.search.model.SearchStoreItem

/**
 * Created by tom on 2019-04-18.
 */
interface SearchContract {
    interface View : BaseView<Presenter> {
        fun setSearchHistory(searchHistory: List<String>, searchItems: List<SearchStoreItem>)
        fun setSearchStores(searchItems: List<SearchStoreItem>)
        fun showStoreListView()
        fun showGeneralErrorMessage()
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onStoreSearchClick(store: Store)
        fun onQuickSearchItemClick(storeType: Int)
        fun onUpdateSearchList(searchText: String)
        fun onTopStoreButtonClick()
        fun onNearestStoreButtonClick()
        fun onTrendingStoreButtonClick()
        fun onConvenienceStoreButtonClick()
    }
}