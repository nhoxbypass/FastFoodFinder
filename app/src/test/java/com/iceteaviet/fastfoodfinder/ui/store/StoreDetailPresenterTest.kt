package com.iceteaviet.fastfoodfinder.ui.store

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.StoreType
import com.iceteaviet.fastfoodfinder.utils.getFakeComments
import io.reactivex.SingleObserver
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
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
    private lateinit var dataManager: DataManager

    /**
     * [ArgumentCaptor] is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private lateinit var getStoreCallbackCaptor: ArgumentCaptor<SingleObserver<List<Comment>>>

    private lateinit var storeDetailPresenter: StoreDetailPresenter

    @Before
    fun setupNotesPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        storeDetailPresenter = StoreDetailPresenter(dataManager, storeDetailView)
    }

    @Test
    fun subscribeTest() {
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)

        storeDetailPresenter.handleExtras(store)
        storeDetailPresenter.subscribe()

        verify(storeDetailView).setToolbarTitle(store.title)

        // Then store is loaded from model, callback is captured and progress indicator is shown
        verify(dataManager).getComments(store.id.toString())
                .subscribe(getStoreCallbackCaptor.capture())
        //verify(storeDetailView).setProgressIndicator(true)

        // When store is finally loaded
        getStoreCallbackCaptor.getValue().onSuccess(comments) // Trigger callback

        // Then progress indicator is hidden and title and description are shown in UI
        //verify(storeDetailView).setProgressIndicator(false)
        verify(storeDetailView).setStoreComments(comments.toMutableList().asReversed())
    }

    @Test
    fun clickOnCallButton_showCallScreen() {
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)

        storeDetailPresenter.onCallButtonClick(store.tel)

        // Then add note UI is shown
        verify(storeDetailView).startCallIntent(store.tel)
    }

    @Test
    fun clickOnNavigationButton_showNavigationScreen() {
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)

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
        private const val STORE_ID = 123
        private const val STORE_TITLE = "store_title"
        private const val STORE_ADDRESS = "store_address"
        private const val STORE_LAT = "10.773996"
        private const val STORE_LNG = "106.6898035"
        private const val STORE_TEL = "012345678965"
        private const val STORE_TYPE = StoreType.TYPE_CIRCLE_K

        private val comments = getFakeComments()
    }
}