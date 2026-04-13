package com.iceteaviet.fastfoodfinder.data.remote.routing

import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store

interface MapsRoutingApiHelper {
    suspend fun getMapsDirection(queries: Map<String, String>, store: Store): MapsDirection
}
