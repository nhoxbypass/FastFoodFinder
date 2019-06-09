package com.iceteaviet.fastfoodfinder.ui.settings

import androidx.annotation.VisibleForTesting
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.filterInvalidData
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

/**
 * Created by tom on 2019-04-18.
 */
class SettingPresenter : BasePresenter<SettingContract.Presenter>, SettingContract.Presenter {

    val settingView: SettingContract.View
    @VisibleForTesting
    var isVietnamese = true


    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, settingView: SettingContract.View) : super(dataManager, schedulerProvider) {
        this.settingView = settingView
    }

    override fun subscribe() {
    }

    override fun onInitSignOutTextView() {
        settingView.initSignOutTextView(dataManager.isSignedIn())
    }

    override fun signOut() {
        dataManager.signOut()
    }

    override fun onLanguageChanged() {
        if (isVietnamese) {
            settingView.updateLangUI(true)
            isVietnamese = false
            settingView.loadLanguage("vi")

        } else {
            settingView.loadLanguage("en")
            settingView.updateLangUI(false)
            isVietnamese = true
        }
    }

    override fun onSetupLanguage() {
        isVietnamese = dataManager.getIfLanguageIsVietnamese()
        this.settingView.updateLangUI(isVietnamese)
    }

    override fun saveLanguagePref() {
        dataManager.setIfLanguageIsVietnamese(isVietnamese)
    }

    override fun onLoadStoreFromServer() {
        dataManager.loadStoresFromServer()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                        settingView.updateLoadingProgressView(true)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        val filteredStoreList = filterInvalidData(storeList.toMutableList())
                        dataManager.setStores(filteredStoreList)

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