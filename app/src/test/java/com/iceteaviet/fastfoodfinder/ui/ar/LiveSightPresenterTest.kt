package com.iceteaviet.fastfoodfinder.ui.ar

import android.os.Build
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.location.SystemLocationManager
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.setFinalStatic
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

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

    @Test
    fun subscribeTest_locationPermissionGranted() {
        // Preconditions
        Mockito.`when`(liveSightView.isLocationPermissionGranted()).thenReturn(true)

        liveSightPresenter.subscribe()

        Mockito.verify(liveSightView, Mockito.never()).requestLocationPermission()
        Mockito.verify(locationManager).requestLocationUpdates()
        Mockito.verify(locationManager).subscribeLocationUpdate(liveSightPresenter)
    }

    @Test
    fun subscribeTest_devicePreLolipop() {
        // Preconditions
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 18)

        liveSightPresenter.subscribe()

        Mockito.verify(liveSightView, Mockito.never()).requestLocationPermission()
        Mockito.verify(locationManager).requestLocationUpdates()
        Mockito.verify(locationManager).subscribeLocationUpdate(liveSightPresenter)
    }

    @Test
    fun subscribeTest_locationPermissionNotGranted() {
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), 24)
        Mockito.`when`(liveSightView.isLocationPermissionGranted()).thenReturn(false)

        liveSightPresenter.subscribe()

        Mockito.verify(liveSightView).requestLocationPermission()
        Mockito.verify(locationManager, Mockito.never()).requestLocationUpdates()
        Mockito.verify(locationManager, Mockito.never()).subscribeLocationUpdate(liveSightPresenter)
    }

    companion object {

    }
}