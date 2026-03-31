package com.iceteaviet.fastfoodfinder.ui.store

import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import com.google.android.gms.maps.model.LatLng
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.core.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.core.location.LocationListener
import com.iceteaviet.fastfoodfinder.core.location.base.ILocationManager
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.getLatLngString
import com.iceteaviet.fastfoodfinder.utils.isLolipopOrHigher
import com.iceteaviet.fastfoodfinder.utils.isValidLocation
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

/**
 * Created by tom on 2019-04-18.
 */
open class StoreDetailPresenter(
    private val clientAuth: com.iceteaviet.fastfoodfinder.data.auth.ClientAuth,
    private val userRepository: com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository,
    private val storeRepository: com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository,
    private val mapsRoutingRepository: com.iceteaviet.fastfoodfinder.data.domain.routing.MapsRoutingRepository,
    schedulerProvider: SchedulerProvider,
    private var locationManager: com.iceteaviet.fastfoodfinder.core.location.base.ILocationManager,
    private val storeDetailView: StoreDetailContract.View
) : BasePresenter<StoreDetailContract.Presenter>(schedulerProvider), StoreDetailContract.Presenter, LocationListener {

    
    @VisibleForTesting
    var currLocation: LatLng? = null

    @VisibleForTesting
    lateinit var currStore: Store

    
    

    override fun subscribe() {
        storeDetailView.setToolbarTitle(currStore.title)

        val currUser = com.iceteaviet.fastfoodfinder.utils.getCurrentUserHelper(clientAuth, userRepository)

        storeDetailView.updateSignInState(currUser != null)

        storeRepository.getComments(currStore.id.toString())
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

    override fun onLocationChanged(location: LatLngAlt) {
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
            storeRepository.insertOrUpdateComment(currStore.id.toString(), comment)
        }
    }

    override fun onCommentButtonClick() {
        val currUser = com.iceteaviet.fastfoodfinder.utils.getCurrentUserHelper(clientAuth, userRepository)
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

        mapsRoutingRepository.getMapsDirection(queries, currStore)
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
        val currUser = com.iceteaviet.fastfoodfinder.utils.getCurrentUserHelper(clientAuth, userRepository)
        if (currUser == null) {
            storeDetailView.showLoginRequestToast()
        }
    }

    override fun onSaveButtonClick() {
        val currUser = com.iceteaviet.fastfoodfinder.utils.getCurrentUserHelper(clientAuth, userRepository)
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