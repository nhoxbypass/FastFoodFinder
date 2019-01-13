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
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.Step
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BaseActivity
import com.iceteaviet.fastfoodfinder.utils.Constant.DEFAULT_ZOOM_LEVEL
import com.iceteaviet.fastfoodfinder.utils.Constant.DETAILED_ZOOM_LEVEL
import com.iceteaviet.fastfoodfinder.utils.convertDpToPx
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableRes
import kotlinx.android.synthetic.main.activity_map_routing.*
import java.util.*


class MapRoutingActivity : BaseActivity() {
    lateinit var txtTravelTime: TextView
    lateinit var txtTravelDistance: TextView
    lateinit var txtTravelOverview: TextView
    lateinit var bottomRecyclerView: RecyclerView
    lateinit var topRecyclerView: RecyclerView
    lateinit var mBottomSheetContainer: LinearLayout
    lateinit var prevInstruction: ImageButton
    lateinit var nextInstruction: ImageButton
    lateinit var routingButtonContainer: LinearLayout

    private var mListener: RoutingAdapter.OnNavigationItemClickListener? = null
    private var isPreviewMode = false
    private var mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var mGoogleMap: GoogleMap? = null
    private var mMapFragment: SupportMapFragment? = null
    private var mMapsDirection: MapsDirection? = null
    private var mCurrDirection: Polyline? = null
    private var mCurrLocation: LatLng? = null
    private var mCurrStore: Store? = null
    private var mBottomRoutingAdapter: RoutingAdapter? = null
    private var mTopRoutingAdapter: RoutingAdapter? = null
    private var mStepList: List<Step> = ArrayList()
    private var mGeoPointList: List<LatLng> = ArrayList()
    private var currDirectionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        txtTravelTime = tv_routing_time
        txtTravelDistance = tv_routing_distance
        txtTravelOverview = tv_routing_overview
        bottomRecyclerView = rv_bottom_sheet
        topRecyclerView = rv_direction_instruction
        mBottomSheetContainer = ll_bottom_sheet
        prevInstruction = btn_prev_instruction
        nextInstruction = btn_next_instruction
        routingButtonContainer = ll_routing_button_container

        if (setupData()) {

            setupUI()

            setupEventListeners()

            setUpMapIfNeeded()
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_map_routing

    private fun setupEventListeners() {
        mListener = object : RoutingAdapter.OnNavigationItemClickListener {
            override fun onClick(index: Int) {
                showDirectionAt(index)
                currDirectionIndex = index
                enterPreviewMode()
            }
        }

        prevInstruction.setOnClickListener {
            currDirectionIndex--
            if (currDirectionIndex >= 0 && currDirectionIndex < mBottomRoutingAdapter!!.itemCount) {
                topRecyclerView.smoothScrollToPosition(currDirectionIndex)
                showDirectionAt(currDirectionIndex)
            } else {
                currDirectionIndex = 0
            }
        }

        nextInstruction.setOnClickListener {
            currDirectionIndex++
            if (currDirectionIndex >= 0 && currDirectionIndex < mBottomRoutingAdapter!!.itemCount) {
                topRecyclerView.smoothScrollToPosition(currDirectionIndex)
                showDirectionAt(currDirectionIndex)
            } else {
                currDirectionIndex = mBottomRoutingAdapter!!.itemCount - 1
            }
        }
    }

    private fun setupUI() {
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetContainer)

        setSupportActionBar(toolbar)
        // add back arrow to mToolbar
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title = mCurrStore!!.title
        }

        mBottomRoutingAdapter = RoutingAdapter(mStepList, RoutingAdapter.TYPE_FULL)
        val layoutManager = LinearLayoutManager(this@MapRoutingActivity)
        bottomRecyclerView.layoutManager = layoutManager
        bottomRecyclerView.adapter = mBottomRoutingAdapter

