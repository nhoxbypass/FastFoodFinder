package com.iceteaviet.fastfoodfinder.ui.storelist

import androidx.annotation.VisibleForTesting
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
class ListDetailPresenter(
    private val clientAuth: com.iceteaviet.fastfoodfinder.data.auth.ClientAuth,
    private val userRepository: com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository,
    private val storeRepository: com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository,
    schedulerProvider: SchedulerProvider,
        private val listDetailView: ListDetailContract.View
) : BasePresenter<ListDetailContract.Presenter>(schedulerProvider), ListDetailContract.Presenter {

    
    @VisibleForTesting
    lateinit var userStoreList: UserStoreList

    @VisibleForTesting
    var photoUrl: String = ""

    

    override fun subscribe() {
        listDetailView.setListNameText(userStoreList.listName)
        listDetailView.loadStoreIcon(getStoreListIconDrawableRes(userStoreList.iconId))

        //add list store to mAdapter here
        storeRepository.findStoresByIds(userStoreList.getStoreIdList())
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