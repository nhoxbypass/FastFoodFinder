package com.iceteaviet.fastfoodfinder.ui.main.recently

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider

/**
 * Created by tom on 2019-04-18.
 */
class MainRecentlyPresenter(
    schedulerProvider: SchedulerProvider,
    private val mainRecentlyView: MainRecentlyContract.View
) : BasePresenter<MainRecentlyContract.Presenter>(schedulerProvider), MainRecentlyContract.Presenter {



    override fun subscribe() {
        val stores = ArrayList<Store>()
        //TODO: Load recently store from Realm

        mainRecentlyView.setStores(stores)
    }

    override fun onStoreItemClick(store: Store) {
        mainRecentlyView.showStoreDetailView(store)
    }
}