package com.iceteaviet.fastfoodfinder.ui.store

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.StoreType
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-05-29.
 */
class StoreDetailPresenterTest {

    @Mock
    private lateinit var storeDetailView: StoreDetailContract.View

    @Mock
    private val dataManager: DataManager? = null

    private lateinit var storeDetailPresenter: StoreDetailPresenter

    @Before
    fun setupNotesPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        storeDetailPresenter = StoreDetailPresenter(dataManager!!, storeDetailView)
    }

    @Test
    fun subscribeTest() {
        storeDetailPresenter.subscribe()
    }

    @Test
    fun clickOnCallButton_showCallScreen() {
        storeDetailPresenter.onCallButtonClick(store.tel)

        // Then add note UI is shown
        verify(storeDetailView).startCallIntent(store.tel)
    }

    @Test
    fun clickOnNavigationButton_showNavigationScreen() {
        storeDetailPresenter.onNavigationButtonClick()

        // Then add note UI is shown
        verify(storeDetailView).showMapRoutingView(store, MapsDirection())
    }

    @Test
    fun clickOnCommentButton_showCommentEditorScreen() {
        storeDetailPresenter.onCommentButtonClick()

        // Then add note UI is shown
        verify(storeDetailView).showCommentEditorView()
    }

    companion object {
        private val store: Store = Store(123, "store_title", "store_address",
                "123", "123", "012345678965", StoreType.TYPE_CIRCLE_K)
    }
}