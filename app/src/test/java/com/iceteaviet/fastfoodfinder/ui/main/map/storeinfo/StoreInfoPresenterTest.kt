package com.iceteaviet.fastfoodfinder.ui.main.map.storeinfo

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.StoreType
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
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