package com.iceteaviet.fastfoodfinder.ui.storelist

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreList
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-06-17.
 */
class StoreListPresenterTest {
    @Mock
    private lateinit var storeListView: StoreListContract.View

    private lateinit var storeListPresenter: StoreListPresenter

    @Mock
    private lateinit var dataManager: DataManager

    @Before
    fun setupPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        storeListPresenter = StoreListPresenter(dataManager, TrampolineSchedulerProvider(), storeListView)
    }

    @Test
    fun subscribeTest() {
        storeListPresenter.subscribe()

        verify(storeListView).setStores(stores)
    }

    companion object {
        private val stores = getFakeStoreList()
    }
}