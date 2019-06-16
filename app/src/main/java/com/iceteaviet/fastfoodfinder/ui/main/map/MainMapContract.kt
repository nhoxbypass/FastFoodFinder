package com.iceteaviet.fastfoodfinder.ui.main.map

import androidx.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.ui.base.BaseView
import com.iceteaviet.fastfoodfinder.ui.main.map.model.NearByStore

/**
 * Created by tom on 2019-04-18.
 */
interface MainMapContract {
    interface View : BaseView<Presenter> {
        fun setMyLocationEnabled(enabled: Boolean)
        fun animateMapCamera(location: LatLng, zoomToDetail: Boolean)
        fun showWarningMessage(@StringRes stringId: Int)
        fun showGeneralErrorMessage()
        fun showCannotGetLocationMessage()
        fun showInvalidStoreLocationWarning()
        fun addMarkersToMap(storeList: List<Store>)
        fun setupMap()
        fun setupMapEventHandlers()
        fun showMapRoutingView(currStore: Store, mapsDirection: MapsDirection)
        fun showDialogStoreInfo(store: Store)
        fun animateMapMarker(marker: Marker?, storeType: Int)
        fun setNearByStores(nearbyStores: List<NearByStore>)
        fun clearNearByStores()
        fun clearMapData()
        fun requestLocationPermission()
        fun isLocationPermissionGranted(): Boolean
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onLocationPermissionGranted()
        fun requestCurrentLocation()
        fun onMapCameraMove(cameraPosition: LatLng, bounds: LatLngBounds)
        fun onGetMapAsync()
        fun onNavigationButtonClick(store: Store)
        fun onMapMarkerAdd(storeId: Int, marker: Marker)
        fun onClearOldMapData()
    }
}