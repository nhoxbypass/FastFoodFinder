package com.iceteaviet.fastfoodfinder.ui.ar

import androidx.annotation.VisibleForTesting
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.location.LocationListener
import com.iceteaviet.fastfoodfinder.location.base.ILocationManager
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.isLolipopOrHigher
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.storesToArPoints
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable


/**
 * Created by tom on 2019-04-16.
 */
class LiveSightPresenter : BasePresenter<LiveSightContract.Presenter>, LiveSightContract.Presenter, LocationListener {

    private val liveSightView: LiveSightContract.View

    private val locationManager: ILocationManager

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider,
                locationManager: ILocationManager, liveSightView: LiveSightContract.View) : super(dataManager, schedulerProvider) {
        this.liveSightView = liveSightView
        this.locationManager = locationManager
    }

    override fun subscribe() {
        if (isLolipopOrHigher() && !liveSightView.isLocationPermissionGranted()) {
            liveSightView.requestLocationPermission()
        } else {
            onLocationPermissionGranted()
        }

        if (isLolipopOrHigher() && !liveSightView.isCameraPermissionGranted()) {
            liveSightView.requestCameraPermission()
        } else {
            liveSightView.initARCameraView()
        }

        liveSightView.initAROverlayView()
        liveSightView.initSensorService()
    }

    override fun unsubscribe() {
        unsubscribeLocationUpdate()
        liveSightView.releaseARCamera()
        super.unsubscribe()
    }

    override fun onLocationPermissionGranted() {
        subscribeLocationUpdate()
        requestCurrentLocation()
    }

    override fun onCameraPermissionGranted() {
        liveSightView.initARCameraView()
    }

    override fun startArCamera() {
    }

    override fun requestCurrentLocation() {
        val lastLocation = locationManager.getCurrentLocation()
        if (lastLocation != null) {
            onLocationChanged(lastLocation)
        } else {
            liveSightView.showCannotGetLocationMessage()
        }
    }

    override fun onLocationChanged(location: LatLngAlt) {
        liveSightView.updateLatestLocation(location)
        dataManager.getStoreInBounds(location.latitude, location.longitude, RADIUS)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        liveSightView.setARPoints(storesToArPoints(storeList))
                    }

                    override fun onError(e: Throwable) {
                        liveSightView.showGeneralErrorMessage()
                    }
                })
    }

    override fun onLocationFailed(type: Int) {
    }

    private fun subscribeLocationUpdate() {
        locationManager.requestLocationUpdates()
        locationManager.subscribeLocationUpdate(this)
    }

    private fun unsubscribeLocationUpdate() {
        locationManager.unsubscribeLocationUpdate(this)
    }

    companion object {
        @VisibleForTesting
        const val RADIUS = 0.01
    }
}