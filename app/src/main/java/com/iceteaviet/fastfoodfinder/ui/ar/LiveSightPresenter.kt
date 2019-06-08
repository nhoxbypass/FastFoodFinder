package com.iceteaviet.fastfoodfinder.ui.ar

import android.location.Location
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
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
class LiveSightPresenter : BasePresenter<LiveSightContract.Presenter>, LiveSightContract.Presenter {

    private val liveSightView: LiveSightContract.View

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, liveSightView: LiveSightContract.View) : super(dataManager, schedulerProvider) {
        this.liveSightView = liveSightView
    }

    override fun subscribe() {
        if (isLolipopOrHigher() && !liveSightView.isLocationPermissionGranted()) {
            liveSightView.requestLocationPermission()
        } else {
            liveSightView.subscribeLocationUpdate()
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
        liveSightView.unsubscribeLocationUpdate()
        liveSightView.releaseARCamera()
        super.unsubscribe()
    }

    override fun onLocationPermissionGranted() {
        liveSightView.subscribeLocationUpdate()
    }

    override fun onCameraPermissionGranted() {
        liveSightView.initARCameraView()
    }

    override fun onCurrLocationChanged(location: Location) {
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

    override fun startArCamera() {
    }

    companion object {
        private const val RADIUS = 0.005
    }
}