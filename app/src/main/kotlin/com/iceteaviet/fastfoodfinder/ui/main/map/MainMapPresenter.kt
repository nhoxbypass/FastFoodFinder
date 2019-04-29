package com.iceteaviet.fastfoodfinder.ui.main.map

import android.text.TextUtils
import android.util.Pair
import android.util.SparseArray
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.firebase.perf.metrics.AddTrace
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.transport.model.SearchEventResult
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.ui.main.map.model.MapCameraPosition
import com.iceteaviet.fastfoodfinder.ui.main.map.model.NearByStore
import com.iceteaviet.fastfoodfinder.utils.calcDistance
import com.iceteaviet.fastfoodfinder.utils.getLatLngString
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.HashMap
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.set

/**
 * Created by tom on 2019-04-18.
 */
class MainMapPresenter : BasePresenter<MainMapContract.Presenter>, MainMapContract.Presenter {

    private val mainMapView: MainMapContract.View

    private var currLocation: LatLng? = null
    private var mStoreList: MutableList<Store>? = null
    private var visibleStores: List<Store>? = null
    private var isZoomToUser = false

    private var markerSparseArray: SparseArray<Marker> = SparseArray() // pair storeId - marker

    private var newVisibleStorePublisher: PublishSubject<Store>? = null
    private var cameraPositionPublisher: PublishSubject<MapCameraPosition>? = null

    constructor(dataManager: DataManager, mainMapView: MainMapContract.View) : super(dataManager) {
        this.mainMapView = mainMapView
        this.mainMapView.presenter = this
    }

    override fun subscribe() {
        mStoreList = ArrayList()
        visibleStores = ArrayList()
        newVisibleStorePublisher = PublishSubject.create()
        cameraPositionPublisher = PublishSubject.create()


        EventBus.getDefault().register(this)
        mainMapView.setupMap()

        dataManager.getLocalStoreDataSource().getAllStores()
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        mStoreList = storeList.toMutableList()
                        if (mStoreList == null || mStoreList!!.size <= 0)
                            mainMapView.showWarningMessage(R.string.get_store_data_failed)
                    }

