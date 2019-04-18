package com.iceteaviet.fastfoodfinder.ui.profile

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Created by tom on 2019-04-18.
 */
class ProfilePresenter : BasePresenter<ProfileContract.Presenter>, ProfileContract.Presenter {

    val profileView: ProfileContract.View
    private var defaultList: MutableList<UserStoreList> = ArrayList() // Default store list (saved, favourite) that every user have

    constructor(dataManager: DataManager, profileView: ProfileContract.View) : super(dataManager) {
        this.profileView = profileView
        this.profileView.presenter = this
    }

    override fun subscribe() {
        // Invalid auth token -> go to login screen
        if (!dataManager.isSignedIn())
            profileView.openLoginActivity()
        else
            loadCurrentUserData()
    }

    override fun onCreateNewListButtonClick() {
        profileView.showCreateNewListDialog()
    }

    override fun onCreateNewList(listName: String, iconId: Int) {
        if (!isListNameExisted(listName)) {
            val currentUser = dataManager.getCurrentUser()
            val id = currentUser!!.getUserStoreLists().size //New id = current size
            val list = UserStoreList(id, ArrayList(), iconId, listName)
            currentUser.addStoreList(list)
            dataManager.getRemoteUserDataSource().updateStoreListForUser(currentUser.uid, currentUser.getUserStoreLists())

            profileView.addUserStoreList(list)
            profileView.setStoreListCount(String.format("(%d)", currentUser.getUserStoreLists().size))
            profileView.dismissCreateNewListDialog()
        } else {
            profileView.warningListNameExisted()
        }
    }

    override fun onSavedListClick() {
        onStoreListClick(defaultList[UserStoreList.ID_SAVED])
    }

    override fun onFavouriteListClick() {
        onStoreListClick(defaultList[UserStoreList.ID_FAVOURITE])
    }

    override fun onStoreListClick(listPacket: UserStoreList) {
        profileView.openListDetail(listPacket, dataManager.getCurrentUser()!!.photoUrl)
    }

    // TODO: Support dialog asking user want to delete or not
    override fun onStoreListLongClick(position: Int) {
        val currentUser = dataManager.getCurrentUser()
        currentUser!!.removeStoreList(position)
        dataManager.getRemoteUserDataSource().updateStoreListForUser(currentUser.uid, currentUser.getUserStoreLists())

        profileView.setStoreListCount(String.format("(%d)", currentUser.getUserStoreLists().size))
    }

    private fun loadCurrentUserData() {
        val uid = dataManager.getCurrentUserUid()
        if (!isValidUserUid(uid))
            return

        dataManager.getRemoteUserDataSource().getUser(uid)
                .subscribe(object : SingleObserver<User> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(user: User) {
                        dataManager.setCurrentUser(user)
                        profileView.loadAvatarPhoto(user.photoUrl)
                        profileView.setName(user.name)
                        profileView.setEmail(user.email)
                        loadStoreLists()

                        profileView.setSavedStoreCount(user.getSavedStoreList().getStoreIdList()!!.size)
                        profileView.setFavouriteStoreCount(user.getFavouriteStoreList().getStoreIdList()!!.size)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    private fun loadStoreLists() {
        val currentUser = dataManager.getCurrentUser()
        for (i in 0 until currentUser!!.getUserStoreLists().size) {
            if (i <= 2) {
                defaultList.add(currentUser.getUserStoreLists()[i])
                break
            }
        }

        profileView.setUserStoreLists(currentUser.getUserStoreLists())
        profileView.setStoreListCount(String.format("(%d)", currentUser.getUserStoreLists().size))
    }

    private fun isListNameExisted(listName: String): Boolean {
        for (storeList in dataManager.getCurrentUser()!!.getUserStoreLists()) {
            if (listName == storeList.listName) {
                return false
            }
        }

        return true
    }
}