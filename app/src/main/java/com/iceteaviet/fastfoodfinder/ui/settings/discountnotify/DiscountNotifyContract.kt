package com.iceteaviet.fastfoodfinder.ui.settings.discountnotify

import com.iceteaviet.fastfoodfinder.ui.base.BaseView

interface DiscountNotifyContract {
    interface View : BaseView<Presenter> {
        fun cancelDialog()
        fun doneDialog()
        fun setupTagContainer(storeList: Array<String>)
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onCancelButtonClick()
        fun onDoneButtonClick()
        fun onSetupTagContainer()
        fun getStoreName(key: String): String
    }
}