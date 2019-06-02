package com.iceteaviet.fastfoodfinder.ui.storelist

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreList
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider

/**
 * Created by tom on 2019-04-18.
 */
class StoreListPresenter : BasePresenter<StoreListContract.Presenter>, StoreListContract.Presenter {

    private val storeListView: StoreListContract.View

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, storeListView: StoreListContract.View) : super(dataManager, schedulerProvider) {
        this.storeListView = storeListView
        this.storeListView.presenter = this
    }

    override fun subscribe() {
        storeListView.setStores(getFakeStoreList())
    }
}