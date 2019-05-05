package com.iceteaviet.fastfoodfinder.ui.settings.discountnotify

class DiscountNotifyPresenter : DiscountNotifyContract.Presenter {

    private val discountNotifyView: DiscountNotifyContract.View;


    constructor(view: DiscountNotifyContract.View) {
        discountNotifyView = view
    }

    override fun unsubscribe() {
    }

    override fun subscribe() {
    }

    override fun onCancelButtonClick() {
        discountNotifyView.cancelDialog()
    }

    override fun onDoneButtonClick() {
        discountNotifyView.doneDialog()
    }

    companion object {
        private const val KEY_CIRCLE_K = "circle_k"
        private const val KEY_MINI_STOP = "mini_stop"
        private const val KEY_FAMILY_MART = "family_mark"
        private const val KEY_BSMART = "bsmart"
        private const val KEY_SHOP_N_GO = "shop_n_go"

        private val LIST_STORES = arrayOf(KEY_BSMART, KEY_CIRCLE_K, KEY_FAMILY_MART, KEY_MINI_STOP, KEY_SHOP_N_GO)
    }

    override fun onSetupTagContainer() {
        discountNotifyView.setupTagContainer(LIST_STORES)
    }


    override fun getStoreName(key: String): String {
        return when (key) {
            KEY_CIRCLE_K -> "Cirle K"
            KEY_BSMART -> "B’s mart"
            KEY_FAMILY_MART -> "Family mart"
            KEY_MINI_STOP -> "Ministop"
            KEY_SHOP_N_GO -> "Shop & Go"
            else -> "Unknown"
        }
    }
}