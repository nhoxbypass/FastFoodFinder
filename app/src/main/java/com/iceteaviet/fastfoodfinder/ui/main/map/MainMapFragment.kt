package com.iceteaviet.fastfoodfinder.ui.main.map

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.collection.SparseArrayCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.core.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.databinding.FragmentMainMapBinding
import com.iceteaviet.fastfoodfinder.ui.main.map.model.NearByStore
import com.iceteaviet.fastfoodfinder.ui.main.map.storeinfo.StoreInfoDialog
import com.iceteaviet.fastfoodfinder.utils.Constant
import com.iceteaviet.fastfoodfinder.utils.Constant.DEFAULT_ZOOM_LEVEL
import com.iceteaviet.fastfoodfinder.utils.REQUEST_LOCATION
import com.iceteaviet.fastfoodfinder.utils.isLocationPermissionGranted
import com.iceteaviet.fastfoodfinder.utils.openRoutingActivity
import com.iceteaviet.fastfoodfinder.utils.requestLocationPermission
import com.iceteaviet.fastfoodfinder.utils.ui.animateMarker
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreIcon
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainMapFragment : Fragment() {

    private val viewModel: MainMapViewModel by viewModels()

    private lateinit var binding: FragmentMainMapBinding

    lateinit var mNearStoreRecyclerView: RecyclerView
    lateinit var mBottomSheetContainer: LinearLayout

    private var googleMap: GoogleMap? = null
    private var mMapFragment: SupportMapFragment? = null
    private var nearByStoreAdapter: NearByStoreAdapter? = null

    private var markerSparseArray = SparseArrayCompat<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val googleLocationManager = GoogleLocationManager.getInstance()
        viewModel.injectLocationManager(googleLocationManager)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        mMapFragment = inflateSupportMapFragment()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.start(isLocationPermissionGranted())
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is MainMapEvent.Idle -> {}
                        is MainMapEvent.RequestLocationPermission -> {
                            requestLocationPermission()
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.SetMyLocationEnabled -> {
                            setMyLocationEnabled(state.enabled)
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.AnimateMapCamera -> {
                            animateMapCamera(state.location, state.zoomToDetail)
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.ShowWarningMessage -> {
                            showWarningMessage(state.stringId)
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.ShowGeneralErrorMessage -> {
                            showGeneralErrorMessage()
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.ShowCannotGetLocationMessage -> {
                            showCannotGetLocationMessage()
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.ShowInvalidStoreLocationWarning -> {
                            showInvalidStoreLocationWarning()
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.AddMarkersToMap -> {
                            addMarkersToMap(state.stores)
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.SetupMap -> {
                            setupMap()
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.SetupMapEventHandlers -> {
                            setupMapEventHandlers()
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.ShowMapRoutingView -> {
                            showMapRoutingView(state.store, state.mapsDirection)
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.ShowDialogStoreInfo -> {
                            showDialogStoreInfo(state.store)
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.AnimateMapMarker -> {
                            val targetMarker = markerSparseArray.get(state.storeId)
                            animateMapMarker(targetMarker, state.storeType)
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.SetNearByStores -> {
                            setNearByStores(state.stores)
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.ClearNearByStores -> {
                            clearNearByStores()
                            viewModel.markEventConsumed()
                        }
                        is MainMapEvent.ClearMapData -> {
                            clearMapData()
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java", ReplaceWith("super.onRequestPermissionsResult(requestCode, permissions, grantResults)"))
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewModel.onLocationPermissionGranted()
                } else {
                    Toast.makeText(requireContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestLocationPermission() {
        requestLocationPermission(this)
    }

    private fun isLocationPermissionGranted(): Boolean {
        return isLocationPermissionGranted(requireContext())
    }

    @SuppressLint("MissingPermission")
    private fun setMyLocationEnabled(enabled: Boolean) {
        googleMap?.isMyLocationEnabled = enabled
    }

    private fun animateMapCamera(location: LatLng, zoomToDetail: Boolean) {
        val zoomLevel = if (zoomToDetail) Constant.DETAILED_ZOOM_LEVEL else DEFAULT_ZOOM_LEVEL
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
    }

    private fun showWarningMessage(stringId: Int) {
        Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show()
    }

    private fun showGeneralErrorMessage() {
        Toast.makeText(context, R.string.error_general_error_code, Toast.LENGTH_LONG).show()
    }

    private fun showInvalidStoreLocationWarning() {
        Toast.makeText(context, R.string.error_invalid_store_location, Toast.LENGTH_LONG).show()
    }

    private fun showCannotGetLocationMessage() {
        Toast.makeText(context, R.string.cannot_get_curr_location, Toast.LENGTH_SHORT).show()
    }

    private fun addMarkersToMap(storeList: List<Store>) {
        if (googleMap == null) return

        viewModel.onClearOldMapData()

        viewLifecycleOwner.lifecycleScope.launch(kotlinx.coroutines.Dispatchers.Main) {
            val bounds = googleMap?.projection?.visibleRegion?.latLngBounds
            for (i in storeList.indices) {
                val store = storeList[i]
                if (bounds != null && !bounds.contains(store.getPosition())) continue

                val marker = googleMap!!.addMarker(
                    MarkerOptions().position(store.getPosition())
                        .title(store.title)
                        .snippet(store.address)
                        .icon(getStoreIcon(resources, store.type, -1, -1))
                )
                if (marker != null) {
                    marker.tag = store
                    markerSparseArray.put(store.id, marker)
                }
                
                // Prevent `addMarker` IPC bottlenecking on massive datasets by yielding the main thread
                if (i > 0 && i % 20 == 0) {
                    kotlinx.coroutines.yield()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupMap() {
        if (googleMap != null) return

        mMapFragment?.getMapAsync { googleMap ->
            this.googleMap = googleMap
            googleMap.isBuildingsEnabled = true

            googleMap.setOnCameraMoveListener {
                viewModel.onMapCameraMove(
                    googleMap.cameraPosition.target,
                    googleMap.projection.visibleRegion.latLngBounds
                )
            }

            viewModel.onGetMapAsync()
        }
    }

    private fun setupMapEventHandlers() {
        setMarkersListener(googleMap)
    }

    private fun showMapRoutingView(currStore: Store, mapsDirection: MapsDirection) {
        openRoutingActivity(requireActivity(), currStore, mapsDirection)
    }

    private fun animateMapMarker(marker: Marker?, storeType: Int) {
        animateMarker(resources, marker, storeType)
    }

    private fun setNearByStores(nearbyStores: List<NearByStore>) {
        nearByStoreAdapter?.setStores(nearbyStores)
    }

    private fun clearNearByStores() {
        nearByStoreAdapter?.clearData()
    }

    private fun clearMapData() {
        markerSparseArray.clear()
        googleMap?.clear()
    }

    private fun inflateSupportMapFragment(): SupportMapFragment? {
        val fragmentManager = childFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.maps_container)

        if (fragment == null) {
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
            val mapFragment = SupportMapFragment.newInstance(options)
            fragmentManager.beginTransaction().replace(R.id.map_placeholder, mapFragment).commitAllowingStateLoss()
            return mapFragment
        } else {
            return fragment as SupportMapFragment
        }
    }

    private fun setupUI() {
        mNearStoreRecyclerView = binding.rvBottomSheet
        mBottomSheetContainer = binding.llBottomSheet

        nearByStoreAdapter = NearByStoreAdapter()
        initBottomSheet()
    }

    private fun initBottomSheet() {
        BottomSheetBehavior.from(mBottomSheetContainer)
        mNearStoreRecyclerView.adapter = nearByStoreAdapter
        mNearStoreRecyclerView.layoutManager = LinearLayoutManager(context)

        nearByStoreAdapter?.setOnStoreListListener(object : NearByStoreAdapter.StoreListListener {
            override fun onItemClick(store: Store) {
                viewModel.onNavigationButtonClick(store)
            }
        })
    }

    private fun setMarkersListener(googleMap: GoogleMap?) {
        googleMap?.setOnMarkerClickListener { marker ->
            val store = marker.tag as Store?
            if (store != null) {
                showDialogStoreInfo(store)
            }
            false
        }
    }

    private fun showDialogStoreInfo(store: Store) {
        val dialog = StoreInfoDialog.newInstance(store)
        dialog.setDialogListen(object : StoreInfoDialog.StoreDialogActionListener {
            override fun onDirection(store: Store?) {
                if (store != null) {
                    viewModel.onNavigationButtonClick(store)
                }
            }
            override fun onAddToFavorite(storeId: Int) {
                Toast.makeText(activity, R.string.fav_stores_added, Toast.LENGTH_SHORT).show()
            }
        })
        dialog.show(requireActivity().supportFragmentManager, "dialog-info")
    }

    companion object {
        fun newInstance(): MainMapFragment {
            val args = Bundle()
            val fragment = MainMapFragment()
            fragment.arguments = args
            return fragment
        }
    }
}