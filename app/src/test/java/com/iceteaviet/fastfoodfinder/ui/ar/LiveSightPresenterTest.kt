package com.iceteaviet.fastfoodfinder.ui.ar

import android.os.Build
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.location.SystemLocationListener
import com.iceteaviet.fastfoodfinder.location.SystemLocationManager
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.setFinalStatic
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.verify

/**
 * Created by tom on 2019-06-09.
 */
class LiveSightPresenterTest {
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
        MockitoAnnotations.initMocks(this)

        schedulerProvider = TrampolineSchedulerProvider()

        // Get a reference to the class under test
        liveSightPresenter = LiveSightPresenter(dataManager, schedulerProvider, locationManager, liveSightView)
    }

    @After
    fun tearDown() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 0)
    }

    @Test
    fun subscribeTest_locationPermissionGranted() {
        // Preconditions
        Mockito.`when`(liveSightView.isLocationPermissionGranted()).thenReturn(true)

        liveSightPresenter.subscribe()

        verify(liveSightView, Mockito.never()).requestLocationPermission()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(liveSightPresenter)
    }

    @Test
    fun subscribeTest_devicePreLolipop() {
        // Preconditions
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 18)

        liveSightPresenter.subscribe()

        verify(liveSightView, Mockito.never()).requestLocationPermission()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(liveSightPresenter)
    }

    @Test
    fun subscribeTest_locationPermissionNotGranted() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 24)
        Mockito.`when`(liveSightView.isLocationPermissionGranted()).thenReturn(false)

        liveSightPresenter.subscribe()

        verify(liveSightView).requestLocationPermission()
        verify(locationManager, Mockito.never()).requestLocationUpdates()
        verify(locationManager, Mockito.never()).subscribeLocationUpdate(liveSightPresenter)
    }

    @Test
    fun onLocationPermissionGrantedTest() {
        liveSightPresenter.onLocationPermissionGranted()

        verify(locationManager).getCurrentLocation()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(liveSightPresenter)
    }

    @Test
    fun requestLocationUpdatesTest() {
        liveSightPresenter.subscribeLocationUpdate()

        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(liveSightPresenter)
    }

    @Test
    fun requestCurrentLocationTest_haveLastLocation() {
        /*// Preconditions
        `when`(locationManager.getCurrentLocation()).thenReturn(location)

        liveSightPresenter.requestCurrentLocation()*/
    }

    @Test
    fun requestCurrentLocationTest_notHaveLastLocation() {
        liveSightPresenter.requestCurrentLocation()

        verify(liveSightView).showCannotGetLocationMessage()
    }

    @Test
    fun onLocationChangeTest() {
        /*// Preconditions
        liveSightPresenter.onLocationPermissionGranted()

        verify(locationManager).getCurrentLocation()
        verify(locationManager).requestLocationUpdates()
        verify(locationManager).subscribeLocationUpdate(capture(locationCallbackCaptor))

        locationCallbackCaptor.value.onLocationChanged(location)*/
    }

    companion object {
        private val location = LatLngAlt(10.1234, 106.1234, 1.0)
    }
}