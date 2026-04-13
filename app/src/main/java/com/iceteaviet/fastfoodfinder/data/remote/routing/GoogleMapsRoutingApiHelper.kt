package com.iceteaviet.fastfoodfinder.data.remote.routing

import com.iceteaviet.fastfoodfinder.data.remote.ApiEndPoint
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import com.iceteaviet.fastfoodfinder.utils.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoogleMapsRoutingApiHelper(googleMapBrowserKey: String) : MapsRoutingApiHelper {

    private val mMapDirectionApi: MapsRoutingApi

    init {
        mMapDirectionApi = get(googleMapBrowserKey, ApiEndPoint.GOOGLE_MAP_BASE_URL).create(MapsRoutingApi::class.java)
    }

    override suspend fun getMapsDirection(queries: Map<String, String>, store: Store): MapsDirection {
        return withContext(Dispatchers.IO) {
            val response = mMapDirectionApi.getDirection(queries).execute()
            val body = response.body()
            if (body != null) body else throw NotFoundException()
        }
    }

    companion object {
        private val TAG = GoogleMapsRoutingApiHelper::class.java.simpleName

        const val PARAM_DESTINATION = "destination"
        const val PARAM_ORIGIN = "origin"
    }
}
