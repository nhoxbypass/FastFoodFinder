package com.iceteaviet.fastfoodfinder.ui.ar

import android.location.Location
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.ar.model.AugmentedPOI
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.isLolipopOrHigher
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableRes
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by tom on 2019-04-16.
 */
class LiveSightPresenter : BasePresenter<LiveSightContract.Presenter>, LiveSightContract.Presenter {

    private val liveSightView: LiveSightContract.View

    constructor(dataManager: DataManager, liveSightView: LiveSightContract.View) : super(dataManager) {
        this.liveSightView = liveSightView
        this.liveSightView.presenter = this
    }

    override fun subscribe() {
        if (isLolipopOrHigher() && !liveSightView.isLocationPermissionGranted()) {
            liveSightView.requestLocationPermission()
        } else {
            liveSightView.subscribeLocationServices()
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
        liveSightView.unsubscribeLocationServices()
        liveSightView.releaseARCamera()
        super.unsubscribe()
    }

    override fun onLocationPermissionGranted() {
        liveSightView.subscribeLocationServices()
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
        private const val RADIUS = 0.005
    }
}