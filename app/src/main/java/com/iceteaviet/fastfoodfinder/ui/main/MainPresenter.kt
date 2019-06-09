package com.iceteaviet.fastfoodfinder.ui.main

import android.text.TextUtils
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.transport.model.SearchEventResult
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.Constant
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by tom on 2019-04-18.
 */
class MainPresenter : BasePresenter<MainContract.Presenter>, MainContract.Presenter {

    private val mainView: MainContract.View

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, mainView: MainContract.View) : super(dataManager, schedulerProvider) {
        this.mainView = mainView
    }

    override fun subscribe() {
        EventBus.getDefault().register(this)

        // Initialize auth info
        val currUser = dataManager.getCurrentUser()
        if (!dataManager.isSignedIn() || currUser == null) {
            mainView.updateProfileHeader(true)
        } else {
            mainView.updateProfileHeader(false)
            if (!TextUtils.isEmpty(currUser.photoUrl)) {
                mainView.loadProfileHeaderAvatar(currUser.photoUrl)
            }

            mainView.setProfileHeaderNameText(currUser.name)
            mainView.setProfileHeaderEmailText(currUser.email)
        }
    }

    override fun unsubscribe() {
        EventBus.getDefault().unregister(this)
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
        EventBus.getDefault().post(SearchEventResult(SearchEventResult.SEARCH_ACTION_COLLAPSE))
    }

    override fun onSearchQuerySubmit(query: String) {
        EventBus.getDefault().post(SearchEventResult(SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT, query))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchResult(searchEventResult: SearchEventResult) {
        when (searchEventResult.resultCode) {
            SearchEventResult.SEARCH_ACTION_QUICK -> {
                handleSearchQuickAction(searchEventResult.searchString)
            }
            SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT -> {
                mainView.hideSearchView()
                if (!searchEventResult.searchString.isBlank()) {
                    handleSearchQuerySubmitAction(searchEventResult.searchString)
                }
            }

            SearchEventResult.SEARCH_ACTION_COLLAPSE -> {
                handleSearchCollapseAction()
            }

            SearchEventResult.SEARCH_ACTION_STORE_CLICK -> {
                mainView.hideSearchView()
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
        mainView.hideSearchView()
        mainView.clearFocus()
    }

    private fun handleSearchQuerySubmitAction(searchString: String) {
        dataManager.addSearchHistories(searchString)
        mainView.setSearchQueryText(searchString)
        mainView.clearFocus()
    }

    private fun handleSearchCollapseAction() {
        mainView.hideSearchView()
        mainView.hideKeyboard()
        mainView.clearFocus()
    }

    private fun handleSearchStoreClickAction(store: Store) {
        mainView.setSearchQueryText(store.title)
        mainView.clearFocus()

        dataManager.addSearchHistories(Constant.SEARCH_STORE_PREFIX + store.id)
    }
}