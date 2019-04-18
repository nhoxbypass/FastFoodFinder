package com.iceteaviet.fastfoodfinder.ui.ar

import android.location.Location
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.local.poi.model.AugmentedPOI
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.isLolipopOrHigher
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableRes
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by tom on 2019-04-16.
 */
class LiveSightPresenter : BasePresenter<LiveSightContract.Presenter>, LiveSightContract.Presenter {

    val liveSightView: LiveSightContract.View

    constructor(dataManager: DataManager, liveSightView: LiveSightContract.View) : super(dataManager) {
        this.liveSightView = liveSightView
        this.liveSightView.presenter = this
    }

    override fun subscribe() {
        if (isLolipopOrHigher() && !liveSightView.isLocationPermissionGranted()) {
            liveSightView.requestLocationPermission()
        } else {
            liveSightView.initLocationService(MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES)
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
        liveSightView.releaseARCamera()
        compositeDisposable.clear()
    }

    override fun onLocationPermissionGranted() {
        liveSightView.initLocationService(MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES)
    }

    override fun onCameraPermissionGranted() {
        liveSightView.initARCameraView()
    }

    override fun onLocationChanged(location: Location) {
        liveSightView.updateLatestLocation(location)
        dataManager.getLocalStoreDataSource().getStoreInBounds(location.latitude - RADIUS,
                location.longitude - RADIUS,
                location.latitude + RADIUS,
                location.longitude + RADIUS)
                .observeOn(Schedulers.computation())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        for (i in storeList.indices) {
                            liveSightView.addARPoint(AugmentedPOI(storeList[i].title!!,
                                    storeList[i].lat!!.toDouble(),
                                    storeList[i].lng!!.toDouble(),
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
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // 10 meters
        private const val MIN_TIME_BW_UPDATES = (1000 * 30).toLong() // 30 seconds
        private const val RADIUS = 0.005
    }
}