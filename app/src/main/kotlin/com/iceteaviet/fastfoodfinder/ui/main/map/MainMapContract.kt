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
        fun requestLocationUpdates()
        fun getLastLocation()
        fun animateMapCamera(location: LatLng, zoomToDetail: Boolean)
        fun showWarningMessage(@StringRes stringId: Int)
        fun addMarkersToMap(storeList: MutableList<Store>)
        fun setupMap()
        fun setupMapEventHandlers()
        fun showMapRoutingView(currStore: Store, mapsDirection: MapsDirection)
        fun showDialogStoreInfo(store: Store)
        fun animateMapMarker(marker: Marker?, storeType: Int)
        fun setNearByStores(nearbyStores: List<NearByStore>)
        fun clearNearByStores()
    }

    interface Presenter : com.iceteaviet.fastfoodfinder.ui.base.Presenter {
        fun onLocationPermissionGranted()
        fun onCurrLocationChanged(latitude: Double, longitude: Double)
        fun onMapCameraMove(cameraPosition: LatLng, bounds: LatLngBounds)
        fun onGetMapAsync()
        fun onDirectionNavigateClick(store: Store)
        fun onMapMarkerAdd(storeId: Int, marker: Marker)
    }
}