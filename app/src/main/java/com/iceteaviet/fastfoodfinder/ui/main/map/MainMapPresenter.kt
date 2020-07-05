package com.iceteaviet.fastfoodfinder.ui.main.map

import androidx.annotation.VisibleForTesting
import androidx.collection.SparseArrayCompat
import androidx.core.util.Pair
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.firebase.perf.metrics.AddTrace
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.location.LatLngAlt
import com.iceteaviet.fastfoodfinder.location.LocationListener
import com.iceteaviet.fastfoodfinder.location.base.ILocationManager
import com.iceteaviet.fastfoodfinder.service.eventbus.SearchEventResult
import com.iceteaviet.fastfoodfinder.service.eventbus.core.IBus
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.ui.main.map.model.MapCameraPosition
import com.iceteaviet.fastfoodfinder.ui.main.map.model.NearByStore
import com.iceteaviet.fastfoodfinder.utils.distanceBetween
import com.iceteaviet.fastfoodfinder.utils.getLatLngString
import com.iceteaviet.fastfoodfinder.utils.isLolipopOrHigher
import com.iceteaviet.fastfoodfinder.utils.isValidLocation
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.set

/**
 * Created by tom on 2019-04-18.
 */
open class MainMapPresenter : BasePresenter<MainMapContract.Presenter>, MainMapContract.Presenter, LocationListener {

    private val mainMapView: MainMapContract.View
    private val bus: IBus

    @VisibleForTesting
    var currLocation: LatLng? = null
    @VisibleForTesting
    var storeList: List<Store> = ArrayList()
    private var visibleStores: List<Store> = ArrayList()
    @VisibleForTesting
    var isZoomToUser = false
    @VisibleForTesting
    var locationGranted = false

    // Pair storeId - marker
    // User SparseArrayCompat to support unit testing: https://stackoverflow.com/questions/37209270/unit-test-sparsearray-using-junit-using-jvm
    @VisibleForTesting
    var markerSparseArray: SparseArrayCompat<Marker> = SparseArrayCompat()

    private var newVisibleStorePublisher: PublishSubject<Store>
    private var cameraPositionPublisher: PublishSubject<MapCameraPosition>

