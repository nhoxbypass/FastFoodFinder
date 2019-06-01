package com.iceteaviet.fastfoodfinder.ui.main.map.storeinfo

import android.os.Bundle
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.store.StoreDetailActivity

class StoreInfoPresenter : StoreInfoContract.Presenter {

    private var store: Store? = null
    private val storeInfoView: StoreInfoContract.View

    constructor(view: StoreInfoContract.View) {
        storeInfoView = view
    }

    override fun subscribe() {
    }

    override fun unsubscribe() {
    }

    override fun parseNewIntent(bundle: Bundle?) {
        if (bundle != null)
            store = bundle.getParcelable(StoreDetailActivity.KEY_STORE)
        storeInfoView.updateNewStoreUI(store)
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