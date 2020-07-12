package com.iceteaviet.fastfoodfinder.ui.store

import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import com.google.android.gms.maps.model.LatLng
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.location.LocationListener
import com.iceteaviet.fastfoodfinder.location.base.ILocationManager
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.getLatLngString
import com.iceteaviet.fastfoodfinder.utils.isLolipopOrHigher
import com.iceteaviet.fastfoodfinder.utils.isValidLocation
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Created by tom on 2019-04-18.
 */
open class StoreDetailPresenter : BasePresenter<StoreDetailContract.Presenter>, StoreDetailContract.Presenter, com.iceteaviet.fastfoodfinder.location.LocationListener {

    private val storeDetailView: StoreDetailContract.View

    @VisibleForTesting
    var currLocation: LatLng? = null

    @VisibleForTesting
    lateinit var currStore: Store

    private var locationManager: ILocationManager<com.iceteaviet.fastfoodfinder.location.LocationListener>

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider,
                locationManager: ILocationManager<com.iceteaviet.fastfoodfinder.location.LocationListener>, storeDetailView: StoreDetailContract.View) : super(dataManager, schedulerProvider) {
        this.storeDetailView = storeDetailView
        this.locationManager = locationManager
    }

    override fun subscribe() {
        storeDetailView.setToolbarTitle(currStore.title)

        val currUser = dataManager.getCurrentUser()

        storeDetailView.updateSignInState(currUser != null)

        dataManager.getComments(currStore.id.toString())
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<List<Comment>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(commentList: List<Comment>) {
                        storeDetailView.setStoreComments(commentList.toMutableList().asReversed())
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })

        if (isLolipopOrHigher() && !storeDetailView.isLocationPermissionGranted()) {
            storeDetailView.requestLocationPermission()
        } else {
            onLocationPermissionGranted()
        }
    }

    override fun unsubscribe() {
        unsubscribeLocationUpdate()
        super.unsubscribe()
    }

    override fun onLocationChanged(location: com.iceteaviet.fastfoodfinder.location.LatLngAlt) {
        currLocation = LatLng(location.latitude, location.longitude)
    }

    override fun onLocationFailed(type: Int) {
    }

    override fun onLocationPermissionGranted() {
        requestCurrentLocation()
        subscribeLocationUpdate()
    }

    override fun handleExtras(extras: Parcelable?) {
        if (extras != null && extras is Store) {
            currStore = extras
        } else {
            storeDetailView.exit()
        }
    }

    override fun requestCurrentLocation() {
        val lastLocation = locationManager.getCurrentLocation()
        if (lastLocation != null) {
            currLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
        } else {
            storeDetailView.showCannotGetLocationMessage()
        }
    }

    override fun onAddNewComment(comment: Comment?) {
        comment?.let {
            storeDetailView.addStoreComment(comment)
            storeDetailView.setAppBarExpanded(false)
            storeDetailView.scrollToCommentList()

            // Update comment data
            dataManager.insertOrUpdateComment(currStore.id.toString(), comment)
        }
    }

    override fun onCommentButtonClick() {
        val currUser = dataManager.getCurrentUser()
        if (currUser != null) {
            storeDetailView.showCommentEditorView()
        } else {
            storeDetailView.showLoginRequestToast()
        }
    }

    override fun onCallButtonClick() {
        if (currStore.tel.isNotEmpty()) {
            storeDetailView.startCallIntent(currStore.tel)
        } else {
            storeDetailView.showInvalidPhoneNumbWarning()
        }
    }

    override fun onNavigationButtonClick() {
        val storeLocation = currStore.getPosition()
        val queries = HashMap<String, String>()

        if (!isValidLocation(storeLocation)) {
            storeDetailView.showInvalidStoreLocationWarning()
            return
        }

        if (!isValidLocation(currLocation)) {
            storeDetailView.showCannotGetLocationMessage()
            return
        }

        val origin = getLatLngString(currLocation)
        val destination = getLatLngString(storeLocation)

        queries[GoogleMapsRoutingApiHelper.PARAM_ORIGIN] = origin
        queries[GoogleMapsRoutingApiHelper.PARAM_DESTINATION] = destination

        dataManager.getMapsDirection(queries, currStore)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<MapsDirection> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(mapsDirection: MapsDirection) {
                        if (mapsDirection.routeList.isNotEmpty())
                            storeDetailView.showMapRoutingView(currStore, mapsDirection)
                        else
                            storeDetailView.showGeneralErrorMessage()
                    }

                    override fun onError(e: Throwable) {
                        storeDetailView.showGeneralErrorMessage()
                    }
                })
    }

    override fun onAddToFavButtonClick() {
        val currUser = dataManager.getCurrentUser()
        if (currUser == null) {
            storeDetailView.showLoginRequestToast()
        }
    }

    override fun onSaveButtonClick() {
        val currUser = dataManager.getCurrentUser()
        if (currUser == null) {
            storeDetailView.showLoginRequestToast()
        }
    }

    override fun onBackButtonClick() {
        storeDetailView.exit()
    }

    private fun subscribeLocationUpdate() {
        locationManager.requestLocationUpdates()
        locationManager.subscribeLocationUpdate(this)
    }

    private fun unsubscribeLocationUpdate() {
        locationManager.unsubscribeLocationUpdate(this)
    }
}