package com.iceteaviet.fastfoodfinder.ui.store

import android.os.Build
import com.google.android.gms.maps.model.LatLng
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.location.LocationListener
import com.iceteaviet.fastfoodfinder.utils.StoreType
import com.iceteaviet.fastfoodfinder.utils.exception.EmptyDataException
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import com.iceteaviet.fastfoodfinder.utils.getFakeComment
import com.iceteaviet.fastfoodfinder.utils.getFakeComments
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.setFinalStatic
import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.eq
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.*


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
    private lateinit var locationManager: GoogleLocationManager

    @Captor
    private lateinit var locationCallbackCaptor: ArgumentCaptor<LocationListener>

    @Before
    fun setupPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        storeDetailPresenter = StoreDetailPresenter(dataManager, TrampolineSchedulerProvider(), locationManager, storeDetailView)
    }

    @After
    fun tearDown() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 0)
    }

    @Test
    fun subscribeTest_locationPermissionGranted() {
        // Preconditions
        `when`(storeDetailView.isLocationPermissionGranted()).thenReturn(true)

        // Mocks
        `when`(dataManager.getComments(eq(STORE_ID.toString()))).thenReturn(
                Single.just(comments)
        )

        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)
        storeDetailPresenter.handleExtras(store)

        storeDetailPresenter.subscribe()

        verify(storeDetailView).setToolbarTitle(STORE_TITLE)
        verify(storeDetailView).setStoreComments(comments.toMutableList().asReversed())
        verify(storeDetailView, never()).requestLocationPermission()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(storeDetailPresenter)
    }

    @Test
    fun subscribeTest_devicePreLolipop() {
        // Preconditions
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 18)

        // Mocks
        `when`(dataManager.getComments(eq(STORE_ID.toString()))).thenReturn(
                Single.just(comments)
        )

        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)
        storeDetailPresenter.handleExtras(store)

        storeDetailPresenter.subscribe()

        verify(storeDetailView).setToolbarTitle(STORE_TITLE)
        verify(storeDetailView).setStoreComments(comments.toMutableList().asReversed())
        verify(storeDetailView, never()).requestLocationPermission()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(storeDetailPresenter)
    }

    @Test
    fun subscribeTest_locationPermissionNotGranted() {
        // Preconditions
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 24)
        `when`(storeDetailView.isLocationPermissionGranted()).thenReturn(false)

        // Mocks
        `when`(dataManager.getComments(eq(STORE_ID.toString()))).thenReturn(
                Single.just(comments)
        )

        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)
        storeDetailPresenter.handleExtras(store)

        storeDetailPresenter.subscribe()

        verify(storeDetailView).setToolbarTitle(STORE_TITLE)
        verify(storeDetailView).setStoreComments(comments.toMutableList().asReversed())
        verify(storeDetailView).requestLocationPermission()
        verify(locationManager, never()).requestLocationUpdates()
        verify(locationManager, never()).subscribeLocationUpdate(storeDetailPresenter)
    }

    @Test
    fun subscribeTest_getCommentsError() {
        // Mocks
        `when`(dataManager.getComments(eq(STORE_ID.toString()))).thenReturn(Single.error(EmptyDataException()))

        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)
        storeDetailPresenter.handleExtras(store)

        storeDetailPresenter.subscribe()

        verify(storeDetailView).setToolbarTitle(STORE_TITLE)
        verify(storeDetailView, never()).setStoreComments(ArgumentMatchers.anyList())
    }

    @Test
    fun unsubscribeTest() {
        storeDetailPresenter.unsubscribe()

        verify(locationManager).unsubscribeLocationUpdate(storeDetailPresenter)
    }

    @Test
    fun onLocationPermissionGrantedTest() {
        storeDetailPresenter.onLocationPermissionGranted()

        verify(locationManager).getCurrentLocation()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(storeDetailPresenter)
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
    fun requestCurrentLocationTest_haveLastLocation() {
        // Preconditions
        `when`(locationManager.getCurrentLocation()).thenReturn(location)

        storeDetailPresenter.requestCurrentLocation()

        assertThat(storeDetailPresenter.currLocation).isNotNull()
        assertThat(storeDetailPresenter.currLocation).isEqualTo(LatLng(location.latitude, location.longitude))
    }

    @Test
    fun requestCurrentLocationTest_notHaveLastLocation() {
        storeDetailPresenter.requestCurrentLocation()

        verify(storeDetailView).showCannotGetLocationMessage()
    }

    @Test
    fun onLocationChangeTest() {
        // Preconditions
        storeDetailPresenter.onLocationPermissionGranted()

        verify(locationManager).getCurrentLocation()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(capture(locationCallbackCaptor))

        locationCallbackCaptor.value.onLocationChanged(location)

        assertThat(storeDetailPresenter.currLocation).isNotNull()
        assertThat(storeDetailPresenter.currLocation).isEqualTo(LatLng(location.latitude, location.longitude))
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
        // Preconditions
        storeDetailPresenter.currLocation = currLocation
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)
        storeDetailPresenter.handleExtras(store)

        // Mocks
        `when`(dataManager.getMapsDirection(ArgumentMatchers.anyMap(), eq(store))).thenReturn(Single.just(mapsDirection))

        storeDetailPresenter.onNavigationButtonClick()

        verify(storeDetailView).showMapRoutingView(store, mapsDirection)
    }

    @Test
    fun clickOnNavigationButton_validData_generalError() {
        // Preconditions
        storeDetailPresenter.currLocation = currLocation
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)
        storeDetailPresenter.handleExtras(store)

        // Mocks
        `when`(dataManager.getMapsDirection(ArgumentMatchers.anyMap(), eq(store))).thenReturn(Single.error(NotFoundException()))

        storeDetailPresenter.onNavigationButtonClick()

        verify(storeDetailView).showGeneralErrorMessage()
    }

    @Test
    fun clickOnNavigationButton_invalidStoreLocation() {
        // Preconditions
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_INVALID_LAT, STORE_INVALID_LNG, STORE_TEL, STORE_TYPE)
        storeDetailPresenter.handleExtras(store)

        storeDetailPresenter.onNavigationButtonClick()

        verify(storeDetailView).showInvalidStoreLocationWarning()
    }

    @Test
    fun clickOnNavigationButton_nullCurrentLocation() {
        // Preconditions
        storeDetailPresenter.currLocation = null

        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)
        storeDetailPresenter.handleExtras(store)

        storeDetailPresenter.onNavigationButtonClick()

        verify(storeDetailView).showCannotGetLocationMessage()
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

        private val location = LatLngAlt(10.1234, 106.1234, 1.0)

        private val mapsDirection = MapsDirection()
    }
}