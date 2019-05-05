package com.iceteaviet.fastfoodfinder.ui.settings

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.filterInvalidData
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by tom on 2019-04-18.
 */
class SettingPresenter : BasePresenter<SettingContract.Presenter>, SettingContract.Presenter {

    val settingView: SettingContract.View

    constructor(dataManager: DataManager, settingView: SettingContract.View) : super(dataManager) {
        this.settingView = settingView
        this.settingView.presenter = this
    }

    override fun subscribe() {
    }

    override fun onInitSignOutTextView() {
        settingView.initSignOutTextView(dataManager.isSignedIn())
    }

    override fun signOut() {
        dataManager.signOut()
    }

    override fun onLanguageTextViewClick() {
        settingView.onClickOnLanguageTextView()
    }

    override fun onLanguageSwitchClick() {
        settingView.onClickOnLanguageSwitch()
    }

    override fun onSetupLanguage() {
        this.settingView.initLanguageView(dataManager.getPreferencesHelper().getIfLanguageIsVietnamese())
    }

    override fun saveLanguagePref(isVietnamese: Boolean) {
        dataManager.getPreferencesHelper().setIfLanguageIsVietnamese(isVietnamese)
    }
    override fun onLoadStoreFromServer() {
        dataManager.loadStoresFromServer()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
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
}