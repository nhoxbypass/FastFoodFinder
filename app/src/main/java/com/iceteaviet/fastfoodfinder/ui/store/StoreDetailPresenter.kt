package com.iceteaviet.fastfoodfinder.ui.store

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.getLatLngString
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * Created by tom on 2019-04-18.
 */
class StoreDetailPresenter : BasePresenter<StoreDetailContract.Presenter>, StoreDetailContract.Presenter {

    private val storeDetailView: StoreDetailContract.View

    private var currLocation: LatLng? = null
    private var currStore: Store? = null

    constructor(dataManager: DataManager, storeDetailView: StoreDetailContract.View) : super(dataManager) {
        this.storeDetailView = storeDetailView
        this.storeDetailView.presenter = this
    }

    override fun subscribe() {
        currStore?.let {
            storeDetailView.setToolbarTitle(it.title)

            dataManager.getRemoteStoreDataSource().getComments(it.id.toString())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : SingleObserver<MutableList<Comment>> {
                        override fun onSubscribe(d: Disposable) {
                            compositeDisposable.add(d)
                        }

                        override fun onSuccess(commentList: MutableList<Comment>) {
                            storeDetailView.setStoreComments(commentList.asReversed())
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                        }
                    })
        }
    }

    override fun onLocationPermissionGranted() {
        storeDetailView.getLastLocation()
        storeDetailView.requestLocationUpdates()
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

    override fun onAddNewComment(comment: Comment?) {
        comment?.let {
            storeDetailView.addStoreComment(comment)
            storeDetailView.setAppBarExpanded(false)
            storeDetailView.scrollToCommentList()

            // Update comment data
            dataManager.getRemoteStoreDataSource().insertOrUpdateComment(currStore!!.id.toString(), comment)
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

            dataManager.getMapsRoutingApiHelper().getMapsDirection(queries, it)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
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