    private var locationManager: ILocationManager<LocationListener>

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider,
                locationManager: ILocationManager<LocationListener>, bus: IBus, storePublisher: PublishSubject<Store>, mapCamPublisher: PublishSubject<MapCameraPosition>, mainMapView: MainMapContract.View) : super(dataManager, schedulerProvider) {
        this.mainMapView = mainMapView
        this.locationManager = locationManager
        this.bus = bus
        this.newVisibleStorePublisher = storePublisher
        this.cameraPositionPublisher = mapCamPublisher
    }

    override fun subscribe() {
        resetState()

        if (isLolipopOrHigher() && !mainMapView.isLocationPermissionGranted()) {
            mainMapView.requestLocationPermission()
        } else {
            onLocationPermissionGranted()
        }

        bus.register(this)

        mainMapView.setupMap()

        loadAllStoresToMap()
    }

    override fun unsubscribe() {
        cameraPositionPublisher.onComplete()
        newVisibleStorePublisher.onComplete()
        bus.unregister(this)
        unsubscribeLocationUpdate()
        super.unsubscribe()
    }

    override fun onLocationPermissionGranted() {
        subscribeLocationUpdate()
        requestCurrentLocation()
        mainMapView.setMyLocationEnabled(true)
        locationGranted = true
    }

    override fun requestCurrentLocation() {
        val lastLocation = locationManager.getCurrentLocation()
        if (lastLocation != null) {
            onCurrLocationChanged(lastLocation.latitude, lastLocation.longitude)
        }
    }

    override fun onLocationChanged(location: LatLngAlt) {
        onCurrLocationChanged(location.latitude, location.longitude)
    }

    override fun onLocationFailed(type: Int) {

    }

    override fun onMapCameraMove(cameraPosition: LatLng, bounds: LatLngBounds) {
        cameraPositionPublisher.onNext(MapCameraPosition(cameraPosition, bounds))
    }

    override fun onGetMapAsync() {
        if (storeList.isNotEmpty())
            mainMapView.addMarkersToMap(storeList)

        if (locationGranted) {
            mainMapView.setMyLocationEnabled(true)
            requestCurrentLocation()
        }

        mainMapView.setupMapEventHandlers()

        subscribeMapCameraPositionChange()
        subscribeNewVisibleStore()
    }

    override fun onNavigationButtonClick(store: Store) {
        val storeLocation = store.getPosition()
        val queries = HashMap<String, String>()

        if (!isValidLocation(storeLocation)) {
            mainMapView.showInvalidStoreLocationWarning()
            return
        }

        if (!isValidLocation(currLocation)) {
            mainMapView.showCannotGetLocationMessage()
            return
        }

        val origin = getLatLngString(currLocation)
        val destination = getLatLngString(storeLocation)

        queries[GoogleMapsRoutingApiHelper.PARAM_ORIGIN] = origin
        queries[GoogleMapsRoutingApiHelper.PARAM_DESTINATION] = destination

        dataManager.getMapsDirection(queries, store)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<MapsDirection> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(mapsDirection: MapsDirection) {
                        if (mapsDirection.routeList.isNotEmpty())
                            mainMapView.showMapRoutingView(store, mapsDirection)
                        else
                            mainMapView.showGeneralErrorMessage()
                    }

                    override fun onError(e: Throwable) {
                        mainMapView.showGeneralErrorMessage()
                    }
                })
    }

    override fun onClearOldMapData() {
        markerSparseArray.clear()
        mainMapView.clearMapData()
    }

    override fun onMapMarkerAdd(storeId: Int, marker: Marker) {
        markerSparseArray.put(storeId, marker)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchResult(searchEventResult: SearchEventResult) {
        when (searchEventResult.resultCode) {
            SearchEventResult.SEARCH_ACTION_QUICK -> {
                handleSearchQuickAction(searchEventResult.storeType)
            }
            SearchEventResult.SEARCH_ACTION_QUERY_SUBMIT -> {
                if (searchEventResult.searchString.isBlank())
                    return

                handleSearchQuerySubmitAction(searchEventResult.searchString)
            }

            SearchEventResult.SEARCH_ACTION_COLLAPSE -> {
                handleSearchCollapseAction()
            }

            SearchEventResult.SEARCH_ACTION_STORE_CLICK -> {
                searchEventResult.store?.let {
                    handleSearchStoreClickAction(it)
                }
            }

            else -> mainMapView.showGeneralErrorMessage()
        }
    }

    private fun resetState() {
        locationGranted = false
        isZoomToUser = false
    }

    private fun subscribeLocationUpdate() {
        locationManager.requestLocationUpdates()
        locationManager.subscribeLocationUpdate(this)
    }

    private fun unsubscribeLocationUpdate() {
        locationManager.unsubscribeLocationUpdate(this)
    }

    private fun onCurrLocationChanged(latitude: Double, longitude: Double) {
        currLocation = LatLng(latitude, longitude).also {
            if (!isZoomToUser) {
                // Zoom and show current location in the Google Map
                // Only zoom-in the first time user location come
                mainMapView.animateMapCamera(it, false)

                isZoomToUser = true
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
            }
        }

        return stores
    }

    private fun generateNearByStoresWithDistance(currPos: LatLng, stores: List<Store>): List<NearByStore> {
        val res = ArrayList<NearByStore>()

        for (store in stores) {
            res.add(NearByStore(store, distanceBetween(currPos, store.getPosition())))
        }

        return res
    }

    @VisibleForTesting
    open fun subscribeMapCameraPositionChange() {
        cameraPositionPublisher.debounce(200, TimeUnit.MILLISECONDS, schedulerProvider.computation())
                .subscribeOn(schedulerProvider.computation())
                .map {
                    val stores = getVisibleStore(storeList, it.cameraBounds)
                    for (store in stores) {
                        if (!this.visibleStores.contains(store)) {
                            // New store become visible
                            onNewStoreBecomeVisible(store)
                        }
                    }
                    visibleStores = stores
                    generateNearByStoresWithDistance(it.cameraPosition, stores)
                }
                .observeOn(schedulerProvider.ui())
                .subscribe(object : Observer<List<NearByStore>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onNext(nearbyStores: List<NearByStore>) {
                        mainMapView.setNearByStores(nearbyStores)
                    }

                    override fun onError(e: Throwable) {
                        mainMapView.showGeneralErrorMessage()
                    }

                    override fun onComplete() {

                    }
                })
    }

    @VisibleForTesting
    open fun subscribeNewVisibleStore() {
        newVisibleStorePublisher.subscribeOn(schedulerProvider.io())
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
                .observeOn(schedulerProvider.ui())
                .subscribe(object : Observer<Pair<Marker?, Int>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onNext(pair: Pair<Marker?, Int>) {
                        val marker = pair.first
                        val storeType = pair.second

                        if (marker != null && storeType != null)
                            mainMapView.animateMapMarker(marker, storeType)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                    override fun onComplete() {
                    }
                })
    }

    @VisibleForTesting
    fun onNewStoreBecomeVisible(store: Store) {
        newVisibleStorePublisher.onNext(store)
    }

    private fun handleSearchQuickAction(storeType: Int) {
        dataManager.findStoresByType(storeType)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        if (storeList.isEmpty()) {
                            mainMapView.showWarningMessage(R.string.store_not_found)
                            return
                        }

                        this@MainMapPresenter.storeList = storeList.toMutableList()
                        mainMapView.addMarkersToMap(this@MainMapPresenter.storeList)
                        mainMapView.animateMapCamera(storeList[0].getPosition(), false)
                    }

                    override fun onError(e: Throwable) {
                        mainMapView.showWarningMessage(R.string.get_store_data_failed)
                    }
                })
    }

    private fun handleSearchQuerySubmitAction(searchString: String) {
        dataManager.findStores(searchString)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        if (storeList.isEmpty()) {
                            mainMapView.showWarningMessage(R.string.store_not_found)
                            return
                        }

                        this@MainMapPresenter.storeList = storeList.toMutableList()
                        mainMapView.addMarkersToMap(this@MainMapPresenter.storeList)
                        mainMapView.animateMapCamera(storeList[0].getPosition(), false)
                    }

                    override fun onError(e: Throwable) {
                        mainMapView.showWarningMessage(R.string.get_store_data_failed)
                    }
                })
    }

    private fun handleSearchCollapseAction() {
        loadAllStoresToMap()

        currLocation?.let {
            mainMapView.animateMapCamera(it, false)
        }
    }

    private fun loadAllStoresToMap() {
        dataManager.getAllStores()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {
                        compositeDisposable.add(d)
                    }

                    override fun onSuccess(storeList: List<Store>) {
                        this@MainMapPresenter.storeList = storeList.toMutableList()
                        if (storeList.isEmpty())
                            mainMapView.showWarningMessage(R.string.get_store_data_failed)
                        else
                            mainMapView.addMarkersToMap(this@MainMapPresenter.storeList)
                    }

                    override fun onError(e: Throwable) {
                        mainMapView.showWarningMessage(R.string.get_store_data_failed)
                    }
                })
    }

    private fun handleSearchStoreClickAction(store: Store) {
        storeList = arrayListOf(store)

        mainMapView.addMarkersToMap(storeList)
        mainMapView.animateMapCamera(store.getPosition(), false)

        mainMapView.clearNearByStores()

        mainMapView.showDialogStoreInfo(store)
    }
}