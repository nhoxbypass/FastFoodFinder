package com.iceteaviet.fastfoodfinder.ui.store

import android.location.Location
import android.os.Parcelable
import androidx.annotation.VisibleForTesting
import com.google.android.gms.maps.model.LatLng
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.location.LocationListener
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.getLatLngString
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Created by tom on 2019-04-18.
 */
class StoreDetailPresenter : BasePresenter<StoreDetailContract.Presenter>, StoreDetailContract.Presenter, LocationListener {

    private val storeDetailView: StoreDetailContract.View

    private var currLocation: LatLng? = null

    @VisibleForTesting
    var currStore: Store? = null

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, storeDetailView: StoreDetailContract.View) : super(dataManager, schedulerProvider) {
        this.storeDetailView = storeDetailView
        this.storeDetailView.presenter = this
    }

    override fun subscribe() {
        currStore?.let {
            storeDetailView.setToolbarTitle(it.title)

            dataManager.getComments(it.id.toString())
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
        }
    }

    override fun onLocationChanged(location: Location) {
        onCurrLocationChanged(location.latitude, location.longitude)
    }

    override fun onLocationFailed(type: Int) {
    }

    override fun onLocationPermissionGranted() {
        requestCurrentLocation()
        requestLocationUpdates()
    }

    override fun handleExtras(extras: Parcelable?) {
        if (extras != null) {
            currStore = extras as Store?

            if (currStore == null)
                storeDetailView.exit()
        } else {
            storeDetailView.exit()
        }
    }

    override fun requestLocationUpdates() {
        GoogleLocationManager.getInstance().subscribeLocationUpdate(this)
    }

    override fun requestCurrentLocation() {
        val lastLocation = GoogleLocationManager.getInstance().getCurrentLocation()
        if (lastLocation != null) {
            onCurrLocationChanged(lastLocation.latitude, lastLocation.longitude)
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
            dataManager.insertOrUpdateComment(currStore!!.id.toString(), comment)
        }
    }

    override fun onCurrLocationChanged(latitude: Double, longitude: Double) {
        currLocation = LatLng(latitude, longitude)
    }

    override fun onCommentButtonClick() {
        storeDetailView.showCommentEditorView()
    }

    override fun onCallButtonClick(tel: String?) {
        if (tel != null && tel.isNotEmpty()) {
            storeDetailView.startCallIntent(tel)
        } else {
            storeDetailView.showInvalidPhoneNumbWarning()
        }
    }

    override fun onNavigationButtonClick() {
        currStore?.let {
            val storeLocation = it.getPosition()
            val queries = HashMap<String, String>()

            val origin: String? = getLatLngString(currLocation)
            val destination: String? = getLatLngString(storeLocation)

            if (origin == null || destination == null)
                return

            queries[GoogleMapsRoutingApiHelper.PARAM_ORIGIN] = origin
            queries[GoogleMapsRoutingApiHelper.PARAM_DESTINATION] = destination

            dataManager.getMapsDirection(queries, it)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(object : SingleObserver<MapsDirection> {
                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable.add(d)
                        }

                        override fun onSuccess(mapsDirection: MapsDirection) {
                            storeDetailView.showMapRoutingView(it, mapsDirection)
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                        }
                    })
        }
    }
}