package com.iceteaviet.fastfoodfinder.ui.routing

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.databinding.ActivityMapRoutingBinding
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.custom.snaphelper.OnSnapListener
import com.iceteaviet.fastfoodfinder.ui.custom.snaphelper.OnSnapPositionChangeListener
import com.iceteaviet.fastfoodfinder.utils.Constant.DEFAULT_ZOOM_LEVEL
import com.iceteaviet.fastfoodfinder.utils.Constant.DETAILED_ZOOM_LEVEL
import com.iceteaviet.fastfoodfinder.utils.convertDpToPx
import com.iceteaviet.fastfoodfinder.utils.extension.attachSnapHelperToListener
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableRes
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapRoutingActivity : BaseActivity(), View.OnClickListener {

    private val viewModel: MapRoutingViewModel by viewModels()

    private lateinit var binding: ActivityMapRoutingBinding

    private lateinit var txtTravelTime: TextView
    private lateinit var txtTravelDistance: TextView
    private lateinit var txtTravelOverview: TextView
    private lateinit var bottomRecyclerView: RecyclerView
    private lateinit var topRecyclerView: RecyclerView
    private lateinit var bottomSheetContainer: LinearLayout
    private lateinit var prevInstruction: ImageButton
    private lateinit var nextInstruction: ImageButton
    private lateinit var routingButtonContainer: LinearLayout

    private val snapHelper = LinearSnapHelper()
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var googleMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private var currDirection: Polyline? = null
    private lateinit var bottomRoutingAdapter: RoutingAdapter
    private lateinit var topRoutingAdapter: RoutingAdapter

    override val layoutId: Int
        get() = R.layout.activity_map_routing

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapRoutingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setUpMapIfNeeded()
        setupEventListeners()

        intent.extras?.let {
            viewModel.handleExtras(
                it.getParcelable<MapsDirection>(KEY_ROUTE_LIST), 
                it.getParcelable<Store>(KEY_DES_STORE)
            )
        }
        
        setupObservers()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    supportActionBar?.title = state.title
                    
                    bottomRoutingAdapter.setStepList(state.stepList)
                    topRoutingAdapter.setStepList(state.stepList)

                    if (state.inPreviewMode) {
                        enterPreviewMode()
                    } else {
                        exitPreviewMode()
                    }

                    txtTravelTime.text = state.durationText
                    txtTravelDistance.text = state.distanceText
                    txtTravelOverview.text = state.summaryText

                    when (val event = state.event) {
                        is MapRoutingEvent.Idle -> {}
                        is MapRoutingEvent.Exit -> {
                            finish()
                            viewModel.markEventConsumed()
                        }
                        is MapRoutingEvent.ShowGetDirectionFailedMessage -> {
                            Toast.makeText(this@MapRoutingActivity, R.string.get_map_direction_failed, Toast.LENGTH_SHORT).show()
                            finish() // Exit automatically on fail
                            viewModel.markEventConsumed()
                        }
                        is MapRoutingEvent.ShowGeneralErrorMessage -> {
                            Toast.makeText(this@MapRoutingActivity, R.string.error_general_error_code, Toast.LENGTH_LONG).show()
                            viewModel.markEventConsumed()
                        }
                        is MapRoutingEvent.AnimateMapCamera -> {
                            animateMapCamera(event.location, event.zoomToDetail)
                            viewModel.markEventConsumed()
                        }
                        is MapRoutingEvent.AddMapMarker -> {
                            addMapMarker(event.location, event.title, event.description, event.icon)
                            viewModel.markEventConsumed()
                        }
                        is MapRoutingEvent.DrawRoutingPath -> {
                            drawRoutingPath(event.currLocation, event.routingGeoPoint)
                            viewModel.markEventConsumed()
                        }
                        is MapRoutingEvent.ScrollTopBannerToPosition -> {
                            scrollTopBannerToPosition(event.index)
                            // manually animate map here to fix conflation limits:
                            if(state.stepList.isNotEmpty()) {
                                animateMapCamera(state.stepList[event.index].endMapCoordination.location, true)
                            }
                            viewModel.markEventConsumed()
                        }
                    }
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_prev_instruction -> viewModel.onPrevInstructionClick()
            R.id.btn_next_instruction -> viewModel.onNextInstructionClick()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            viewModel.onBackArrowButtonPress()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun enterPreviewMode() {
        routingButtonContainer.visibility = View.VISIBLE
        topRecyclerView.visibility = View.VISIBLE
        bottomSheetBehavior?.isHideable = true
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun exitPreviewMode() {
        routingButtonContainer.visibility = View.GONE
        topRecyclerView.visibility = View.GONE
        bottomSheetBehavior?.isHideable = false
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun scrollTopBannerToPosition(directionIndex: Int) {
        topRecyclerView.smoothScrollToPosition(directionIndex)
    }

    private fun animateMapCamera(location: LatLng, zoomToDetail: Boolean) {
        val zoomLevel = if (zoomToDetail) DETAILED_ZOOM_LEVEL else DEFAULT_ZOOM_LEVEL
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
    }

    private fun addMapMarker(location: LatLng, title: String, description: String, icon: Int) {
        googleMap?.addMarker(MarkerOptions().position(location)
            .title(title)
            .snippet(description)
            .icon(BitmapDescriptorFactory.fromResource(icon)))
    }

    private fun drawRoutingPath(currLocation: LatLng?, routingGeoPoint: List<LatLng>) {
        googleMap?.let {
            val builder = LatLngBounds.Builder()
            if (currLocation != null) builder.include(currLocation)

            val options = PolylineOptions()
                .clickable(true)
                .color(ContextCompat.getColor(this, R.color.googleBlue))
                .width(12f)
                .geodesic(true)
                .zIndex(5f)

            for (i in routingGeoPoint.indices) {
                val geoPoint = routingGeoPoint[i]
                options.add(geoPoint)
                builder.include(geoPoint)
            }

            currDirection?.remove()
            currDirection = it.addPolyline(options)

            val bounds = builder.build()
            val padding = 48
            zoomToShowAllMarker(bounds, it, padding)
        }
    }

    private fun setupUI() {
        findViews()
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        bottomRoutingAdapter = RoutingAdapter(RoutingAdapter.TYPE_FULL)
        val layoutManager = LinearLayoutManager(this)
        bottomRecyclerView.layoutManager = layoutManager
        bottomRecyclerView.adapter = bottomRoutingAdapter

        topRoutingAdapter = RoutingAdapter(RoutingAdapter.TYPE_SHORT)
        val topLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        topRecyclerView.layoutManager = topLayoutManager
        topRecyclerView.adapter = topRoutingAdapter
        snapHelper.attachToRecyclerView(topRecyclerView)
    }

    private fun findViews() {
        txtTravelTime = binding.tvRoutingTime
        txtTravelDistance = binding.tvRoutingDistance
        txtTravelOverview = binding.tvRoutingOverview
        bottomRecyclerView = binding.rvBottomSheet
        topRecyclerView = binding.rvDirectionInstruction
        bottomSheetContainer = binding.llBottomSheet
        prevInstruction = binding.btnPrevInstruction
        nextInstruction = binding.btnNextInstruction
        routingButtonContainer = binding.llRoutingButtonContainer
    }

    private fun setupEventListeners() {
        prevInstruction.setOnClickListener(this)
        nextInstruction.setOnClickListener(this)

        val listener = object : RoutingAdapter.OnNavigationRowClickListener {
            override fun onClick(index: Int) {
                viewModel.onNavigationRowClick(index)
            }
        }
        bottomRoutingAdapter.setOnNavigationItemClickListener(listener)
        topRoutingAdapter.setOnNavigationItemClickListener(listener)

        topRecyclerView.attachSnapHelperToListener(snapHelper, object : OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                viewModel.onTopRoutingBannerPositionChange(position)
            }
        }, OnSnapListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE_BY_DRAGGING)
    }

    private fun setUpMapIfNeeded() {
        if (mapFragment === null) {
            mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

            mapFragment?.getMapAsync { map ->
                googleMap = map
                viewModel.onGetMapAsync()
                
                // Directly pull map init data here to render map correctly bypassing event limits
                val mapData = viewModel.getInitMapData()
                if (mapData != null) {
                    addMapMarker(mapData.store.getPosition(), mapData.store.title, mapData.store.address, getStoreLogoDrawableRes(mapData.store.type))
                    mapData.currLocation?.let { loc ->
                        animateMapCamera(loc, false)
                        addMapMarker(loc, "Your location", "Your current location, please follow the line", R.drawable.ic_map_bluedot)
                    }
                    drawRoutingPath(mapData.currLocation, mapData.geoPointList)
                }
            }
        }
    }

    private fun zoomToShowAllMarker(bounds: LatLngBounds, googleMap: GoogleMap, padding: Int) {
        val displayMetrics = resources.displayMetrics

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels - convertDpToPx(160f)
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height.toInt(), padding)

        googleMap.animateCamera(cu)
    }

    companion object {
        const val KEY_ROUTE_LIST = "route_list"
        const val KEY_DES_STORE = "des_store"
    }
}