package com.iceteaviet.fastfoodfinder.data.remote.routing

import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import java.util.*

/**
 * Created by tom on 7/18/18.
 */
class FakeGoogleMapsRoutingApiHelper : MapsRoutingApiHelper {

    private val DIRECTION_SERVICE_DATA: Map<String, MapsDirection>

    init {
        DIRECTION_SERVICE_DATA = TreeMap()
    }

    override fun getMapsDirection(queries: Map<String, String>, store: Store, callback: MapsRoutingApiHelper.RoutingLoadCallback<MapsDirection>) {
        val response = DIRECTION_SERVICE_DATA.get(queries.toString())
        if (response != null)
            callback.onSuccess(response)
        else
            callback.onError(NotFoundException())
    }
}
