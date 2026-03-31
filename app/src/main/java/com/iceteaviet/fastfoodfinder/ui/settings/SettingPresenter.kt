package com.iceteaviet.fastfoodfinder.ui.settings

import androidx.annotation.VisibleForTesting

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.filterInvalidData
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

/**
 * Created by tom on 2019-04-18.
 */
class SettingPresenter(
    private val clientAuth: com.iceteaviet.fastfoodfinder.data.auth.ClientAuth,
    private val preferencesRepository: com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository,
    private val storeRepository: com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository,
    schedulerProvider: SchedulerProvider,
    val settingView: SettingContract.View
) : BasePresenter<SettingContract.Presenter>(schedulerProvider), SettingContract.Presenter {

    @VisibleForTesting
    var isVietnamese = true

    override fun subscribe() {
    }

    override fun onInitSignOutTextView() {
        settingView.initSignOutTextView(clientAuth.isSignedIn())
    }

    override fun signOut() {
        clientAuth.signOut()
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
        isVietnamese = preferencesRepository.getIfLanguageIsVietnamese()
        this.settingView.updateLangUI(isVietnamese)
    }

    override fun saveLanguagePref() {
        preferencesRepository.setIfLanguageIsVietnamese(isVietnamese)
    }

    override fun onLoadStoreFromServer() {
        com.iceteaviet.fastfoodfinder.utils.loadStoresFromServerHelper(com.iceteaviet.fastfoodfinder.App.getContext(), clientAuth, storeRepository)
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe(object : SingleObserver<List<Store>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                    settingView.updateLoadingProgressView(true)
                }

                override fun onSuccess(storeList: List<Store>) {
                    val filteredStoreList = filterInvalidData(storeList.toMutableList())
                    storeRepository.setStores(filteredStoreList)

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