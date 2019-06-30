package com.iceteaviet.fastfoodfinder.ui.main.favourite

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface MainFavContract {
    interface View : BaseView<Presenter> {
        fun setStores(storeList: List<Store>)
        fun addStore(store: Store)
        fun updateStore(store: Store)
        fun removeStore(store: Store)
        fun showWarningMessage(message: String?)
        fun showStoreDetailView(store: Store)
        fun showGeneralErrorMessage()
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onStoreItemClick(store: Store)
    }
}