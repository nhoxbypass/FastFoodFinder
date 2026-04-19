package com.iceteaviet.fastfoodfinder.domain.model

import com.google.android.gms.maps.model.LatLng

fun Store.toLatLng(): LatLng = LatLng(lat, lng)
