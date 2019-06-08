package com.iceteaviet.fastfoodfinder.ui.store

import com.google.android.gms.maps.model.LatLng
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.utils.StoreType
import com.iceteaviet.fastfoodfinder.utils.getFakeComment
import com.iceteaviet.fastfoodfinder.utils.getFakeComments
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.eq
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-05-29.
 */
class StoreDetailPresenterTest {

    @Mock
    private lateinit var storeDetailView: StoreDetailContract.View

    private lateinit var storeDetailPresenter: StoreDetailPresenter

    @Mock
    private lateinit var dataManager: DataManager

    @Mock
    private lateinit var googleLocationManager: GoogleLocationManager

    @Before
    fun setupPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        storeDetailPresenter = StoreDetailPresenter(dataManager, TrampolineSchedulerProvider(), googleLocationManager, storeDetailView)
    }

    @Test
    fun subscribeTest() {
        `when`(dataManager.getComments(eq(STORE_ID.toString()))).thenReturn(
                Single.just(comments)
        )

        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)
        storeDetailPresenter.handleExtras(store)

        storeDetailPresenter.subscribe()

        verify(storeDetailView).setToolbarTitle(store.title)

        verify(dataManager).getComments(store.id.toString())

        verify(storeDetailView).setStoreComments(comments.toMutableList().asReversed())
    }

    @Test
    fun onLocationPermissionGrantedTest() {
        val spyPresenter = spy(storeDetailPresenter)

        spyPresenter.onLocationPermissionGranted()

        verify(spyPresenter).requestCurrentLocation()
        verify(spyPresenter).requestLocationUpdates()
    }

    @Test
    fun handleExtrasTest() {
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)
        storeDetailPresenter.handleExtras(store)

        assertThat(storeDetailPresenter.currStore).isEqualTo(store)
    }

    @Test
    fun handleExtrasTestWithNull() {
        storeDetailPresenter.handleExtras(null)

        verify(storeDetailView).exit()
    }

    @Test
    fun requestLocationUpdatesTest() {
        storeDetailPresenter.requestLocationUpdates()

        verify(googleLocationManager).subscribeLocationUpdate(storeDetailPresenter)
    }

    @Test
    fun requestCurrentLocationTest() {
        storeDetailPresenter.requestCurrentLocation()

        verify(googleLocationManager).getCurrentLocation()
    }

    @Test
    fun onAddNewCommentTest() {
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)
        storeDetailPresenter.handleExtras(store)

        storeDetailPresenter.onAddNewComment(comment)

        verify(storeDetailView).addStoreComment(comment)
        verify(storeDetailView).setAppBarExpanded(false)
        verify(storeDetailView).scrollToCommentList()

        verify(dataManager).insertOrUpdateComment(store.id.toString(), comment)
    }

    @Test
    fun clickOnCallButton_showCallScreen() {
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)
        storeDetailPresenter.handleExtras(store)

        storeDetailPresenter.onCallButtonClick()

        verify(storeDetailView).startCallIntent(store.tel)
    }

    @Test
    fun clickOnCallButton_showInvalidNumber() {
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_INVALID_TEL, STORE_TYPE)
        storeDetailPresenter.handleExtras(store)

        storeDetailPresenter.onCallButtonClick()

        verify(storeDetailView).showInvalidPhoneNumbWarning()
    }

    @Test
    fun clickOnNavigationButton_showNavigationScreen() {
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)

        `when`(dataManager.getMapsDirection(ArgumentMatchers.anyMap(), eq(store))).thenReturn(
                Single.just(mapsDirection)
        )

        storeDetailPresenter.handleExtras(store)
        storeDetailPresenter.currLocation = currLocation

        storeDetailPresenter.onNavigationButtonClick()

        verify(storeDetailView).showMapRoutingView(store, mapsDirection)
    }

    @Test
    fun clickOnNavigationButton_doNothing() {
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_INVALID_LAT, STORE_INVALID_LNG, STORE_TEL, STORE_TYPE)
        storeDetailPresenter.handleExtras(store)

        storeDetailPresenter.onNavigationButtonClick()

        verifyZeroInteractions(storeDetailView)
    }

    @Test
    fun clickOnCommentButton_showCommentEditorScreen() {
        storeDetailPresenter.onCommentButtonClick()

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

        private const val STORE_INVALID_TEL = ""
        private const val STORE_INVALID_ADDRESS = ""
        private const val STORE_INVALID_LAT = "0"
        private const val STORE_INVALID_LNG = "0"

        private val comments = getFakeComments()
        private val comment = getFakeComment()

        private val currLocation = LatLng(10.1234, 106.1234)

        private val mapsDirection = MapsDirection()
    }
}