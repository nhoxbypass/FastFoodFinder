package com.iceteaviet.fastfoodfinder.ui.main.map

import android.os.Build
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.iceteaviet.fastfoodfinder.Injection
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.location.LocationListener
import com.iceteaviet.fastfoodfinder.service.eventbus.SearchEventResult
import com.iceteaviet.fastfoodfinder.service.eventbus.core.IBus
import com.iceteaviet.fastfoodfinder.ui.main.map.model.MapCameraPosition
import com.iceteaviet.fastfoodfinder.ui.main.map.model.NearByStore
import com.iceteaviet.fastfoodfinder.utils.*
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import com.iceteaviet.fastfoodfinder.utils.exception.UnknownException
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.eq
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.*

/**
 * Created by tom on 2019-06-15.
 */
class MainMapPresenterTest {
    @Mock
    private lateinit var mainMapView: MainMapContract.View

    @Mock
    private lateinit var dataManager: DataManager

    @Mock
    private lateinit var locationManager: GoogleLocationManager

    @Mock
    private lateinit var bus: IBus

    private lateinit var storePublisher: PublishSubject<Store>

    private lateinit var mapCamPublisher: PublishSubject<MapCameraPosition>

    @Captor
    private lateinit var locationCallbackCaptor: ArgumentCaptor<LocationListener>

    @Captor
    private lateinit var nearByStoreCallbackCaptor: ArgumentCaptor<List<NearByStore>>

    private lateinit var mainMapPresenter: MainMapPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()
        storePublisher = Injection.providePublishSubject()
        mapCamPublisher = Injection.providePublishSubject()

