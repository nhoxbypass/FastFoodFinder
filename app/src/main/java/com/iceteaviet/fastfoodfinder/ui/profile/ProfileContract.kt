package com.iceteaviet.fastfoodfinder.ui.profile

import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface ProfileContract {
    interface View : BaseView<Presenter> {
        fun openLoginActivity()
        fun loadAvatarPhoto(photoUrl: String)
        fun setName(name: String)
        fun setEmail(email: String)
        fun setStoreListCount(storeCount: String)
        fun setUserStoreLists(userStoreLists: List<UserStoreList>)
        fun addUserStoreList(list: UserStoreList)
        fun setSavedStoreCount(size: Int)
        fun setFavouriteStoreCount(size: Int)
        fun showCreateNewListDialog()
        fun dismissCreateNewListDialog()
        fun warningListNameExisted()
        fun openListDetail(userStoreList: UserStoreList, photoUrl: String)
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onCreateNewListButtonClick()
        fun onCreateNewList(listName: String, iconId: Int)
        fun onSavedListClick()
        fun onFavouriteListClick()
        fun onStoreListClick(listPacket: UserStoreList)
        fun onStoreListLongClick(position: Int)
    }
}