package com.iceteaviet.fastfoodfinder.ui.main.map.storeinfo

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.StoreType
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-06-15.
 */
class StoreInfoPresenterTest {
    @Mock
    private lateinit var storeInfoView: StoreInfoContract.View

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var storeInfoPresenter: StoreInfoPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()

        storeInfoPresenter = StoreInfoPresenter(dataManager, schedulerProvider, storeInfoView)
    }

    @Test
    fun handleExtrasTest_invalidData() {
        storeInfoPresenter.handleExtras(null)

        verify(storeInfoView).exit()
    }

    @Test
    fun handleExtrasTest_validData() {
        storeInfoPresenter.handleExtras(store)

        verify(storeInfoView).updateNewStoreUI(store)
    }

    @Test
    fun subscribeTest() {
        storeInfoPresenter.subscribe()
    }

    @Test
    fun setOnDetailTextViewClickTest() {
        storeInfoPresenter.setOnDetailTextViewClick()
        verify(storeInfoView).openStoreDetailActivity(storeInfoPresenter.store)
    }

    @Test
    fun onMakeCallWithPermissionTest_Empty_Tel() {
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, "", StoreType.TYPE_CIRCLE_K)
        storeInfoPresenter.store = store
        storeInfoPresenter.onMakeCallWithPermission()
        verify(storeInfoView).showEmptyTelToast()
    }

    @Test
    fun onMakeCallWithPermissionTest_Not_Empty_Tel() {
        storeInfoPresenter.store = store
        storeInfoPresenter.onMakeCallWithPermission()
        verify(storeInfoView).makeNativeCall(STORE_TEL)
    }

    @Test
    fun onAddToFavoriteButtonClickTest_Invalid_Store() {
        storeInfoPresenter.store = null
        storeInfoPresenter.onAddToFavoriteButtonClick()
        verify(storeInfoView, Mockito.never()).addStoreToFavorite(any())
    }

    @Test
    fun onAddToFavoriteButtonClickTest_Valid_Store() {
        storeInfoPresenter.store = store
        storeInfoPresenter.onAddToFavoriteButtonClick()
        verify(storeInfoView).addStoreToFavorite(store)
    }

    @Test
    fun onDirectionButtonClick_Invalid_Store() {
        // Preconditions
        storeInfoPresenter.store = null

        storeInfoPresenter.onDirectionButtonClick()

        verify(storeInfoView, Mockito.never()).onDirectionChange(any())
    }

    @Test
    fun onDirectionButtonClick_Valid_Store() {
        storeInfoPresenter.store = store

        storeInfoPresenter.onDirectionButtonClick()

        verify(storeInfoView).onDirectionChange(store)
    }

    companion object {
        private const val STORE_ID = 123
        private const val STORE_TITLE = "store_title"
        private const val STORE_ADDRESS = "store_address"
        private const val STORE_LAT = "10.773996"
        private const val STORE_LNG = "106.6898035"
        private const val STORE_TEL = "012345678965"

        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, StoreType.TYPE_CIRCLE_K)
    }
}