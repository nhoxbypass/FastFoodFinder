package com.iceteaviet.fastfoodfinder.data.domain.routing

import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.domain.model.Store

interface MapsRoutingRepository {
    suspend fun getMapsDirection(queries: Map<String, String>, store: Store): MapsDirection
}
