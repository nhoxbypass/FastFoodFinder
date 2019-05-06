package com.iceteaviet.fastfoodfinder.ui.main

import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface MainContract {
    interface View : BaseView<Presenter> {
        fun showProfileView()
        fun showLoginView()
        fun setSearchQueryText(searchString: String)
        fun hideKeyboard()
        fun clearFocus()
        fun showSearchWarningMessage()
        fun showSearchView()
        fun hideSearchView()
        fun updateProfileHeader(showSignIn: Boolean)
        fun loadProfileHeaderAvatar(photoUrl: String)
        fun setProfileHeaderNameText(name: String)
        fun setProfileHeaderEmailText(email: String)
        fun showARLiveSightView()
        fun showSettingsView()
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onProfileMenuItemClick()
        fun onSignInMenuItemClick()
        fun onSearchMenuItemExpand()
        fun onSearchMenuItemCollapse()
        fun onSearchQuerySubmit(query: String)
        fun onARLiveSightMenuItemClick()
        fun onSettingsMenuItemClick()
    }
}