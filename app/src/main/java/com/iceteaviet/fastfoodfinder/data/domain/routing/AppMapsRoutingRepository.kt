package com.iceteaviet.fastfoodfinder.data.domain.routing

import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppMapsRoutingRepository(private val mapsRoutingApiHelper: MapsRoutingApiHelper) : MapsRoutingRepository {

    override suspend fun getMapsDirection(queries: Map<String, String>, store: Store): MapsDirection {
        return mapsRoutingApiHelper.getMapsDirection(queries, store)
    }

    companion object {
        private val TAG = AppMapsRoutingRepository::class.java.simpleName
    }
}
