package com.iceteaviet.fastfoodfinder.ui.ar

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.location.SystemLocationListener
import com.iceteaviet.fastfoodfinder.location.SystemLocationManager
import com.iceteaviet.fastfoodfinder.utils.exception.UnknownException
import com.iceteaviet.fastfoodfinder.utils.getFakeArPoints
import com.iceteaviet.fastfoodfinder.utils.getFakeStoreList
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.capture
import com.nhaarman.mockitokotlin2.never
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 * Created by tom on 2019-06-09.
 */
class LiveSightPresenterTest {
    private lateinit var mockAnnotations: AutoCloseable
    
    @Mock
    private lateinit var liveSightView: LiveSightContract.View

    @Mock
    private lateinit var dataManager: DataManager

    @Mock
    private lateinit var locationManager: SystemLocationManager

    @Captor
    private lateinit var locationCallbackCaptor: ArgumentCaptor<SystemLocationListener>

    private lateinit var liveSightPresenter: LiveSightPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        mockAnnotations = MockitoAnnotations.openMocks(this)

        schedulerProvider = TrampolineSchedulerProvider()

        // Get a reference to the class under test
        liveSightPresenter = LiveSightPresenter(dataManager, schedulerProvider, locationManager, liveSightView)
    }

    @After
    fun tearDown() {
        mockAnnotations.close()
    }

    @Test
    fun subscribeTest_locationPermissionNotGranted() {
        `when`(liveSightView.isLocationPermissionGranted()).thenReturn(false)

        liveSightPresenter.subscribe()

        verify(liveSightView).requestLocationPermission()
        verify(locationManager, Mockito.never()).requestLocationUpdates()
        verify(locationManager, Mockito.never()).subscribeLocationUpdate(liveSightPresenter)
    }

    @Test
    fun subscribeTest_locationPermissionGranted() {
        // Preconditions
        `when`(liveSightView.isLocationPermissionGranted()).thenReturn(true)

        liveSightPresenter.subscribe()

        verify(liveSightView, Mockito.never()).requestLocationPermission()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(liveSightPresenter)
    }

    @Test
    fun subscribeTest_cameraPermissionNotGranted() {
        // Preconditions
        `when`(liveSightView.isCameraPermissionGranted()).thenReturn(false)

        liveSightPresenter.subscribe()

        verify(liveSightView).requestCameraPermission()
        verify(liveSightView, never()).initARCameraView()
    }

    @Test
    fun subscribeTest_cameraPermissionGranted() {
        // Preconditions
        `when`(liveSightView.isCameraPermissionGranted()).thenReturn(true)

        liveSightPresenter.subscribe()

        verify(liveSightView, never()).requestCameraPermission()
        verify(liveSightView).initARCameraView()
    }

    @Test
    fun unsubscribeTest() {
        liveSightPresenter.unsubscribe()

        verify(locationManager).unsubscribeLocationUpdate(liveSightPresenter)
        verify(liveSightView).releaseARCamera()
    }

    @Test
    fun onLocationPermissionGrantedTest() {
        liveSightPresenter.onLocationPermissionGranted()

        verify(locationManager).getCurrentLocation()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(liveSightPresenter)
    }

    @Test
    fun onCameraPermissionGrantedTest() {
        liveSightPresenter.onCameraPermissionGranted()

        verify(liveSightView).initARCameraView()
    }

    @Test
    fun requestCurrentLocationTest_haveLastLocation_getStoreInBoundsError() {
        // Preconditions
        `when`(locationManager.getCurrentLocation()).thenReturn(location)
        `when`(dataManager.getStoreInBounds(location.latitude, location.longitude, LiveSightPresenter.RADIUS))
                .thenReturn(Single.error(UnknownException()))

        liveSightPresenter.requestCurrentLocation()

        verify(liveSightView).updateLatestLocation(location)
        verify(liveSightView).showGeneralErrorMessage()
    }

    @Test
    fun requestCurrentLocationTest_haveLastLocation_getStoreInBoundsEmpty() {
        // Preconditions
        `when`(locationManager.getCurrentLocation()).thenReturn(location)
        `when`(dataManager.getStoreInBounds(location.latitude, location.longitude, LiveSightPresenter.RADIUS))
                .thenReturn(Single.just(ArrayList()))

        liveSightPresenter.requestCurrentLocation()

        verify(liveSightView).updateLatestLocation(location)
        verify(liveSightView).setARPoints(ArrayList())
    }

    @Test
    fun requestCurrentLocationTest_haveLastLocation_getStoreInBoundsSuccess() {
        // Preconditions
        `when`(locationManager.getCurrentLocation()).thenReturn(location)
        `when`(dataManager.getStoreInBounds(location.latitude, location.longitude, LiveSightPresenter.RADIUS))
                .thenReturn(Single.just(stores))

        liveSightPresenter.requestCurrentLocation()

        verify(liveSightView).updateLatestLocation(location)
        verify(liveSightView).setARPoints(arPoints)
    }

    @Test
    fun requestCurrentLocationTest_notHaveLastLocation() {
        liveSightPresenter.requestCurrentLocation()

        verify(liveSightView).showCannotGetLocationMessage()
    }

    @Test
    fun onLocationChangeTest_getStoreInBoundsError() {
        // Preconditions
        liveSightPresenter.onLocationPermissionGranted()
        `when`(dataManager.getStoreInBounds(location.latitude, location.longitude, LiveSightPresenter.RADIUS))
                .thenReturn(Single.error(UnknownException()))

        verify(locationManager).getCurrentLocation()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(capture(locationCallbackCaptor))

        locationCallbackCaptor.value.onLocationChanged(location)

        verify(liveSightView).updateLatestLocation(location)
        verify(liveSightView).showGeneralErrorMessage()
    }

    @Test
    fun onLocationChangeTest_getStoreInBoundsEmpty() {
        // Preconditions
        liveSightPresenter.onLocationPermissionGranted()
        `when`(dataManager.getStoreInBounds(location.latitude, location.longitude, LiveSightPresenter.RADIUS))
                .thenReturn(Single.just(ArrayList()))

        verify(locationManager).getCurrentLocation()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(capture(locationCallbackCaptor))

        locationCallbackCaptor.value.onLocationChanged(location)

        verify(liveSightView).updateLatestLocation(location)
        verify(liveSightView).setARPoints(ArrayList())
    }

    @Test
    fun onLocationChangeTest_getStoreInBoundsSuccess() {
        // Preconditions
        liveSightPresenter.onLocationPermissionGranted()
        `when`(dataManager.getStoreInBounds(location.latitude, location.longitude, LiveSightPresenter.RADIUS))
                .thenReturn(Single.just(stores))

        verify(locationManager).getCurrentLocation()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(capture(locationCallbackCaptor))

        locationCallbackCaptor.value.onLocationChanged(location)

        verify(liveSightView).updateLatestLocation(location)
        verify(liveSightView).setARPoints(arPoints)
    }

    companion object {
        private val location = LatLngAlt(10.1234, 106.1234, 1.0)

        private val stores = getFakeStoreList()
        private val arPoints = getFakeArPoints()
    }
}