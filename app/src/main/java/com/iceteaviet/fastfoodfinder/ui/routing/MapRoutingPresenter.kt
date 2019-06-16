package com.iceteaviet.fastfoodfinder.ui.routing

import androidx.annotation.VisibleForTesting
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.Step
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BasePresenter
import com.iceteaviet.fastfoodfinder.utils.isValidLocation
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.ui.getStoreLogoDrawableRes
import java.util.*

/**
 * Created by tom on 2019-04-18.
 */
class MapRoutingPresenter : BasePresenter<MapRoutingContract.Presenter>, MapRoutingContract.Presenter {

    private val mapRoutingView: MapRoutingContract.View

    @VisibleForTesting
    var inPreviewMode = false
    @VisibleForTesting
    var currLocation: LatLng? = null
    @VisibleForTesting
    lateinit var currStore: Store
    @VisibleForTesting
    var stepList: List<Step> = ArrayList()
    @VisibleForTesting
    var geoPointList: List<LatLng> = ArrayList()
    @VisibleForTesting
    var currDirectionIndex = 0

    @VisibleForTesting
    lateinit var mapsDirection: MapsDirection

    constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, mapRoutingView: MapRoutingContract.View) : super(dataManager, schedulerProvider) {
        this.mapRoutingView = mapRoutingView
    }

    override fun subscribe() {
    }

    override fun handleExtras(mapsDirection: MapsDirection?, store: Store?) {
        if (isRoutingDataValid(mapsDirection, store)) {
            setupData(mapsDirection!!, store!!)
            mapRoutingView.setStoreTitle(currStore.title)
            mapRoutingView.setRoutingStepList(stepList)
        } else {
            mapRoutingView.showGetDirectionFailedMessage()
            mapRoutingView.exit()
        }
    }

    override fun onNavigationRowClick(index: Int) {
        if (index < 0 || index >= stepList.size) {
            mapRoutingView.showGeneralErrorMessage()
            return
        }

        currDirectionIndex = index
        mapRoutingView.animateMapCamera(stepList[index].endMapCoordination.location, true)
        mapRoutingView.enterPreviewMode()
        inPreviewMode = true
    }

    override fun onPrevInstructionClick() {
        currDirectionIndex--
        if (currDirectionIndex >= 0) {
            mapRoutingView.scrollToPosition(currDirectionIndex)
            mapRoutingView.animateMapCamera(stepList[currDirectionIndex].endMapCoordination.location, true)
        } else {
            currDirectionIndex = 0
        }
    }

    override fun onNextInstructionClick() {
        currDirectionIndex++
        if (currDirectionIndex < stepList.size) {
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
        mapRoutingView.addMapMarker(currStore.getPosition(), currStore.title, currStore.address, getStoreLogoDrawableRes(currStore.type))

        currLocation?.let {
            mapRoutingView.animateMapCamera(it, false)
            mapRoutingView.addMapMarker(it, "Your location",
                    "Your current location, please follow the line", R.drawable.ic_map_bluedot)
        }

        mapRoutingView.drawRoutingPath(currLocation, geoPointList)

        mapRoutingView.setTravelDurationText(mapsDirection.routeList[0].legList[0].getDuration())
        mapRoutingView.setTravelDistanceText(mapsDirection.routeList[0].legList[0].getDistance())
        mapRoutingView.setTravelSummaryText(String.format("Via %s", mapsDirection.routeList[0].summary))
    }

    private fun isRoutingDataValid(mapsDirection: MapsDirection?, store: Store?): Boolean {
        if (store == null || !isValidLocation(store.getPosition()))
            return false

        if (mapsDirection == null
                || mapsDirection.routeList.isEmpty()
                || mapsDirection.routeList[0].legList.isEmpty()
                || mapsDirection.routeList[0].legList[0].stepList.isEmpty()) {
            return false
        }

        return true
    }

    private fun setupData(mapsDirection: MapsDirection, store: Store) {
        this.mapsDirection = mapsDirection
        currStore = store

        stepList = mapsDirection.routeList[0].legList[0].stepList
        currLocation = stepList[0].startMapCoordination.location
        geoPointList = PolyUtil.decode(mapsDirection.routeList[0].encodedPolylineString)
    }
}