package com.iceteaviet.fastfoodfinder.data.remote.routing

import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import io.reactivex.Single
import java.util.*

/**
 * Created by tom on 7/18/18.
 */
class FakeGoogleMapsRoutingApiHelper : MapsRoutingApiHelper {

    private val DIRECTION_SERVICE_DATA: Map<String, MapsDirection>

    init {
        DIRECTION_SERVICE_DATA = TreeMap()
    }

    override fun getMapsDirection(queries: Map<String, String>, store: Store): Single<MapsDirection> {
        return Single.create { emitter ->
            val response = DIRECTION_SERVICE_DATA.get(queries.toString())
            if (response != null)
                emitter.onSuccess(response)
            else
                emitter.onError(NotFoundException())
        }
    }
}
