package com.iceteaviet.fastfoodfinder.ui.main.map.storeinfo

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider

class StoreInfoPresenter : BasePresenter<StoreInfoContract.Presenter>, StoreInfoContract.Presenter {
    private var store: Store? = null
    private val storeInfoView: StoreInfoContract.View

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, view: StoreInfoContract.View) : super(dataManager, schedulerProvider) {
        storeInfoView = view
    }

    override fun subscribe() {
    }

    override fun handleExtras(store: Store?) {
        this.store = store
        if (store != null)
            storeInfoView.updateNewStoreUI(store)
        else
            storeInfoView.exit()
    }

    override fun setOnDetailTextViewClick() {
        storeInfoView.openStoreDetailActivity(store)
    }

    override fun onMakeCallWithPermission() {
        if (store!!.tel.isNotEmpty()) {
            storeInfoView.makeNativeCall(store!!.tel)
        } else {
            storeInfoView.showEmptyTelToast()
        }
    }

    override fun onAddToFavoriteButtonClick() {
        store?.let {
            storeInfoView.addStoreToFavorite(it)
        }
    }

    override fun onDirectionButtonClick() {
        store?.let {
            storeInfoView.onDirectionChange(it)
        }
    }
}