                    override fun onError(e: Throwable) {
                        mainMapView.showWarningMessage(R.string.get_store_data_failed)
                    }
                })
    }

    override fun unsubscribe() {
        super.unsubscribe()
        newVisibleStorePublisher!!.onComplete()
        EventBus.getDefault().unregister(this)
    }

    override fun onLocationPermissionGranted() {
        mainMapView.setMyLocationEnabled(true)
        mainMapView.getLastLocation()
        mainMapView.requestLocationUpdates()

        // Showing the current location in Google Map
        mainMapView.animateMapCamera(currLocation!!, false)
    }

    override fun onGoogleApiConnected() {

    }

    override fun onCurrLocationChanged(latitude: Double, longitude: Double) {
        currLocation = LatLng(latitude, longitude)
    }

    override fun onMapCameraMove(cameraPosition: LatLng, bounds: LatLngBounds) {
        cameraPositionPublisher?.onNext(MapCameraPosition(cameraPosition, bounds))
    }

    override fun onGetMapAsync() {
        mainMapView.addMarkersToMap(mStoreList!!)

        mainMapView.setupMapEventHandlers()

        cameraPositionPublisher!!
                .debounce(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<MapCameraPosition> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onNext(cameraPosition: MapCameraPosition) {
                        visibleStores = getVisibleStore(mStoreList!!, cameraPosition.cameraBounds)
                        visibleStores?.let {
                            mainMapView.setNearByStores(generateNearByStoresWithDistance(cameraPosition.cameraPosition, it))
                        }
                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })

        newVisibleStorePublisher!!
                .observeOn(Schedulers.computation())
                .map { store ->
                    val marker = markerSparseArray.get(store.id)

                    // TODO: warm up cache
                    /*val animator = getMarkerAnimator()
                    animator.addUpdateListener { animation ->
                        val scale = animation.animatedValue as Float
                        try {
                            getStoreIcon(resources, store.type, Math.round(scale * 75), Math.round(scale * 75)) // warm up cache
                        } catch (ex: IllegalArgumentException) {
                            ex.printStackTrace()
                        }
                    }
                    animator.start()*/

                    Pair(marker, store.type)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Pair<Marker, Int>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onNext(pair: Pair<Marker, Int>) {
                        mainMapView.animateMapMarker(pair.first, pair.second)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                    override fun onComplete() {
                    }
                })
    }

    override fun onLocationChanged(latitude: Double, longitude: Double) {
        currLocation = LatLng(latitude, longitude)
        // Creating a LatLng object for the current location

        if (!isZoomToUser) {
            // Zoom and show current location in the Google Map
            mainMapView.animateMapCamera(currLocation!!, false)

            isZoomToUser = true
        }
    }

    override fun onDirectionNavigateClick(store: Store) {
        val storeLocation = store.getPosition()
        val queries = HashMap<String, String>()

        val origin = getLatLngString(currLocation)
        val destination = getLatLngString(storeLocation)

        if (TextUtils.isEmpty(origin) || TextUtils.isEmpty(destination))
            return

        queries[GoogleMapsRoutingApiHelper.PARAM_ORIGIN] = origin
        queries[GoogleMapsRoutingApiHelper.PARAM_DESTINATION] = destination

        dataManager.getMapsRoutingApiHelper().getMapsDirection(queries, store)
                .subscribe(object : SingleObserver<MapsDirection> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(mapsDirection: MapsDirection) {
                        mainMapView.showMapRoutingView(store, mapsDirection)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }

    override fun onMapMarkerAdd(storeId: Int, marker: Marker) {
        markerSparseArray.put(storeId, marker)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchResult(searchEventResult: SearchEventResult) {
        when (searchEventResult.resultCode) {
            SearchEventResult.SEARCH_ACTION_QUICK -> {
                dataManager.getLocalStoreDataSource()
                        .findStoresByType(searchEventResult.storeType)
                        .subscribe(object : SingleObserver<List<Store>> {
                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable.add(d)
                            }

                            override fun onSuccess(storeList: List<Store>) {
                                if (storeList.isEmpty()) {
                                    mainMapView.showWarningMessage(R.string.store_not_found)
                                    return
                                }

                                mStoreList = storeList.toMutableList()
                                mainMapView.addMarkersToMap(mStoreList!!)
                                mainMapView.animateMapCamera(storeList[0].getPosition(), false)

                                /*mAdapter!!.setCurrCameraPosition(mGoogleMap!!.cameraPosition.target)
                                visibleStores = getVisibleStore(mStoreList!!, mGoogleMap!!.projection.visibleRegion.latLngBounds)
                                visibleStores?.let { mAdapter!!.setStores(it) }*/
                            }

                            override fun onError(e: Throwable) {
                                mainMapView.showWarningMessage(R.string.get_store_data_failed)
                            }
                        })
            }
            SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT -> {
                if (searchEventResult.searchString.isBlank())
                    return

                dataManager.getLocalStoreDataSource()
                        .findStores(searchEventResult.searchString)
                        .subscribe(object : SingleObserver<List<Store>> {
                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable.add(d)
                            }

                            override fun onSuccess(storeList: List<Store>) {
                                if (storeList.isEmpty()) {
                                    mainMapView.showWarningMessage(R.string.store_not_found)
                                    return
                                }

                                mStoreList = storeList.toMutableList()
                                mainMapView.addMarkersToMap(mStoreList!!)
                                mainMapView.animateMapCamera(storeList[0].getPosition(), false)

                                /*mAdapter!!.setCurrCameraPosition(mGoogleMap!!.cameraPosition.target)
                                visibleStores = getVisibleStore(mStoreList!!, mGoogleMap!!.projection.visibleRegion.latLngBounds)
                                visibleStores?.let { mAdapter!!.setStores(it) }*/
                            }

                            override fun onError(e: Throwable) {
                                mainMapView.showWarningMessage(R.string.get_store_data_failed)
                            }
                        })
            }

            SearchEventResult.SEARCH_ACTION_COLLAPSE -> {
                dataManager.getLocalStoreDataSource().getAllStores()
                        .subscribe(object : SingleObserver<List<Store>> {
                            override fun onSubscribe(d: Disposable) {
                                compositeDisposable.add(d)
                            }

                            override fun onSuccess(storeList: List<Store>) {
                                if (storeList.isEmpty()) {
                                    mainMapView.showWarningMessage(R.string.get_store_data_failed)
                                    return
                                }

                                mStoreList = storeList.toMutableList()
                                mainMapView.addMarkersToMap(mStoreList!!)
                            }

                            override fun onError(e: Throwable) {
                                mainMapView.showWarningMessage(R.string.get_store_data_failed)
                            }
                        })

                if (currLocation != null)
                    mainMapView.animateMapCamera(currLocation!!, false)
            }

            SearchEventResult.SEARCH_ACTION_STORE_CLICK -> {
                if (searchEventResult.store != null) {
                    mStoreList = ArrayList()
                    mStoreList!!.add(searchEventResult.store!!)

                    mainMapView.addMarkersToMap(mStoreList!!)
                    mainMapView.animateMapCamera(searchEventResult.store!!.getPosition(), false)

                    mainMapView.clearNearByStores()

                    mainMapView.showDialogStoreInfo(searchEventResult.store!!)
                }
            }
        }
    }

    @AddTrace(name = "getVisibleStore")
    private fun getVisibleStore(storeList: List<Store>, bounds: LatLngBounds): List<Store> {
        val stores = ArrayList<Store>()

        for (i in storeList.indices) {
            val store = storeList[i]
            if (bounds.contains(store.getPosition())) {
                // Inside visible range
                stores.add(store)
                if (!this.visibleStores!!.contains(store)) {
                    // New store become visible
                    newVisibleStorePublisher!!.onNext(store)
                }
            }
        }

        return stores
    }

    private fun generateNearByStoresWithDistance(currPos: LatLng, stores: List<Store>): List<NearByStore> {
        val res = ArrayList<NearByStore>()

        for (store in stores) {
            res.add(NearByStore(store, calcDistance(currPos, store.getPosition())))
        }

        return res
    }
}