package com.iceteaviet.fastfoodfinder.ui.routing

import android.os.Bundle
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.Step
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableRes
import java.util.*

/**
 * Created by tom on 2019-04-18.
 */
class MapRoutingPresenter : BasePresenter<MapRoutingContract.Presenter>, MapRoutingContract.Presenter {

    private val mapRoutingView: MapRoutingContract.View

    private var inPreviewMode = false
    private var currLocation: LatLng? = null
    private var currStore: Store? = null
    private var stepList: List<Step> = ArrayList()
    private var geoPointList: List<LatLng> = ArrayList()
    private var currDirectionIndex = 0

    private var mapsDirection: MapsDirection? = null

    constructor(dataManager: DataManager, mapRoutingView: MapRoutingContract.View) : super(dataManager) {
        this.mapRoutingView = mapRoutingView
        this.mapRoutingView.presenter = this
    }

    override fun subscribe() {
        if (currStore != null)
            mapRoutingView.setStoreTitle(currStore!!.title!!)

        mapRoutingView.setRoutingStepList(stepList)
    }

    override fun fetchDataFromExtra(extras: Bundle?) {
        if (extras == null) {
            return
        }

        mapsDirection = extras.getParcelable(MapRoutingActivity.KEY_ROUTE_LIST)
        currStore = extras.getParcelable(MapRoutingActivity.KEY_DES_STORE)
    }

    override fun isRoutingDataValid(): Boolean {
        if (mapsDirection == null || currStore == null
                || mapsDirection!!.routeList.isEmpty() || mapsDirection!!.routeList[0].legList.isEmpty()) {
            return false
        }

        return true
    }

    override fun setupData() {
        mapsDirection?.let {
            stepList = it.routeList[0].legList[0].stepList
            currLocation = stepList[0].startMapCoordination.location
            geoPointList = PolyUtil.decode(it.routeList[0].encodedPolylineString)
        }
    }

    override fun onNavigationRowClick(index: Int) {
        currDirectionIndex = index
        mapRoutingView.animateMapCamera(stepList[index].endMapCoordination.location, true)
        mapRoutingView.enterPreviewMode()
        inPreviewMode = true
    }

    override fun onPrevInstructionClick() {
        currDirectionIndex--
        if (currDirectionIndex >= 0 && currDirectionIndex < stepList.size) {
            mapRoutingView.scrollToPosition(currDirectionIndex)
            mapRoutingView.animateMapCamera(stepList[currDirectionIndex].endMapCoordination.location, true)
        } else {
            currDirectionIndex = 0
        }
    }

    override fun onNextInstructionClick() {
        currDirectionIndex++
        if (currDirectionIndex >= 0 && currDirectionIndex < stepList.size) {
            mapRoutingView.scrollToPosition(currDirectionIndex)
            mapRoutingView.animateMapCamera(stepList[currDirectionIndex].endMapCoordination.location, true)
        } else {
            currDirectionIndex = stepList.size - 1
        }
    }

    override fun onBackArrowButtonPress() {
        if (inPreviewMode) {
            mapRoutingView.exitPreviewMode()
            inPreviewMode = false
        } else {
            mapRoutingView.exit()
        }
    }

    override fun onGetMapAsync() {
        currStore?.let {
            mapRoutingView.addMapMarker(it.getPosition(), it.title, it.address, getStoreLogoDrawableRes(it.type))
        }

        currLocation?.let {
            mapRoutingView.animateMapCamera(it, false)
            mapRoutingView.addMapMarker(it, "Your location",
                    "Your current location, please follow the line", R.drawable.ic_map_bluedot)

            mapRoutingView.drawRoutingPath(it, geoPointList)
        }

        mapsDirection?.let {
            mapRoutingView.setTravelDurationText(it.routeList[0].legList[0].getDuration())
            mapRoutingView.setTravelDistanceText(it.routeList[0].legList[0].getDistance())
            mapRoutingView.setTravelSummaryText(String.format("Via %s", it.routeList[0].summary))
        }
    }
}