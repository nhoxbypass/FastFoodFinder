package com.iceteaviet.fastfoodfinder.ui.ar

import com.iceteaviet.fastfoodfinder.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.ui.ar.model.AugmentedPOI
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-16.
 */

interface LiveSightContract {
    interface View : BaseView<Presenter> {
        fun requestLocationPermission()
        fun isLocationPermissionGranted(): Boolean
        fun showCannotGetLocationMessage()
        fun requestCameraPermission()
        fun isCameraPermissionGranted(): Boolean
        fun updateLatestLocation(latestLocation: LatLngAlt)
        fun initARCameraView()
        fun initAROverlayView()
        fun initSensorService()
        fun releaseARCamera()
        fun addARPoint(arPoi: AugmentedPOI)
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun startArCamera()
        fun onLocationPermissionGranted()
        fun onCameraPermissionGranted()
        fun subscribeLocationUpdate()
        fun unsubscribeLocationUpdate()
        fun requestCurrentLocation()
    }
}