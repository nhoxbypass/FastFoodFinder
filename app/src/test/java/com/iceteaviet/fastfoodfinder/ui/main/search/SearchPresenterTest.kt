package com.iceteaviet.fastfoodfinder.ui.main.search

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.main.search.model.SearchStoreItem
import com.iceteaviet.fastfoodfinder.utils.StoreType
import com.iceteaviet.fastfoodfinder.utils.exception.UnknownException
import com.iceteaviet.fastfoodfinder.utils.getFakeSearchStoreItems
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreList
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by tom on 2019-06-15.
 */
class SearchPresenterTest {
    @Mock
    private lateinit var searchView: SearchContract.View

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var searchPresenter: SearchPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()

        searchPresenter = SearchPresenter(dataManager, schedulerProvider, searchView)
    }

    @Test
    fun subscribeTest_emptySearchHistory() {
        // Preconditions
        `when`(dataManager.getSearchHistories()).thenReturn(TreeSet())

        searchPresenter.subscribe()

        verifyZeroInteractions(searchView)
    }

    @Test
    fun subscribeTest_haveSearchHistory() {
        // Preconditions
        `when`(dataManager.getSearchHistories()).thenReturn(searchHistory)

        // Mocks
        `when`(dataManager.findStoreById(STORE_ID)).thenReturn(Single.just(store))

        searchPresenter.subscribe()

        verify(searchView).setSearchHistory(searchHistory.toList().asReversed(), searchItems)
    }

    @Test
    fun onUpdateSearchListTest_error() {
        // Mocks
        `when`(dataManager.findStores(ArgumentMatchers.anyString())).thenReturn(Single.error(UnknownException()))

        searchPresenter.onUpdateSearchList("search text")

        verify(searchView).showGeneralErrorMessage()
    }

    @Test
    fun onUpdateSearchListTest_emptyData() {
        // Mocks
        `when`(dataManager.findStores(ArgumentMatchers.anyString())).thenReturn(Single.just(ArrayList()))

        searchPresenter.onUpdateSearchList("search text")

        verify(searchView).setSearchStores(ArrayList())
    }

    @Test
    fun onUpdateSearchListTest() {
        // Mocks
        `when`(dataManager.findStores(ArgumentMatchers.anyString())).thenReturn(Single.just(stores))

        searchPresenter.onUpdateSearchList("search text")

        verify(searchView).setSearchStores(searchStoreItems)
    }

    companion object {
        private const val STORE_ID = 123
        private const val STORE_TITLE = "store_title"
        private const val STORE_ADDRESS = "store_address"
        private const val STORE_LAT = "10.773996"
        private const val STORE_LNG = "106.6898035"
        private const val STORE_TEL = "012345678965"

        private val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, StoreType.TYPE_CIRCLE_K)

        private val searchHistory = linkedSetOf("search-store-id_123", "quan 7", "circle K")
        private val searchItems = arrayListOf(
                SearchStoreItem(null, "circle K"),
                SearchStoreItem(null, "quan 7"),
                SearchStoreItem(store, "")
        )

        private val stores = getFakeStoreList()

        private val searchStoreItems = getFakeSearchStoreItems()
    }
}