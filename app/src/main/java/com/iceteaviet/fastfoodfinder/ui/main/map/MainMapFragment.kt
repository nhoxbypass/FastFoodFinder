package com.iceteaviet.fastfoodfinder.ui.main.map

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.core.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.iceteaviet.fastfoodfinder.databinding.FragmentMainMapBinding
import com.iceteaviet.fastfoodfinder.ui.main.map.cluster.StoreClusterItem
import com.iceteaviet.fastfoodfinder.ui.main.map.cluster.StoreClusterRenderer
import com.iceteaviet.fastfoodfinder.ui.main.map.model.NearByStore
import com.iceteaviet.fastfoodfinder.ui.main.map.storeinfo.StoreInfoDialog
import com.iceteaviet.fastfoodfinder.utils.Constant
import com.iceteaviet.fastfoodfinder.utils.Constant.DEFAULT_ZOOM_LEVEL
import com.iceteaviet.fastfoodfinder.utils.REQUEST_LOCATION
import com.iceteaviet.fastfoodfinder.utils.isLocationPermissionGranted
import com.iceteaviet.fastfoodfinder.utils.openRoutingActivity
import com.iceteaviet.fastfoodfinder.utils.requestLocationPermission
import kotlinx.coroutines.flow.collect
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
    private var clusterManager: ClusterManager<StoreClusterItem>? = null
    private var mapRevealed = false

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
        setupUI(savedInstanceState)
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
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is MainMapEvent.RequestLocationPermission -> {
                            requestLocationPermission()
                        }
                        is MainMapEvent.SetMyLocationEnabled -> {
                            setMyLocationEnabled(event.enabled)
                        }
                        is MainMapEvent.AnimateMapCamera -> {
                            animateMapCamera(event.location, event.zoomToDetail)
                        }
                        is MainMapEvent.ShowWarningMessage -> {
                            showWarningMessage(event.stringId)
                        }
                        is MainMapEvent.ShowGeneralErrorMessage -> {
                            showGeneralErrorMessage()
                        }
                        is MainMapEvent.ShowCannotGetLocationMessage -> {
                            showCannotGetLocationMessage()
                        }
                        is MainMapEvent.ShowInvalidStoreLocationWarning -> {
                            showInvalidStoreLocationWarning()
                        }
                        is MainMapEvent.AddStoresToCluster -> {
                            addStoresToCluster(event.stores)
                        }
                        is MainMapEvent.SetupMap -> {
                            setupMap()
                        }
                        is MainMapEvent.ShowMapRoutingView -> {
                            showMapRoutingView(event.store, event.mapsDirection)
                        }
                        is MainMapEvent.ShowDialogStoreInfo -> {
                            showDialogStoreInfo(event.store)
                        }
                        is MainMapEvent.SetNearByStores -> {
                            setNearByStores(event.stores)
                        }
                        is MainMapEvent.ClearNearByStores -> {
                            clearNearByStores()
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
        if (!mapRevealed) {
            binding.mapPlaceholder.visibility = View.VISIBLE
            mapRevealed = true
        }
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

    private fun addStoresToCluster(stores: List<Store>) {
        val cm = clusterManager ?: return
        cm.clearItems()
        cm.addItems(stores.map { StoreClusterItem(it) })
        cm.cluster()
    }

    @SuppressLint("MissingPermission")
    private fun setupMap() {
        if (googleMap != null) return

        mMapFragment?.getMapAsync { map ->
            this.googleMap = map
            map.isBuildingsEnabled = true

            val cm = ClusterManager<StoreClusterItem>(requireContext(), map)
            cm.renderer = StoreClusterRenderer(requireContext(), map, cm)
            this.clusterManager = cm

            map.setOnCameraIdleListener(cm)
            map.setOnMarkerClickListener(cm)

            cm.setOnClusterItemClickListener { item ->
                showDialogStoreInfo(item.getStore())
                true
            }

            map.setOnCameraMoveListener {
                viewModel.onMapCameraMove(
                    map.cameraPosition.target,
                    map.projection.visibleRegion.latLngBounds
                )
            }

            map.setOnCameraMoveStartedListener {
                viewModel.onCameraMoveStarted()
            }

            viewModel.onGetMapAsync()
        }
    }

    private fun showMapRoutingView(currStore: Store, mapsDirection: MapsDirection) {
        openRoutingActivity(requireActivity(), currStore, mapsDirection)
    }

    private fun setNearByStores(nearbyStores: List<NearByStore>) {
        nearByStoreAdapter?.setStores(nearbyStores)
    }

    private fun clearNearByStores() {
        nearByStoreAdapter?.clearData()
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

    private fun setupUI(savedInstanceState: Bundle?) {
        mNearStoreRecyclerView = binding.rvBottomSheet
        mBottomSheetContainer = binding.llBottomSheet

        if (savedInstanceState == null) {
            binding.mapPlaceholder.visibility = View.INVISIBLE
        } else {
            mapRevealed = true
        }

        nearByStoreAdapter = NearByStoreAdapter()
        initBottomSheet()
    }

    private fun initBottomSheet() {
        BottomSheetBehavior.from(mBottomSheetContainer)
        mNearStoreRecyclerView.adapter = nearByStoreAdapter
        mNearStoreRecyclerView.layoutManager = LinearLayoutManager(context)

        nearByStoreAdapter?.setOnStoreListListener(object : NearByStoreAdapter.StoreListListener {
            override fun onItemClick(store: Store) {
                viewModel.onNearByStoreClicked(store)
            }
        })
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
