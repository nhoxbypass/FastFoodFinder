package com.iceteaviet.fastfoodfinder.ui.settings

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import com.iceteaviet.fastfoodfinder.BuildConfig
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.filterInvalidData
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

/**
 * Created by tom on 2019-04-18.
 */
class SettingPresenter : BasePresenter<SettingContract.Presenter>, SettingContract.Presenter {

    val settingView: SettingContract.View
    lateinit var pref : SharedPreferences

    constructor(dataManager: DataManager, settingView: SettingContract.View) : super(dataManager) {
        this.settingView = settingView
        this.settingView.presenter = this
        pref =  this.settingView.getActivity().applicationContext.getSharedPreferences(
                BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
    }

    override fun subscribe() {
    }

    override fun onInitSignOutTextView() {
        settingView.initSignOutTextView(!dataManager.isSignedIn())
    }

    override fun signOut() {
        dataManager.signOut()
    }

    override fun saveLanguagePref(isVietnamese: Boolean) {
        val editor = pref.edit()
        editor.putBoolean(KEY_LANGUAGE, isVietnamese)
        editor.apply()
    }
    override fun onLoadStoreFromServer() {
        dataManager.loadStoresFromServer()
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        settingView.updateLoadingProgressView(true)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        dataManager.getLocalStoreDataSource().setStores(filterInvalidData(storeList.toMutableList()))
                        settingView.showSuccessLoadingToast("")
                        settingView.updateLoadingProgressView(false)
                    }

                    override fun onError(e: Throwable) {
                        settingView.showFailedLoadingToast(e.message)
                        settingView.updateLoadingProgressView(false)
                    }
                })
    }

    override fun onSetupLanguage() {
        this.settingView.onLanguageChanged(pref.getBoolean(KEY_LANGUAGE, false))
    }
    companion object {
        const val KEY_LANGUAGE = "lang"
    }

}