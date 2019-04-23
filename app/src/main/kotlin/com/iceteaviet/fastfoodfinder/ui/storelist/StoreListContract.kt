package com.iceteaviet.fastfoodfinder.ui.storelist

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface StoreListContract {
    interface View : BaseView<Presenter> {
        fun setStores(stores: List<Store>)
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
    }
}