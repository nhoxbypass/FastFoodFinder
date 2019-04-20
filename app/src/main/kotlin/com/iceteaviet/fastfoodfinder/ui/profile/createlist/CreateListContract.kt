package com.iceteaviet.fastfoodfinder.ui.profile.createlist

import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface CreateListContract {
    interface View : BaseView<Presenter> {
        fun notifyWithResult(storeName: String, iconId: Int)
        fun cancel()
        fun showEmptyNameWarning()
        fun updateSelectedIconUI(iconId: Int)
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onDoneButtonClick(name: String)
        fun onCancelButtonClick()
        fun onListIconSelect(iconId: Int)
    }
}