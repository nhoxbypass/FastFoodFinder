package com.iceteaviet.fastfoodfinder.ui.main.map

import android.os.Build
import com.google.android.gms.maps.model.LatLng
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.location.LocationListener
import com.iceteaviet.fastfoodfinder.utils.exception.UnknownException
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreList
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.setFinalStatic
import com.nhaarman.mockitokotlin2.capture
import io.reactivex.Single
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

    @Captor
    private lateinit var locationCallbackCaptor: ArgumentCaptor<LocationListener>

    private lateinit var mainMapPresenter: MainMapPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()

        mainMapPresenter = MainMapPresenter(dataManager, schedulerProvider, locationManager, mainMapView)
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

        verify(mainMapView).setupMap()
        verify(mainMapView).requestLocationPermission()
        verify(locationManager, never()).requestLocationUpdates()
        verify(locationManager, never()).subscribeLocationUpdate(mainMapPresenter)
    }

    @Test
    fun requestLocationUpdatesTest() {
        mainMapPresenter.subscribeLocationUpdate()

        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(mainMapPresenter)
    }

    @Test
    fun requestCurrentLocationTest_haveLastLocation() {
        // Preconditions
        `when`(locationManager.getCurrentLocation()).thenReturn(location)

        mainMapPresenter.requestCurrentLocation()

        assertThat(mainMapPresenter.currLocation).isNotNull()
        assertThat(mainMapPresenter.currLocation).isEqualTo(LatLng(location.latitude, location.longitude))
    }

    @Test
    fun requestCurrentLocationTest_notHaveLastLocation() {
        mainMapPresenter.requestCurrentLocation()

        verify(mainMapView).showCannotGetLocationMessage()
    }

    @Test
    fun onLocationChangeTest() {
        // Preconditions
        mainMapPresenter.onLocationPermissionGranted()

        verify(locationManager).getCurrentLocation()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(capture(locationCallbackCaptor))

        locationCallbackCaptor.value.onLocationChanged(location)

        assertThat(mainMapPresenter.currLocation).isNotNull()
        assertThat(mainMapPresenter.currLocation).isEqualTo(LatLng(location.latitude, location.longitude))
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

    companion object {
        private val location = LatLngAlt(10.1234, 106.1234, 1.0)

        private val stores = getFakeStoreList()
    }
}