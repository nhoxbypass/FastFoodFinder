package com.iceteaviet.fastfoodfinder.ui.main.map.storeinfo

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

interface StoreInfoContract {
    interface View : BaseView<Presenter> {
        fun updateNewStoreUI(store: Store?)
        fun openStoreDetailActivity(store: Store?)
        fun makeNativeCall(tel: String)
        fun addStoreToFavorite(store: Store)
        fun onDirectionChange(store: Store)
        fun showEmptyTelToast()
        fun exit()

    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun handleExtras(store: Store?)
        fun setOnDetailTextViewClick()
        fun onMakeCallWithPermission()
        fun onAddToFavoriteButtonClick()
        fun onDirectionButtonClick()
    }
}