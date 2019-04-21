package com.iceteaviet.fastfoodfinder.ui.routing

import android.os.Bundle
import androidx.annotation.DrawableRes
import com.google.android.gms.maps.model.LatLng
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.Step
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface MapRoutingContract {
    interface View : BaseView<Presenter> {
        fun setStoreTitle(title: String)
        fun setRoutingStepList(stepList: List<Step>)
        fun enterPreviewMode()
        fun exitPreviewMode()
        fun scrollToPosition(directionIndex: Int)
        fun exit()
        fun animateMapCamera(location: LatLng, zoomToDetail: Boolean)
        fun addMapMarker(location: LatLng, title: String, description: String, @DrawableRes icon: Int)
        fun drawRoutingPath(currLocation: LatLng, routingGeoPoint: List<LatLng>)
        fun setTravelDurationText(duration: String)
        fun setTravelDistanceText(distance: String)
        fun setTravelSummaryText(summary: String)
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun fetchDataFromExtra(extras: Bundle?)
        fun checkDataValid(): Boolean
        fun setupData()
        fun onNavigationRowClick(index: Int)
        fun onPrevInstructionClick()
        fun onNextInstructionClick()
        fun onBackArrowButtonPress()
        fun onLoadMap()
    }
}