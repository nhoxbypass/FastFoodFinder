package com.iceteaviet.fastfoodfinder.ui.settings

import android.content.SharedPreferences
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface SettingContract {
    interface View : BaseView<Presenter> {
        fun initSignOutTextView(enabled : Boolean)
        fun updateLoadingProgressView(showProgress: Boolean)
        fun showSuccessLoadingToast(successMessage : String?)
        fun showFailedLoadingToast(failedMessage: String?)
        fun updateLangUI(isVietnamese: Boolean)
        fun loadLanguage(languageToLoad: String)
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onInitSignOutTextView()
        fun signOut()
        fun onLoadStoreFromServer()
        fun onSetupLanguage()
        fun saveLanguagePref()
        fun onLanguageTextViewClick()
        fun onLanguageSwitchClick()
    }
}