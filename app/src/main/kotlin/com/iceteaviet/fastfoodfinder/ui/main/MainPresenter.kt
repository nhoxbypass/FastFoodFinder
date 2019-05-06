package com.iceteaviet.fastfoodfinder.ui.main

import android.text.TextUtils
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.transport.model.SearchEventResult
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.Constant
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by tom on 2019-04-18.
 */
class MainPresenter : BasePresenter<MainContract.Presenter>, MainContract.Presenter {

    private val mainView: MainContract.View

    constructor(dataManager: DataManager, mainView: MainContract.View) : super(dataManager) {
        this.mainView = mainView
        this.mainView.presenter = this
    }

    override fun subscribe() {
        EventBus.getDefault().register(this)

        // Initialize auth info
        if (!dataManager.isSignedIn() || dataManager.getCurrentUser() == null) {
            mainView.updateProfileHeader(true)
        } else {
            val user = dataManager.getCurrentUser()!!

            mainView.updateProfileHeader(false)
            if (!TextUtils.isEmpty(user.photoUrl)) {
                mainView.loadProfileHeaderAvatar(user.photoUrl)
            }

            mainView.setProfileHeaderNameText(user.name)
            mainView.setProfileHeaderEmailText(user.email)
        }
    }

    override fun unsubscribe() {
        super.unsubscribe()
        EventBus.getDefault().unregister(this)
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
                mainView.setSearchQueryText(searchEventResult.searchString)
                mainView.hideSearchView()
                mainView.clearFocus()
            }
            SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT -> {
                mainView.hideSearchView()
                if (!searchEventResult.searchString.isBlank()) {
                    dataManager.addSearchHistories(searchEventResult.searchString)
                    mainView.setSearchQueryText(searchEventResult.searchString)
                    mainView.clearFocus()
                }
            }

            SearchEventResult.SEARCH_ACTION_COLLAPSE -> {
                mainView.hideSearchView()
                mainView.hideKeyboard()
                mainView.clearFocus()
            }

            SearchEventResult.SEARCH_ACTION_STORE_CLICK -> {
                mainView.hideSearchView()
                if (searchEventResult.store != null) {
                    mainView.setSearchQueryText(searchEventResult.store!!.title!!)
                    mainView.clearFocus()

                    dataManager.addSearchHistories(Constant.SEARCH_STORE_PREFIX + searchEventResult.store!!.id)
                }
            }

            else -> mainView.showSearchWarningMessage()
        }
    }
}