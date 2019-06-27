package com.iceteaviet.fastfoodfinder.ui.main

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.service.eventbus.SearchEventResult
import com.iceteaviet.fastfoodfinder.service.eventbus.core.IBus
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.Constant
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by tom on 2019-04-18.
 */
class MainPresenter : BasePresenter<MainContract.Presenter>, MainContract.Presenter {

    private val mainView: MainContract.View
    private val bus: IBus

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, bus: IBus, mainView: MainContract.View) : super(dataManager, schedulerProvider) {
        this.mainView = mainView
        this.bus = bus
    }

    override fun subscribe() {
        bus.register(this)

        // Initialize auth info
        val currUser = dataManager.getCurrentUser()
        if (!dataManager.isSignedIn() || currUser == null) {
            mainView.updateProfileHeader(true)
        } else {
            mainView.updateProfileHeader(false)
            mainView.setProfileHeaderNameText(currUser.name)
            mainView.setProfileHeaderEmailText(currUser.email)

            if (!currUser.photoUrl.isBlank()) {
                mainView.loadProfileHeaderAvatar(currUser.photoUrl)
            }
        }
    }

    override fun unsubscribe() {
        bus.unregister(this)
        super.unsubscribe()
    }

    override fun onProfileMenuItemClick() {
        if (dataManager.isSignedIn())
            mainView.showProfileView()
        else
            mainView.showLoginView()
    }

    override fun onARLiveSightMenuItemClick() {
        mainView.showARLiveSightView()
    }

    override fun onSettingsMenuItemClick() {
        mainView.showSettingsView()
    }

    override fun onSignInMenuItemClick() {
        mainView.showLoginView()
    }

    override fun onSearchMenuItemExpand() {
        mainView.showSearchView()
    }

    override fun onSearchMenuItemCollapse() {
        bus.post(SearchEventResult(SearchEventResult.SEARCH_ACTION_COLLAPSE))
    }

    override fun onSearchQuerySubmit(query: String) {
        bus.post(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT, query))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchResult(searchEventResult: SearchEventResult) {
        mainView.hideSearchView()
        mainView.clearFocus()

        when (searchEventResult.resultCode) {
            SearchEventResult.SEARCH_ACTION_QUICK -> {
                handleSearchQuickAction(searchEventResult.searchString)
            }
            SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT -> {
                if (!searchEventResult.searchString.isBlank()) {
                    handleSearchQuerySubmitAction(searchEventResult.searchString)
                }
            }

            SearchEventResult.SEARCH_ACTION_COLLAPSE -> {
                handleSearchCollapseAction()
            }

            SearchEventResult.SEARCH_ACTION_STORE_CLICK -> {
                val store = searchEventResult.store
                if (store != null) {
                    handleSearchStoreClickAction(store)
                }
            }

            else -> mainView.showSearchWarningMessage()
        }
    }

    private fun handleSearchQuickAction(searchString: String) {
        mainView.setSearchQueryText(searchString)
    }

    private fun handleSearchQuerySubmitAction(searchString: String) {
        dataManager.addSearchHistories(searchString)
        mainView.setSearchQueryText(searchString)
    }

    private fun handleSearchCollapseAction() {
        mainView.hideKeyboard()
    }

    private fun handleSearchStoreClickAction(store: Store) {
        mainView.setSearchQueryText(store.title)

        dataManager.addSearchHistories(Constant.SEARCH_STORE_PREFIX + store.id)
    }
}