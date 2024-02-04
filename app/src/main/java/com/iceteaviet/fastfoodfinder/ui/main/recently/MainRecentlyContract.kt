package com.iceteaviet.fastfoodfinder.ui.main.recently

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface MainRecentlyContract {
    interface View : BaseView<Presenter> {
        fun setStores(stores: ArrayList<Store>)
        fun showStoreDetailView(store: Store)
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onStoreItemClick(store: Store)
    }
}