package com.iceteaviet.fastfoodfinder.ui.main.map


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.TextUtils
import android.util.Pair
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.perf.metrics.AddTrace
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper.Companion.PARAM_DESTINATION
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper.Companion.PARAM_ORIGIN
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.data.transport.model.SearchEventResult
import com.iceteaviet.fastfoodfinder.ui.routing.MapRoutingActivity
import com.iceteaviet.fastfoodfinder.ui.routing.MapRoutingActivity.Companion.KEY_DES_STORE
import com.iceteaviet.fastfoodfinder.ui.routing.MapRoutingActivity.Companion.KEY_ROUTE_LIST
import com.iceteaviet.fastfoodfinder.ui.store.StoreInfoDialog
import com.iceteaviet.fastfoodfinder.utils.*
import com.iceteaviet.fastfoodfinder.utils.Constant.DEFAULT_ZOOM_LEVEL
import com.iceteaviet.fastfoodfinder.utils.ui.animateMarker
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreIcon
import io.reactivex.Observer
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_main_map.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class MainMapFragment : Fragment(), GoogleApiClient.ConnectionCallbacks, LocationListener {

    lateinit var mNearStoreRecyclerView: RecyclerView
    lateinit var mBottomSheetContainer: LinearLayout

    private var mLocationRequest: LocationRequest? = null
    private var currLocation: LatLng? = null
    private var mStoreList: MutableList<Store>? = null
    private var visibleStores: List<Store>? = null
    private var markerSparseArray: SparseArray<Marker>? = null // pair storeId - marker
    private var mGoogleMap: GoogleMap? = null
    private var mMapFragment: SupportMapFragment? = null
    private var mAdapter: NearByStoreListAdapter? = null
    private var googleApiClient: GoogleApiClient? = null
    private var isZoomToUser = false

    private var newVisibleStorePublisher: PublishSubject<Store>? = null
    private var cameraPositionPublisher: PublishSubject<CameraPosition>? = null
    private lateinit var dataManager: DataManager

    private val lastLocation: LatLng
        @SuppressLint("MissingPermission")
        get() {
            val lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient) // 32ms
            if (lastLocation != null) {
                return LatLng(lastLocation.latitude, lastLocation.longitude)
            } else {
                Toast.makeText(activity, R.string.cannot_get_curr_location, Toast.LENGTH_SHORT).show()
                return mGoogleMap!!.cameraPosition.target
            }
        }


    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mMapFragment = inflateSupportMapFragment()
    }

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeVariables()
        initStoreData()
    }

    @Nullable
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_map, container, false)
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mNearStoreRecyclerView = rv_bottom_sheet
        mBottomSheetContainer = ll_bottom_sheet

        initBottomSheet()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this)
        if (mGoogleMap == null) {
            initializeMapData()
        }
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        newVisibleStorePublisher!!.onComplete()
        super.onDestroy()
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mGoogleMap!!.isMyLocationEnabled = true
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this)
                    currLocation = lastLocation
                } else {
                    Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(@Nullable bundle: Bundle?) {
        if (mStoreList == null || mStoreList!!.isEmpty()) {
            Toast.makeText(context, R.string.store_not_found, Toast.LENGTH_SHORT).show()
            return
        }

        if (mGoogleMap == null)
            return

        addMarkersToMap(mStoreList, mGoogleMap) // 1.9s
        setMarkersListener(mGoogleMap)

        if (isLocationPermissionGranted(context!!)) {
            mGoogleMap!!.isMyLocationEnabled = true
            currLocation = lastLocation // 32ms

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this) // 13 ms

            // Showing the current location in Google Map
            mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation, DEFAULT_ZOOM_LEVEL))
        } else {
            requestLocationPermission(this) // sau
        }
    }

    override fun onConnectionSuspended(i: Int) {
        Toast.makeText(activity, R.string.cannot_connect_location_service, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        googleApiClient!!.connect()
    }


    override fun onStop() {
        googleApiClient!!.disconnect()
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSearchResult(searchEventResult: SearchEventResult) {
        val resultCode = searchEventResult.resultCode
        when (resultCode) {
            SearchEventResult.SEARCH_ACTION_QUICK -> {
                dataManager.getLocalStoreDataSource()
                        .findStoresByType(searchEventResult.storeType)
                        .subscribe(object : SingleObserver<List<Store>> {
                            override fun onSubscribe(d: Disposable) {

                            }

                            override fun onSuccess(storeList: List<Store>) {
                                if (storeList.isEmpty() && isAdded) {
                                    Toast.makeText(context, R.string.store_not_found, Toast.LENGTH_SHORT).show()
                                    return
                                }

                                mStoreList = storeList.toMutableList()

                                addMarkersToMap(mStoreList, mGoogleMap)
                                mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(mStoreList!![0].getPosition(), DEFAULT_ZOOM_LEVEL))
                                mAdapter!!.setCurrCameraPosition(mGoogleMap!!.cameraPosition.target)
                                visibleStores = getVisibleStore(mStoreList!!, mGoogleMap!!.projection.visibleRegion.latLngBounds)
                                visibleStores?.let { mAdapter!!.setStores(it) }
                            }

                            override fun onError(e: Throwable) {
                                if (isAdded)
                                    Toast.makeText(context, R.string.get_store_data_failed, Toast.LENGTH_SHORT).show()
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

                            }

                            override fun onSuccess(storeList: List<Store>) {
                                if (storeList.isEmpty() && isAdded) {
                                    Toast.makeText(context, R.string.store_not_found, Toast.LENGTH_SHORT).show()
                                    return
                                }

                                mStoreList = storeList.toMutableList()

                                addMarkersToMap(mStoreList, mGoogleMap)
                                mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(mStoreList!![0].getPosition(), DEFAULT_ZOOM_LEVEL))
                                mAdapter!!.setCurrCameraPosition(mGoogleMap!!.cameraPosition.target)
                                visibleStores = getVisibleStore(mStoreList!!, mGoogleMap!!.projection.visibleRegion.latLngBounds)
                                visibleStores?.let { mAdapter!!.setStores(it) }
                            }

                            override fun onError(e: Throwable) {
                                if (isAdded)
                                    Toast.makeText(context, R.string.get_store_data_failed, Toast.LENGTH_SHORT).show()
                            }
                        })
            }

            SearchEventResult.SEARCH_ACTION_COLLAPSE -> {
                dataManager.getLocalStoreDataSource().getAllStores()
                        .subscribe(object : SingleObserver<List<Store>> {
                            override fun onSubscribe(d: Disposable) {

                            }

                            override fun onSuccess(storeList: List<Store>) {
                                if (storeList.isEmpty() && isAdded) {
                                    Toast.makeText(context, R.string.get_store_data_failed, Toast.LENGTH_SHORT).show()
                                    return
                                }

                                mStoreList = storeList.toMutableList()
                                addMarkersToMap(mStoreList, mGoogleMap)
                            }

                            override fun onError(e: Throwable) {
                                if (isAdded)
                                    Toast.makeText(context, R.string.get_store_data_failed, Toast.LENGTH_SHORT).show()
                            }
                        })

                if (currLocation != null)
                    mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation, DEFAULT_ZOOM_LEVEL))
            }

            SearchEventResult.SEARCH_ACTION_STORE_CLICK -> {
                if (searchEventResult.store != null) {
                    mStoreList = ArrayList()
                    mStoreList!!.add(searchEventResult.store!!)

                    addMarkersToMap(mStoreList, mGoogleMap)
                    mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(searchEventResult.store!!.getPosition(), DEFAULT_ZOOM_LEVEL))
                    mAdapter!!.clearData()

                    showDialogStoreInfo(searchEventResult.store!!)
                }
            }

            else -> Toast.makeText(context, R.string.search_error, Toast.LENGTH_SHORT).show()
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

    fun inflateSupportMapFragment(): SupportMapFragment? {
        val fragmentManager = childFragmentManager
        var fragment = fragmentManager.findFragmentById(R.id.maps_container)
        var mapFragment: SupportMapFragment?

        if (fragment === null) {
            val cameraPosition = CameraPosition.builder()
                    .target(Constant.DEFAULT_MAP_TARGET)
                    .zoom(16f)
                    .build()
            val options = GoogleMapOptions()
            options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                    .camera(cameraPosition)
                    .compassEnabled(true)
                    .rotateGesturesEnabled(true)
                    .zoomGesturesEnabled(true)
                    .tiltGesturesEnabled(true)
            mapFragment = SupportMapFragment.newInstance(options)
            fragmentManager.beginTransaction().replace(R.id.map_placeholder, mapFragment as Fragment).commit() // TODO: Check
            fragmentManager.executePendingTransactions()
        } else {
            mapFragment = fragment as SupportMapFragment
        }

        return mapFragment
    }


    private fun initializeVariables() {
        mStoreList = ArrayList()
        visibleStores = ArrayList()
        markerSparseArray = SparseArray()

        mAdapter = NearByStoreListAdapter()
        dataManager = App.getDataManager()
        newVisibleStorePublisher = PublishSubject.create()
        cameraPositionPublisher = PublishSubject.create()

        googleApiClient = GoogleApiClient.Builder(context!!)
                .addConnectionCallbacks(this@MainMapFragment)
                .addOnConnectionFailedListener { e(TAG, getString(R.string.cannot_get_curr_location)) }
                .addApi(LocationServices.API)
                .build()

        mLocationRequest = createLocationRequest()
    }

    private fun initStoreData() {
        dataManager.getLocalStoreDataSource().getAllStores()
                .subscribe(object : SingleObserver<List<Store>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(storeList: List<Store>) {
                        mStoreList = storeList.toMutableList()
                        if ((mStoreList == null || mStoreList!!.size <= 0) && isAdded)
                            Toast.makeText(context, R.string.get_store_data_failed, Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(e: Throwable) {
                        if (isAdded)
                            Toast.makeText(context, R.string.get_store_data_failed, Toast.LENGTH_SHORT).show()
                    }
                })
    }


    private fun initBottomSheet() {
        BottomSheetBehavior.from(mBottomSheetContainer)
        mNearStoreRecyclerView.adapter = mAdapter
        mNearStoreRecyclerView.layoutManager = LinearLayoutManager(context)

        mAdapter!!.setOnStoreListListener(object : NearByStoreListAdapter.StoreListListener {
            override fun onItemClick(store: Store) {
                getDirection(store)
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun initializeMapData() {
        mMapFragment!!.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            mGoogleMap!!.isBuildingsEnabled = true

            //Animate marker icons when camera move
            mGoogleMap!!.setOnCameraMoveListener {
                if (cameraPositionPublisher != null)
                    cameraPositionPublisher!!.onNext(mGoogleMap!!.cameraPosition)
            }

            cameraPositionPublisher!!
                    .debounce(200, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<CameraPosition> {
                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(cameraPosition: CameraPosition) {
                            mAdapter!!.setCurrCameraPosition(cameraPosition.target)
                            visibleStores = getVisibleStore(mStoreList!!, mGoogleMap!!.projection.visibleRegion.latLngBounds)
                            visibleStores?.let { mAdapter!!.setStores(it) }
                        }

                        override fun onError(e: Throwable) {

                        }

                        override fun onComplete() {

                        }
                    })

            newVisibleStorePublisher!!
                    .observeOn(Schedulers.computation())
                    .map { store ->
                        val marker = markerSparseArray!!.get(store.id)

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
                        }

                        override fun onNext(pair: Pair<Marker, Int>) {
                            animateMarker(resources, pair.first, pair.second)
                        }

                        override fun onError(e: Throwable) {
                            e.printStackTrace()
                        }

                        override fun onComplete() {
                        }
                    })
        }
    }


    private fun addMarkersToMap(storeList: List<Store>?, googleMap: GoogleMap?) {
        if (googleMap == null || storeList == null)
            return

        googleMap.clear()

        // Set icons of the store marker to green
        for (i in storeList.indices) {
            val store = storeList[i]
            val marker = googleMap.addMarker(MarkerOptions().position(store.getPosition()) // addMarker 30ms
                    .title(store.title)
                    .snippet(store.address)
                    .icon(getStoreIcon(resources, store.type, -1, -1))) // fromBitmap 25 -> 100ms
            marker.tag = store
            markerSparseArray!!.put(store.id, marker)
        }
    }

    private fun getDirection(store: Store) {
        val storeLocation = store.getPosition()
        val queries = HashMap<String, String>()

        val origin = getLatLngString(currLocation)
        val destination = getLatLngString(storeLocation)

        if (TextUtils.isEmpty(origin) || TextUtils.isEmpty(destination))
            return

        queries[PARAM_ORIGIN] = origin
        queries[PARAM_DESTINATION] = destination

        dataManager.getMapsRoutingApiHelper().getMapsDirection(queries, store)
                .subscribe(object : SingleObserver<MapsDirection> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onSuccess(mapsDirection: MapsDirection) {
                        val intent = Intent(activity, MapRoutingActivity::class.java)
                        val extras = Bundle()
                        extras.putParcelable(KEY_ROUTE_LIST, mapsDirection)
                        extras.putParcelable(KEY_DES_STORE, store)
                        intent.putExtras(extras)
                        startActivity(intent)
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }
                })
    }


    private fun setMarkersListener(googleMap: GoogleMap?) {
        googleMap?.setOnMarkerClickListener { marker ->
            // Handle store marker click click here
            val store = marker.tag as Store?

            if (store != null)
                showDialogStoreInfo(store)

            false
        }
    }


    private fun showDialogStoreInfo(store: Store) {
        val dialog = StoreInfoDialog.newInstance(store)
        dialog.setDialogListen(object : StoreInfoDialog.StoreDialogActionListener {
            override fun onDirection(store: Store?) {
                getDirection(store!!)
            }

            override fun onAddToFavorite(storeId: Int) {
                //TODO lưu vào danh sách yêu thích
                Toast.makeText(activity, R.string.fav_stores_added, Toast.LENGTH_SHORT).show()
            }
        })
        dialog.show(activity?.supportFragmentManager!!, "dialog-info")
    }

    override fun onLocationChanged(location: Location) {
        currLocation = LatLng(location.latitude, location.longitude)
        // Creating a LatLng object for the current location

        if (!isZoomToUser) {
            // Zoom and show current location in the Google Map
            mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(currLocation, Constant.DEFAULT_ZOOM_LEVEL))

            isZoomToUser = true
        }
    }

    companion object {
        private val TAG = MainMapFragment::class.java.simpleName


        fun newInstance(): MainMapFragment {

            val args = Bundle()

            val fragment = MainMapFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
