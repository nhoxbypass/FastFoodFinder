package com.iceteaviet.fastfoodfinder.data.remote.routing

import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.domain.model.Store
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import java.util.*

class FakeGoogleMapsRoutingApiHelper : MapsRoutingApiHelper {

    private val DIRECTION_SERVICE_DATA: Map<String, MapsDirection>

    init {
        DIRECTION_SERVICE_DATA = TreeMap()
    }

    override suspend fun getMapsDirection(queries: Map<String, String>, store: Store): MapsDirection {
        val response = DIRECTION_SERVICE_DATA.get(queries.toString())
        if (response != null)
            return response
        else
            throw NotFoundException()
    }
}
