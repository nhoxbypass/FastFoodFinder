package com.iceteaviet.fastfoodfinder.ui.routing

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.iceteaviet.fastfoodfinder.App
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.Step
import com.iceteaviet.fastfoodfinder.databinding.ActivityMapRoutingBinding
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.ui.custom.snaphelper.OnSnapListener
import com.iceteaviet.fastfoodfinder.ui.custom.snaphelper.OnSnapPositionChangeListener
import com.iceteaviet.fastfoodfinder.utils.Constant.DEFAULT_ZOOM_LEVEL
import com.iceteaviet.fastfoodfinder.utils.Constant.DETAILED_ZOOM_LEVEL
import com.iceteaviet.fastfoodfinder.utils.convertDpToPx
import com.iceteaviet.fastfoodfinder.utils.extension.attachSnapHelperToListener


class MapRoutingActivity : BaseActivity(), MapRoutingContract.View, View.OnClickListener {
    override lateinit var presenter: MapRoutingContract.Presenter

    /**
     * Views Ref
     */
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

    private val snapHelper = LinearSnapHelper() // The item is fullscreen, may be using PagerSnapHelper is better?
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

        presenter = MapRoutingPresenter(App.getDataManager(), App.getSchedulerProvider(), this)

        binding = ActivityMapRoutingBinding.inflate(layoutInflater)
        setupUI()
        setUpMapIfNeeded()
        setupEventListeners()

        intent.extras?.let {
            presenter.handleExtras(it.getParcelable(KEY_ROUTE_LIST), it.getParcelable(KEY_DES_STORE))
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.subscribe()
    }

    override fun onPause() {
        super.onPause()
        presenter.unsubscribe()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_prev_instruction -> {
                presenter.onPrevInstructionClick()
            }

            R.id.btn_next_instruction -> {
                presenter.onNextInstructionClick()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            presenter.onBackArrowButtonPress()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun setStoreTitle(title: String) {
        supportActionBar!!.title = title
    }

    override fun setRoutingStepList(stepList: List<Step>) {
        bottomRoutingAdapter.setStepList(stepList)
        topRoutingAdapter.setStepList(stepList)
    }

    override fun enterPreviewMode() {
        routingButtonContainer.visibility = View.VISIBLE
        topRecyclerView.visibility = View.VISIBLE
        bottomSheetBehavior?.isHideable = true
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun exitPreviewMode() {
        routingButtonContainer.visibility = View.GONE
        topRecyclerView.visibility = View.GONE
        bottomSheetBehavior?.isHideable = false
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun scrollTopBannerToPosition(directionIndex: Int) {
        topRecyclerView.smoothScrollToPosition(directionIndex)
    }

    override fun exit() {
        finish()
    }

    override fun showGetDirectionFailedMessage() {
        Toast.makeText(this, R.string.get_map_direction_failed, Toast.LENGTH_SHORT).show()
    }

    override fun showGeneralErrorMessage() {
        Toast.makeText(this, R.string.error_general_error_code, Toast.LENGTH_LONG).show()
    }

    override fun animateMapCamera(location: LatLng, zoomToDetail: Boolean) {
        val zoomLevel = if (zoomToDetail) DETAILED_ZOOM_LEVEL else DEFAULT_ZOOM_LEVEL
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
    }

    override fun addMapMarker(location: LatLng, title: String, description: String, icon: Int) {
        googleMap?.addMarker(MarkerOptions().position(location)
            .title(title)
            .snippet(description)
            .icon(BitmapDescriptorFactory.fromResource(icon)))
    }

    override fun drawRoutingPath(currLocation: LatLng?, routingGeoPoint: List<LatLng>) {
        googleMap?.let {
            //Add position to viewBounds
            val builder = LatLngBounds.Builder()
            if (currLocation != null)
                builder.include(currLocation)

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

            // Build the viewbounds contain all markers
            val bounds = builder.build()
            val padding = 48 // offset from edges of the map in pixels
            zoomToShowAllMarker(bounds, it, padding)
        }
    }

    override fun setTravelDurationText(duration: String) {
        txtTravelTime.text = duration
    }

    override fun setTravelDistanceText(distance: String) {
        txtTravelDistance.text = distance
    }

    override fun setTravelSummaryText(summary: String) {
        txtTravelOverview.text = summary
    }

    private fun setupUI() {
        findViews()
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)

        setSupportActionBar(binding.toolbar)
        // add back arrow to mToolbar
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
                presenter.onNavigationRowClick(index)
            }
        }
        bottomRoutingAdapter.setOnNavigationItemClickListener(listener)
        topRoutingAdapter.setOnNavigationItemClickListener(listener)

        topRecyclerView.attachSnapHelperToListener(snapHelper, object : OnSnapPositionChangeListener {
            override fun onSnapPositionChange(position: Int) {
                presenter.onTopRoutingBannerPositionChange(position)
            }
        }, OnSnapListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE_BY_DRAGGING)
    }

    private fun setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mapFragment === null) {
            mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            // Check if we were successful in obtaining the map.

            mapFragment?.getMapAsync { map ->
                googleMap = map
                if (map != null) {
                    // The map is verified. It is now safe to manipulate the map.
                    presenter.onGetMapAsync()
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