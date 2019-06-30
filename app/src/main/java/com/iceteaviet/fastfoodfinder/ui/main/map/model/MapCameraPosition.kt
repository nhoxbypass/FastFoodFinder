package com.iceteaviet.fastfoodfinder.ui.main.map.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

/**
 * Created by tom on 2019-04-29.
 */
data class MapCameraPosition(var cameraPosition: LatLng, var cameraBounds: LatLngBounds)