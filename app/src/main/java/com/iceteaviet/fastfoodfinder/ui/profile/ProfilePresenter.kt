package com.iceteaviet.fastfoodfinder.ui.profile

import androidx.annotation.VisibleForTesting
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
class ProfilePresenter(
    private val clientAuth: com.iceteaviet.fastfoodfinder.data.auth.ClientAuth,
    private val userRepository: com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository,
    schedulerProvider: SchedulerProvider,
        private val profileView: ProfileContract.View
) : BasePresenter<ProfileContract.Presenter>(schedulerProvider), ProfileContract.Presenter {

    
    @VisibleForTesting
    var defaultList: MutableList<UserStoreList> = ArrayList() // Default store list (saved, favourite) that every user have

    

    override fun subscribe() {
        // Invalid auth token -> go to login screen
        if (!clientAuth.isSignedIn())
            profileView.openLoginActivity()
        else
            loadCurrentUserData()
    }

    override fun onCreateNewListButtonClick() {
        profileView.showCreateNewListDialog()
    }

    // TODO: Check valid list name using FormatUtils
    override fun onCreateNewList(listName: String, iconId: Int) {
        val currentUser = com.iceteaviet.fastfoodfinder.utils.getCurrentUserHelper(clientAuth, userRepository) ?: return

        if (!isListNameExisted(listName, currentUser)) {
            val id = currentUser.getUserStoreLists().size // New id = current size
            val list = UserStoreList(id, ArrayList(), iconId, listName)
            currentUser.addStoreList(list)
            userRepository.updateStoreListForUser(currentUser.getUid(), currentUser.getUserStoreLists())

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
        val user = com.iceteaviet.fastfoodfinder.utils.getCurrentUserHelper(clientAuth, userRepository)
        if (user != null)
            profileView.openListDetail(listPacket, user.photoUrl)
    }

    // TODO: Support dialog asking user want to delete or not
    override fun onStoreListLongClick(position: Int) {
        val currentUser = com.iceteaviet.fastfoodfinder.utils.getCurrentUserHelper(clientAuth, userRepository)

        if (currentUser == null)
            return

        currentUser.removeStoreList(position)
        userRepository.updateStoreListForUser(currentUser.getUid(), currentUser.getUserStoreLists())

        profileView.setStoreListCount(String.format("(%d)", currentUser.getUserStoreLists().size))
    }

    private fun loadCurrentUserData() {
        val uid = clientAuth.getCurrentUserUid()
        if (!isValidUserUid(uid))
            return

        userRepository.getUser(uid)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe(object : SingleObserver<User> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onSuccess(user: User) {
                    userRepository.insertOrUpdateUser(user)
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