package com.iceteaviet.fastfoodfinder.ui.setting

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.ui.settings.SettingContract
import com.iceteaviet.fastfoodfinder.ui.settings.SettingPresenter
import com.iceteaviet.fastfoodfinder.utils.filterInvalidData
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreList
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SettingPresenterTest {
    @Mock
    private lateinit var settingView: SettingContract.View

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var settingPresenter: SettingPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()

        settingPresenter = SettingPresenter(dataManager, schedulerProvider, settingView)
    }

    @Test
    fun onInitSignOutTextViewTest() {
        `when`(dataManager.isSignedIn()).thenReturn(true)
        settingPresenter.onInitSignOutTextView()
        verify(settingView).initSignOutTextView(true)
    }

    @Test
    fun signOutTest() {
        settingPresenter.signOut()
        verify(dataManager).signOut()
    }

    @Test
    fun onLanguageChanged_IsVietnamese() {
        settingPresenter.isVietnamese = true

        settingPresenter.onLanguageChanged()

        verify(settingView).updateLangUI(true)
        assertThat(settingPresenter.isVietnamese).isEqualTo(false)
        verify(settingView).loadLanguage("vi")
    }

    @Test
    fun onLanguageChanged_IsNotVietnamese() {
        settingPresenter.isVietnamese = false

        settingPresenter.onLanguageChanged()

        verify(settingView).updateLangUI(false)
        assertThat(settingPresenter.isVietnamese).isEqualTo(true)
        verify(settingView).loadLanguage("en")
    }

    @Test
    fun onSetupLanguageTest() {
        `when`(dataManager.getIfLanguageIsVietnamese()).thenReturn(true)

        settingPresenter.onSetupLanguage()

        assertThat(settingPresenter.isVietnamese).isEqualTo(true)
        verify(settingView).updateLangUI(true)
    }

    @Test
    fun saveLanguagePrefTest() {
        settingPresenter.saveLanguagePref()
        verify(dataManager).setIfLanguageIsVietnamese(true)
    }

    @Test
    fun onLoadStoreFromServer_success() {
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.just(stores))
        settingPresenter.onLoadStoreFromServer()
        verify(dataManager).setStores(filterInvalidData(stores.toMutableList()))
        verify(settingView).showSuccessLoadingToast("")
        verify(settingView).updateLoadingProgressView(false)
    }

    @Test
    fun onLoadStoreFromServer_Failed() {
        var exception = Throwable("Failed message")
        `when`(dataManager.loadStoresFromServer()).thenReturn(Single.error(exception))
        settingPresenter.onLoadStoreFromServer()
        verify(settingView).showFailedLoadingToast(exception.message)
        verify(settingView).updateLoadingProgressView(false)
    }

    companion object {
        private val stores = getFakeStoreList()

    }
}