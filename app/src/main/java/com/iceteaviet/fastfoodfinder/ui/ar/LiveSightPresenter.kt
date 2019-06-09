package com.iceteaviet.fastfoodfinder.ui.ar

import android.location.Location
import android.os.Bundle
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.location.SystemLocationListener
import com.iceteaviet.fastfoodfinder.location.base.ILocationManager
import com.iceteaviet.fastfoodfinder.ui.ar.model.AugmentedPOI
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.isLolipopOrHigher
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableRes
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable


/**
 * Created by tom on 2019-04-16.
 */
class LiveSightPresenter : BasePresenter<LiveSightContract.Presenter>, LiveSightContract.Presenter, SystemLocationListener {

    private val liveSightView: LiveSightContract.View

    private val locationManager: ILocationManager<SystemLocationListener>

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider,
                locationManager: ILocationManager<SystemLocationListener>, liveSightView: LiveSightContract.View) : super(dataManager, schedulerProvider) {
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

    override fun subscribeLocationUpdate() {
        locationManager.requestLocationUpdates()
        locationManager.subscribeLocationUpdate(this)
    }

    override fun unsubscribeLocationUpdate() {
        locationManager.unsubscribeLocationUpdate(this)
    }

    override fun requestCurrentLocation() {
        val lastLocation = locationManager.getCurrentLocation()
        if (lastLocation != null) {
            onLocationChanged(lastLocation)
        } else {
            liveSightView.showCannotGetLocationMessage()
        }
    }

    override fun onLocationChanged(location: Location) {
        liveSightView.updateLatestLocation(location)
        dataManager.getStoreInBounds(location.latitude - RADIUS,
                location.longitude - RADIUS,
                location.latitude + RADIUS,
                location.longitude + RADIUS)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        for (i in storeList.indices) {
                            liveSightView.addARPoint(AugmentedPOI(storeList[i].title,
                                    storeList[i].lat.toDouble(),
                                    storeList[i].lng.toDouble(),
                                    0.0,
                                    getStoreLogoDrawableRes(storeList[i].type)))
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    override fun onLocationFailed(type: Int) {
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
    }

    override fun onProviderEnabled(provider: String) {
    }

    override fun onProviderDisabled(provider: String) {
    }

    companion object {
        private const val RADIUS = 0.005
    }
}