        mTopRoutingAdapter = RoutingAdapter(mStepList, RoutingAdapter.TYPE_SHORT)
        val topLayoutManager = LinearLayoutManager(this@MapRoutingActivity, LinearLayoutManager.HORIZONTAL, false)
        topRecyclerView.layoutManager = topLayoutManager
        topRecyclerView.adapter = mTopRoutingAdapter
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(topRecyclerView)
    }

    private fun setupData(): Boolean {
        val extras = intent.extras
        if (extras == null) {
            finish()
            return false
        }

        mMapsDirection = extras.getParcelable(KEY_ROUTE_LIST)
        mCurrStore = extras.getParcelable(KEY_DES_STORE)

        if (mMapsDirection == null || mCurrStore == null
                || mMapsDirection!!.routeList.isEmpty() || mMapsDirection!!.routeList[0].legList.isEmpty()) {
            Toast.makeText(this@MapRoutingActivity, R.string.get_map_direction_failed, Toast.LENGTH_SHORT).show()
            finish()
            return false
        }

        mStepList = mMapsDirection!!.routeList[0].legList[0].stepList
        mCurrLocation = mStepList[0].startMapCoordination.location
        mGeoPointList = PolyUtil.decode(mMapsDirection!!.routeList[0].encodedPolylineString)

        return true
    }

    private fun showDirectionAt(currDirectionIndex: Int) {
        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(mBottomRoutingAdapter!!.getDirectionLocationAt(currDirectionIndex), DETAILED_ZOOM_LEVEL))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            if (isPreviewMode) {
                exitPreviewMode()
            } else {
                finish() // close this activity and return to preview activity (if there is any)

            }
        }

        return super.onOptionsItemSelected(item)
    }

    protected fun setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMapFragment === null) {
            mMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            // Check if we were successful in obtaining the map.
            if (mMapFragment !== null) {
                mMapFragment!!.getMapAsync { map -> loadMap(map) }
            }
        }
    }

    // The Map is verified. It is now safe to manipulate the map.
    protected fun loadMap(googleMap: GoogleMap?) {
        if (googleMap != null) {
            // ... use map here
            mGoogleMap = googleMap
            mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(mCurrLocation, DEFAULT_ZOOM_LEVEL))

            googleMap.addMarker(MarkerOptions().position(mCurrLocation!!)
                    .title("Your location")
                    .snippet("Your current location, please follow the line")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_bluedot)))

            if (mCurrStore != null && mCurrStore!!.getPosition() != null) {
                googleMap.addMarker(MarkerOptions().position(mCurrStore!!.getPosition())
                        .title(mCurrStore!!.title)
                        .snippet(mCurrStore!!.address)
                        .icon(BitmapDescriptorFactory.fromResource(getStoreLogoDrawableRes(mCurrStore!!.type))))
            }

            mGoogleMap?.let { drawPolylines(mGeoPointList, it) }


            txtTravelTime.text = mMapsDirection!!.routeList[0].legList[0].getDuration()
            txtTravelDistance.text = mMapsDirection!!.routeList[0].legList[0].getDistance()
            txtTravelOverview.text = String.format("Via %s", mMapsDirection!!.routeList[0].summary)


            mBottomRoutingAdapter!!.setOnNavigationItemClickListener(mListener!!)
            mTopRoutingAdapter!!.setOnNavigationItemClickListener(mListener!!)
        }
    }

    private fun drawPolylines(geoPointList: List<LatLng>, googleMap: GoogleMap) {
        //Add position to viewBounds
        val builder = LatLngBounds.Builder()
        builder.include(mCurrLocation!!)

        val options = PolylineOptions()
                .clickable(true)
                .color(ContextCompat.getColor(this@MapRoutingActivity, R.color.googleBlue))
                .width(12f)
                .geodesic(true)
                .zIndex(5f)

        for (i in geoPointList.indices) {
            val geoPoint = geoPointList[i]
            options.add(geoPoint)
            builder.include(geoPoint)
        }

        if (mCurrDirection != null)
            mCurrDirection!!.remove()

        mCurrDirection = googleMap.addPolyline(options)

        //Build the viewbounds contain all markers
        val bounds = builder.build()

        zoomToShowAllMarker(bounds, googleMap)

    }

    private fun zoomToShowAllMarker(bounds: LatLngBounds, googleMap: GoogleMap) {
        val displayMetrics = resources.displayMetrics

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels - convertDpToPx(160f)
        val padding = 24 // offset from edges of the map in pixels
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height.toInt(), padding)

        googleMap.animateCamera(cu)
    }

    private fun enterPreviewMode() {
        routingButtonContainer.visibility = View.VISIBLE
        topRecyclerView.visibility = View.VISIBLE
        mBottomSheetBehavior!!.isHideable = true
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_HIDDEN
        isPreviewMode = true
    }

    private fun exitPreviewMode() {
        routingButtonContainer.visibility = View.GONE
        topRecyclerView.visibility = View.GONE
        mBottomSheetBehavior!!.isHideable = false
        mBottomSheetBehavior!!.state = BottomSheetBehavior.STATE_COLLAPSED
        isPreviewMode = false
    }

    companion object {
        const val KEY_ROUTE_LIST = "route_list"
        const val KEY_DES_STORE = "des_store"
    }
}
