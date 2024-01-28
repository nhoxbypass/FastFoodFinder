package com.iceteaviet.fastfoodfinder.ui.profile

import androidx.annotation.VisibleForTesting
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.isValidUserUid
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

/**
 * Created by tom on 2019-04-18.
 */
class ProfilePresenter : BasePresenter<ProfileContract.Presenter>, ProfileContract.Presenter {

    private val profileView: ProfileContract.View

    @VisibleForTesting
    var defaultList: MutableList<UserStoreList> = ArrayList() // Default store list (saved, favourite) that every user have

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, profileView: ProfileContract.View) : super(dataManager, schedulerProvider) {
        this.profileView = profileView
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

    // TODO: Check valid list name using FormatUtils
    override fun onCreateNewList(listName: String, iconId: Int) {
        val currentUser = dataManager.getCurrentUser() ?: return

        if (!isListNameExisted(listName, currentUser)) {
            val id = currentUser.getUserStoreLists().size // New id = current size
            val list = UserStoreList(id, ArrayList(), iconId, listName)
            currentUser.addStoreList(list)
            dataManager.updateStoreListForUser(currentUser.getUid(), currentUser.getUserStoreLists())

            profileView.addUserStoreList(list)
            profileView.setStoreListCount(String.format("(%d)", currentUser.getUserStoreLists().size))
            profileView.dismissCreateNewListDialog()
        } else {
            profileView.warningListNameExisted()
        }
    }

    override fun onSavedListClick() {
        if (UserStoreList.ID_SAVED < defaultList.size) {
            onStoreListClick(defaultList[UserStoreList.ID_SAVED])
        }
    }

    override fun onFavouriteListClick() {
        if (UserStoreList.ID_FAVOURITE < defaultList.size) {
            onStoreListClick(defaultList[UserStoreList.ID_FAVOURITE])
        }
    }

    override fun onStoreListClick(listPacket: UserStoreList) {
        val user = dataManager.getCurrentUser()
        if (user != null)
            profileView.openListDetail(listPacket, user.photoUrl)
    }

    // TODO: Support dialog asking user want to delete or not
    override fun onStoreListLongClick(position: Int) {
        val currentUser = dataManager.getCurrentUser()

        if (currentUser == null)
            return

        currentUser.removeStoreList(position)
        dataManager.updateStoreListForUser(currentUser.getUid(), currentUser.getUserStoreLists())

        profileView.setStoreListCount(String.format("(%d)", currentUser.getUserStoreLists().size))
    }

    private fun loadCurrentUserData() {
        val uid = dataManager.getCurrentUserUid()
        if (!isValidUserUid(uid))
            return

        dataManager.getUser(uid)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe(object : SingleObserver<User> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(user: User) {
                    dataManager.updateCurrentUser(user)
                    if (!user.photoUrl.isBlank())
                        profileView.loadAvatarPhoto(user.photoUrl)
                    profileView.setName(user.name)
                    profileView.setEmail(user.email)
                    loadStoreLists(user)

                    profileView.setSavedStoreCount(user.getSavedStoreList().getStoreIdList().size)
                    profileView.setFavouriteStoreCount(user.getFavouriteStoreList().getStoreIdList().size)
                }

                override fun onError(e: Throwable) {
                    profileView.showGeneralErrorMessage()
                }
            })
    }

    private fun loadStoreLists(currentUser: User) {
        for (i in 0 until currentUser.getUserStoreLists().size) {
            if (i <= 1) {
                // Load default lists
                defaultList.add(currentUser.getUserStoreLists()[i])
            } else {
                break
            }
        }

        profileView.setUserStoreLists(currentUser.getUserStoreLists())
        profileView.setStoreListCount(String.format("(%d)", currentUser.getUserStoreLists().size))
    }

    private fun isListNameExisted(listName: String, user: User): Boolean {
        val currStoreLists = user.getUserStoreLists()
        for (storeList in currStoreLists) {
            if (listName == storeList.listName) {
                return true
            }
        }

        return false
    }
}