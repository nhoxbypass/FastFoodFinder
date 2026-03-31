package com.iceteaviet.fastfoodfinder.ui.storelist

import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreList
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider

/**
 * Created by tom on 2019-04-18.
 */
class StoreListPresenter(
    private val clientAuth: com.iceteaviet.fastfoodfinder.data.auth.ClientAuth,
    private val userRepository: com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository,
    private val storeRepository: com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository,
    schedulerProvider: SchedulerProvider,
        private val storeListView: StoreListContract.View
) : BasePresenter<StoreListContract.Presenter>(schedulerProvider), StoreListContract.Presenter {

    
    

    override fun subscribe() {
        storeListView.setStores(getFakeStoreList())
    }
}