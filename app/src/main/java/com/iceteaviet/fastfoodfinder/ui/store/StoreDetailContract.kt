package com.iceteaviet.fastfoodfinder.ui.store

import android.os.Parcelable
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Comment
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BaseView

/**
 * Created by tom on 2019-04-18.
 */
interface StoreDetailContract {
    interface View : BaseView<Presenter> {
        fun requestLocationPermission()
        fun isLocationPermissionGranted(): Boolean
        fun setToolbarTitle(title: String)
        fun showCannotGetLocationMessage()
        fun setStoreComments(listComments: MutableList<Comment>)
        fun addStoreComment(comment: Comment)
        fun setAppBarExpanded(expanded: Boolean)
        fun scrollToCommentList()
        fun showCommentEditorView()
        fun startCallIntent(tel: String)
        fun showInvalidPhoneNumbWarning()
        fun showMapRoutingView(currStore: Store, mapsDirection: MapsDirection)
        fun exit()
        fun showStoreAddedToFavMessage()
        fun showGeneralErrorMessage()
        fun showInvalidStoreLocationWarning()
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onLocationPermissionGranted()
        fun requestCurrentLocation()
        fun handleExtras(extras: Parcelable?)
        fun onAddNewComment(comment: Comment?)
        fun onCommentButtonClick()
        fun onCallButtonClick()
        fun onNavigationButtonClick()
        fun onAddToFavButtonClick()
        fun onSaveButtonClick()
    }
}