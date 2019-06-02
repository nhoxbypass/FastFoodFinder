package com.iceteaviet.fastfoodfinder.ui.main.recently

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import java.util.*

/**
 * Created by tom on 2019-04-18.
 */
class MainRecentlyPresenter : BasePresenter<MainRecentlyContract.Presenter>, MainRecentlyContract.Presenter {

    private val mainRecentlyView: MainRecentlyContract.View

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, mainRecentlyView: MainRecentlyContract.View) : super(dataManager, schedulerProvider) {
        this.mainRecentlyView = mainRecentlyView
        this.mainRecentlyView.presenter = this
    }

    override fun subscribe() {
        val stores = ArrayList<Store>()
        //TODO: Load recently store from Realm

        mainRecentlyView.setStores(stores)
    }

    override fun onStoreItemClick(store: Store) {
        mainRecentlyView.showStoreDetailView(store)
    }
}