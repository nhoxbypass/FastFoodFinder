package com.iceteaviet.fastfoodfinder.ui.storelist

import androidx.annotation.DrawableRes
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface ListDetailContract {
    interface View : BaseView<Presenter> {
        fun setStores(storeList: List<Store>)
        fun exit()
        fun setListNameText(listName: String)
        fun loadStoreIcon(@DrawableRes storeIcon: Int)
        fun showGeneralErrorMessage()
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun handleExtras(userStoreList: UserStoreList?, photoUrl: String?)
    }
}