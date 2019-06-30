package com.iceteaviet.fastfoodfinder.ui.storelist

import androidx.annotation.VisibleForTesting
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreListIconDrawableRes
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

/**
 * Created by tom on 2019-04-18.
 */
class ListDetailPresenter : BasePresenter<ListDetailContract.Presenter>, ListDetailContract.Presenter {

    private val listDetailView: ListDetailContract.View

    @VisibleForTesting
    lateinit var userStoreList: UserStoreList
    @VisibleForTesting
    var photoUrl: String? = null

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, listDetailView: ListDetailContract.View) : super(dataManager, schedulerProvider) {
        this.listDetailView = listDetailView
    }

    override fun subscribe() {
        listDetailView.setListNameText(userStoreList.listName)
        listDetailView.loadStoreIcon(getStoreListIconDrawableRes(userStoreList.iconId))

        //add list store to mAdapter here
        dataManager.findStoresByIds(userStoreList.getStoreIdList())
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        listDetailView.setStores(storeList)
                    }

                    override fun onError(e: Throwable) {
                        listDetailView.showGeneralErrorMessage()
                    }
                })
    }

    override fun handleExtras(userStoreList: UserStoreList?, photoUrl: String?) {
        if (photoUrl == null && userStoreList == null) {
            listDetailView.exit()
            return
        }

        this.userStoreList = userStoreList ?: UserStoreList()
        this.photoUrl = photoUrl ?: ""
    }
}