        mainMapPresenter = MainMapPresenter(dataManager, schedulerProvider, locationManager, bus, storePublisher, mapCamPublisher, mainMapView)
    }

    @After
    fun tearDown() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 0)
    }

    @Test
    fun subscribeTest_locationPermissionGranted() {
        // Preconditions
        Mockito.`when`(mainMapView.isLocationPermissionGranted()).thenReturn(true)

        // Mocks
        `when`(dataManager.getAllStores()).thenReturn(Single.never())

        mainMapPresenter.subscribe()

        verify(bus).register(mainMapPresenter)
        verify(mainMapView).setupMap()
        verify(mainMapView, never()).requestLocationPermission()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(mainMapPresenter)
    }

    @Test
    fun subscribeTest_devicePreLolipop() {
        // Preconditions
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 18)

        // Mocks
        `when`(dataManager.getAllStores()).thenReturn(Single.never())

        mainMapPresenter.subscribe()

        verify(bus).register(mainMapPresenter)
        verify(mainMapView).setupMap()
        verify(mainMapView, never()).requestLocationPermission()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(mainMapPresenter)
    }

    @Test
    fun subscribeTest_locationPermissionNotGranted() {
        // Preconditions
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 24)
        Mockito.`when`(mainMapView.isLocationPermissionGranted()).thenReturn(false)

        // Mocks
        `when`(dataManager.getAllStores()).thenReturn(Single.never())

        mainMapPresenter.subscribe()

        verify(bus).register(mainMapPresenter)
        verify(mainMapView).setupMap()
        verify(mainMapView).requestLocationPermission()
        verify(locationManager, never()).requestLocationUpdates()
        verify(locationManager, never()).subscribeLocationUpdate(mainMapPresenter)
    }

    @Test
    fun onGetMapAsyncTest_emptyStoreList() {
        // Preconditions
        mainMapPresenter.storeList = ArrayList()

        mainMapPresenter.onGetMapAsync()

        verify(mainMapView, never()).addMarkersToMap(ArgumentMatchers.anyList())
    }

    @Test
    fun onGetMapAsyncTest_haveStoreList() {
        // Preconditions
        mainMapPresenter.storeList = stores

        mainMapPresenter.onGetMapAsync()

        verify(mainMapView).addMarkersToMap(stores)
    }

    @Test
    fun onGetMapAsyncTest_locationPermissionNotGranted() {
        // Preconditions
        mainMapPresenter.locationGranted = false

        mainMapPresenter.onGetMapAsync()

        verify(mainMapView, never()).setMyLocationEnabled(true)
    }

    @Test
    fun onGetMapAsyncTest_locationPermissionGranted_notHaveLastLocation() {
        // Preconditions
        mainMapPresenter.locationGranted = true

        mainMapPresenter.onGetMapAsync()

        verify(mainMapView, atLeastOnce()).setMyLocationEnabled(true)
        verify(mainMapView).setupMapEventHandlers()
        verify(mainMapView, never()).animateMapCamera(anyObject(), ArgumentMatchers.anyBoolean())
    }

    @Test
    fun onGetMapAsyncTest_locationPermissionGranted_haveLastLocation() {
        // Preconditions
        `when`(locationManager.getCurrentLocation()).thenReturn(location)
        val spyMainMapPresenter = spy(mainMapPresenter)
        spyMainMapPresenter.locationGranted = true

        spyMainMapPresenter.onGetMapAsync()

        verify(mainMapView, atLeastOnce()).setMyLocationEnabled(true)
        verify(spyMainMapPresenter, atLeastOnce()).requestCurrentLocation()
        verify(mainMapView).setupMapEventHandlers()
        assertThat(spyMainMapPresenter.currLocation).isNotNull()
        assertThat(spyMainMapPresenter.currLocation).isEqualTo(LatLng(location.latitude, location.longitude))
        verify(spyMainMapPresenter).subscribeMapCameraPositionChange()
        verify(spyMainMapPresenter).subscribeNewVisibleStore()
    }

    @Test
    fun requestCurrentLocationTest_haveLastLocation_notZoomToUser() {
        // Preconditions
        `when`(locationManager.getCurrentLocation()).thenReturn(location)
        mainMapPresenter.isZoomToUser = false

        mainMapPresenter.requestCurrentLocation()

        assertThat(mainMapPresenter.currLocation).isNotNull()
        assertThat(mainMapPresenter.currLocation).isEqualTo(LatLng(location.latitude, location.longitude))
        verify(mainMapView).animateMapCamera(LatLng(location.latitude, location.longitude), false)
    }

    @Test
    fun requestCurrentLocationTest_haveLastLocation_zoomToUser() {
        // Preconditions
        `when`(locationManager.getCurrentLocation()).thenReturn(location)
        mainMapPresenter.isZoomToUser = true

        mainMapPresenter.requestCurrentLocation()

        assertThat(mainMapPresenter.currLocation).isNotNull()
        assertThat(mainMapPresenter.currLocation).isEqualTo(LatLng(location.latitude, location.longitude))
        verify(mainMapView, never()).animateMapCamera(anyObject(), ArgumentMatchers.anyBoolean())
    }

    @Test
    fun requestCurrentLocationTest_notHaveLastLocation() {
        mainMapPresenter.requestCurrentLocation()

        verify(mainMapView, never()).animateMapCamera(anyObject(), ArgumentMatchers.anyBoolean())
    }

    @Test
    fun onLocationPermissionGrantedTest() {
        // Preconditions
        mainMapPresenter.onLocationPermissionGranted()

        verify(locationManager).getCurrentLocation()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(capture(locationCallbackCaptor))
        verify(mainMapView).setMyLocationEnabled(true)

        locationCallbackCaptor.value.onLocationChanged(location)

        assertThat(mainMapPresenter.currLocation).isNotNull()
        assertThat(mainMapPresenter.currLocation).isEqualTo(LatLng(location.latitude, location.longitude))
    }

    @Test
    fun onLocationChangeTest_notZoomToUser() {
        // Preconditions
        mainMapPresenter.locationGranted = true
        mainMapPresenter.isZoomToUser = false

        mainMapPresenter.onLocationChanged(location)

        assertThat(mainMapPresenter.currLocation).isNotNull()
        assertThat(mainMapPresenter.currLocation).isEqualTo(LatLng(location.latitude, location.longitude))
        verify(mainMapView).animateMapCamera(LatLng(location.latitude, location.longitude), false)
    }

    @Test
    fun subscribeTest_getStoresLocalEmpty() {
        // Preconditions
        `when`(dataManager.getAllStores()).thenReturn(Single.just(ArrayList()))

        mainMapPresenter.subscribe()

        verify(mainMapView).setupMap()
        verify(mainMapView).showWarningMessage(ArgumentMatchers.anyInt())
        verify(mainMapView, never()).addMarkersToMap(ArgumentMatchers.anyList())
    }

    @Test
    fun subscribeTest_getStoresLocalError() {
        // Preconditions
        `when`(dataManager.getAllStores()).thenReturn(Single.error(UnknownException()))

        mainMapPresenter.subscribe()

        verify(mainMapView).setupMap()
        verify(mainMapView).showWarningMessage(ArgumentMatchers.anyInt())
        verify(mainMapView, never()).addMarkersToMap(ArgumentMatchers.anyList())
    }

    @Test
    fun subscribeTest_getStoresLocalSuccess() {
        // Preconditions
        `when`(dataManager.getAllStores()).thenReturn(Single.just(stores))

        mainMapPresenter.subscribe()

        verify(mainMapView).setupMap()
        verify(mainMapView, never()).showWarningMessage(ArgumentMatchers.anyInt())
        verify(mainMapView).addMarkersToMap(stores.toMutableList())
    }

    @Test
    fun unsubscribeTest() {
        mainMapPresenter.unsubscribe()

        verify(bus).unregister(mainMapPresenter)
        verify(locationManager).unsubscribeLocationUpdate(mainMapPresenter)
    }

    @Test
    fun onNavigationButtonClickTest_showNavigationScreen() {
        // Preconditions
        mainMapPresenter.currLocation = latLng

        // Mocks
        `when`(dataManager.getMapsDirection(ArgumentMatchers.anyMap(), eq(store))).thenReturn(Single.just(mapsDirection))

        mainMapPresenter.onNavigationButtonClick(store)

        verify(mainMapView).showMapRoutingView(store, mapsDirection)
    }

    @Test
    fun onNavigationButtonClickTest_invalidMapsDirection() {
        // Preconditions
        mainMapPresenter.currLocation = latLng

        // Mocks
        `when`(dataManager.getMapsDirection(ArgumentMatchers.anyMap(), eq(store))).thenReturn(Single.just(invalidMapsDirection))

        mainMapPresenter.onNavigationButtonClick(store)

        verify(mainMapView).showGeneralErrorMessage()
    }

    @Test
    fun onNavigationButtonClickTest_validStoreLocation_generalError() {
        // Preconditions
        mainMapPresenter.currLocation = latLng
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)

        // Mocks
        `when`(dataManager.getMapsDirection(ArgumentMatchers.anyMap(), eq(store))).thenReturn(Single.error(NotFoundException()))

        mainMapPresenter.onNavigationButtonClick(store)

        verify(mainMapView).showGeneralErrorMessage()
    }

    @Test
    fun onNavigationButtonClickTest_invalidStoreLocation() {
        // Preconditions
        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_INVALID_LAT, STORE_INVALID_LNG, STORE_TEL, STORE_TYPE)

        mainMapPresenter.onNavigationButtonClick(store)

        verify(mainMapView).showInvalidStoreLocationWarning()
    }

    @Test
    fun onNavigationButtonClickTest_nullCurrentLocation() {
        // Preconditions
        mainMapPresenter.currLocation = null

        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)

        mainMapPresenter.onNavigationButtonClick(store)

        verify(mainMapView).showCannotGetLocationMessage()
    }

    @Test
    fun onClearOldMapDataTest() {
        mainMapPresenter.onClearOldMapData()

        assertThat(mainMapPresenter.markerSparseArray.size()).isZero()
        verify(mainMapView).clearMapData()
    }

    @Test
    fun onSearchResultTest_actionQuick_findStoresError() {
        // Preconditions
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, StoreType.TYPE_CIRCLE_K)
        `when`(dataManager.findStoresByType(StoreType.TYPE_CIRCLE_K)).thenReturn(Single.error(NotFoundException()))

        mainMapPresenter.onSearchResult(searchEventResult)

        verify(mainMapView).showWarningMessage(ArgumentMatchers.anyInt())
    }

    @Test
    fun onSearchResultTest_actionQuick_findStoresEmpty() {
        // Preconditions
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, StoreType.TYPE_CIRCLE_K)
        `when`(dataManager.findStoresByType(StoreType.TYPE_CIRCLE_K)).thenReturn(Single.just(ArrayList()))

        mainMapPresenter.onSearchResult(searchEventResult)

        verify(mainMapView).showWarningMessage(ArgumentMatchers.anyInt())
        verify(mainMapView, never()).addMarkersToMap(ArgumentMatchers.anyList())
        verify(mainMapView, never()).animateMapCamera(com.nhaarman.mockitokotlin2.any(), eq(false))
    }

    @Test
    fun onSearchResultTest_actionQuick_findStoresSuccess() {
        // Preconditions
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_QUICK, StoreType.TYPE_CIRCLE_K)
        `when`(dataManager.findStoresByType(StoreType.TYPE_CIRCLE_K)).thenReturn(Single.just(circleKStores))

        mainMapPresenter.onSearchResult(searchEventResult)

        verify(mainMapView).addMarkersToMap(circleKStores)
        verify(mainMapView).animateMapCamera(circleKStores[0].getPosition(), false)
    }

    @Test
    fun onSearchResultTest_actionQuerySubmit_empty() {
        // Preconditions
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT, "")

        mainMapPresenter.onSearchResult(searchEventResult)

        verifyZeroInteractions(mainMapView)
    }

    @Test
    fun onSearchResultTest_actionQuerySubmit_findStoresError() {
        // Preconditions
        val queryStr = "circle K"
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT, queryStr)
        `when`(dataManager.findStores(queryStr)).thenReturn(Single.error(NotFoundException()))

        mainMapPresenter.onSearchResult(searchEventResult)

        verify(mainMapView).showWarningMessage(ArgumentMatchers.anyInt())
    }

    @Test
    fun onSearchResultTest_actionQuerySubmit_findStoresEmpty() {
        // Preconditions
        val queryStr = "circle K"
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT, queryStr)
        `when`(dataManager.findStores(queryStr)).thenReturn(Single.just(ArrayList()))

        mainMapPresenter.onSearchResult(searchEventResult)

        verify(mainMapView).showWarningMessage(ArgumentMatchers.anyInt())
        verify(mainMapView, never()).addMarkersToMap(ArgumentMatchers.anyList())
        verify(mainMapView, never()).animateMapCamera(com.nhaarman.mockitokotlin2.any(), eq(false))
    }

    @Test
    fun onSearchResultTest_actionQuerySubmit_findStoresSuccess() {
        // Preconditions
        val queryStr = "circle K"
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT, queryStr)
        `when`(dataManager.findStores(queryStr)).thenReturn(Single.just(circleKStores))

        mainMapPresenter.onSearchResult(searchEventResult)

        verify(mainMapView).addMarkersToMap(circleKStores)
        verify(mainMapView).animateMapCamera(circleKStores[0].getPosition(), false)
    }

    @Test
    fun onSearchResultTest_actionCollapse_notHaveCurrentLocation() {
        // Preconditions
        mainMapPresenter.currLocation = null
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_COLLAPSE)
        `when`(dataManager.getAllStores()).thenReturn(Single.never())

        mainMapPresenter.onSearchResult(searchEventResult)

        verify(mainMapView, never()).animateMapCamera(com.nhaarman.mockitokotlin2.any(), eq(false))
    }

    @Test
    fun onSearchResultTest_actionCollapse_getAllStoresError() {
        // Preconditions
        mainMapPresenter.currLocation = latLng
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_COLLAPSE)
        `when`(dataManager.getAllStores()).thenReturn(Single.error(UnknownException()))

        mainMapPresenter.onSearchResult(searchEventResult)

        verify(mainMapView).showWarningMessage(ArgumentMatchers.anyInt())
        verify(mainMapView).animateMapCamera(latLng, false)
    }

    @Test
    fun onSearchResultTest_actionCollapse_getAllStoresEmpty() {
        // Preconditions
        mainMapPresenter.currLocation = latLng
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_COLLAPSE)
        `when`(dataManager.getAllStores()).thenReturn(Single.just(ArrayList()))

        mainMapPresenter.onSearchResult(searchEventResult)

        verify(mainMapView).showWarningMessage(ArgumentMatchers.anyInt())
        verify(mainMapView).animateMapCamera(latLng, false)
    }

    @Test
    fun onSearchResultTest_actionCollapse_getAllStoresSuccess() {
        // Preconditions
        mainMapPresenter.currLocation = latLng
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_COLLAPSE)
        `when`(dataManager.getAllStores()).thenReturn(Single.just(stores))

        mainMapPresenter.onSearchResult(searchEventResult)

        verify(mainMapView).addMarkersToMap(stores)
        verify(mainMapView).animateMapCamera(latLng, false)
    }

    @Test
    fun onSearchResultTest_actionStoreClick_nullStore() {
        // Preconditions
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_STORE_CLICK, "")

        mainMapPresenter.onSearchResult(searchEventResult)

        verifyZeroInteractions(mainMapView)
    }

    @Test
    fun onSearchResultTest_actionStoreClick() {
        // Preconditions
        val searchEventResult = SearchEventResult(SearchEventResult.SEARCH_ACTION_STORE_CLICK, store.title, store)

        mainMapPresenter.onSearchResult(searchEventResult)

        verify(mainMapView).addMarkersToMap(arrayListOf(store))
        verify(mainMapView).animateMapCamera(store.getPosition(), false)
        verify(mainMapView).clearNearByStores()
        verify(mainMapView).showDialogStoreInfo(store)
    }

    @Test
    fun onSearchResultTest_invalidAction() {
        // Preconditions
        val searchEventResult = SearchEventResult(-1)

        mainMapPresenter.onSearchResult(searchEventResult)

        verify(mainMapView).showGeneralErrorMessage()
    }

    @Test
    fun cameraPositionPublisherTest_error() {
        // Preconditions
        mainMapPresenter.storeList = ArrayList()
        mainMapPresenter.subscribeMapCameraPositionChange()

        val bounds = LatLngBounds(southwest, northeast)
        mapCamPublisher.onError(UnknownException())

        verify(mainMapView).showGeneralErrorMessage()
    }

    @Test
    fun onMapCameraMoveTest_emptyStoreList() {
        // Preconditions
        mainMapPresenter.storeList = ArrayList()
        mainMapPresenter.subscribeMapCameraPositionChange()

        val bounds = LatLngBounds(southwest, northeast)
        mainMapPresenter.onMapCameraMove(latLng, bounds)

        verify(mainMapView).setNearByStores(ArrayList())
    }

    @Test
    fun onMapCameraMoveTest_haveStoreList_noStoreInBounds() {
        // Preconditions
        mainMapPresenter.storeList = stores
        mainMapPresenter.subscribeMapCameraPositionChange()

        val bounds = LatLngBounds(southwest, northeast)
        mainMapPresenter.onMapCameraMove(latLng, bounds)

        verify(mainMapView).setNearByStores(ArrayList())
    }

    @Test
    fun onMapCameraMoveTest_haveStoreList_allStoresInBounds() {
        // Preconditions
        mainMapPresenter.storeList = inBoundsStores
        mainMapPresenter.subscribeMapCameraPositionChange()

        val bounds = LatLngBounds(southwest, northeast)
        mainMapPresenter.onMapCameraMove(latLng, bounds)

        verify(mainMapView).setNearByStores(nearByStores)
    }

    @Test
    fun onMapCameraMoveTest_haveStoreList_partialStoresInBounds() {
        // Preconditions
        mainMapPresenter.storeList = partialInBoundsStores
        mainMapPresenter.subscribeMapCameraPositionChange()

        val bounds = LatLngBounds(southwest, northeast)
        mainMapPresenter.onMapCameraMove(latLng, bounds)

        verify(mainMapView).setNearByStores(nearByStores)
    }

    // Workaround solution
    private fun <T> anyObject(): T {
        return Mockito.anyObject<T>()
    }

    companion object {
        private const val STORE_ID = 123
        private const val STORE_TITLE = "store_title"
        private const val STORE_ADDRESS = "store_address"
        private const val STORE_LAT = "10.773996"
        private const val STORE_LNG = "106.6898035"
        private const val STORE_TEL = "012345678965"
        private const val STORE_TYPE = StoreType.TYPE_CIRCLE_K

        private const val STORE_INVALID_LAT = "0"
        private const val STORE_INVALID_LNG = "0"

        private val location = LatLngAlt(10.1234, 106.1234, 1.0)
        private val latLng = LatLng(10.1234, 106.1234)
        private val invalidLatLng = LatLng(999.0, 999.0)
        private val northeast = LatLng(10.4321, 106.4321)
        private val southwest = LatLng(10.1001, 106.1001)

        private val partialInBoundsStores = arrayListOf(
                Store(1, STORE_TITLE, STORE_ADDRESS, "10.1101", "106.1101", STORE_TEL, STORE_TYPE),
                Store(5, STORE_TITLE, STORE_ADDRESS, "10.9876", "107.9876", STORE_TEL, STORE_TYPE),
                Store(2, STORE_TITLE, STORE_ADDRESS, "10.4012", "106.4012", STORE_TEL, STORE_TYPE),
                Store(3, STORE_TITLE, STORE_ADDRESS, "10.1234", "106.1234", STORE_TEL, STORE_TYPE),
                Store(6, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE),
                Store(4, STORE_TITLE, STORE_ADDRESS, "10.3214", "106.3214", STORE_TEL, STORE_TYPE)
        )
        private val inBoundsStores = arrayListOf(
                Store(1, STORE_TITLE, STORE_ADDRESS, "10.1101", "106.1101", STORE_TEL, STORE_TYPE),
                Store(2, STORE_TITLE, STORE_ADDRESS, "10.4012", "106.4012", STORE_TEL, STORE_TYPE),
                Store(3, STORE_TITLE, STORE_ADDRESS, "10.1234", "106.1234", STORE_TEL, STORE_TYPE),
                Store(4, STORE_TITLE, STORE_ADDRESS, "10.3214", "106.3214", STORE_TEL, STORE_TYPE)
        )
        private val nearByStores = arrayListOf(
                NearByStore(Store(1, STORE_TITLE, STORE_ADDRESS, "10.1101", "106.1101", STORE_TEL, STORE_TYPE), 2.07098),
                NearByStore(Store(2, STORE_TITLE, STORE_ADDRESS, "10.4012", "106.4012", STORE_TEL, STORE_TYPE), 43.24741),
                NearByStore(Store(3, STORE_TITLE, STORE_ADDRESS, "10.1234", "106.1234", STORE_TEL, STORE_TYPE), 0.0),
                NearByStore(Store(4, STORE_TITLE, STORE_ADDRESS, "10.3214", "106.3214", STORE_TEL, STORE_TYPE), 30.82617)
        )
        private val stores = getFakeStoreList()
        private val circleKStores = getFakeCircleKStoreList()

        val store = Store(STORE_ID, STORE_TITLE, STORE_ADDRESS, STORE_LAT, STORE_LNG, STORE_TEL, STORE_TYPE)

        private val invalidMapsDirection = MapsDirection()
        private val mapsDirection = getFakeMapsDirection()
    }
}