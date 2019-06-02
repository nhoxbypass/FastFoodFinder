package com.iceteaviet.fastfoodfinder.data.remote.routing

import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store

/**
 * Created by tom on 7/18/18.
 */
interface MapsRoutingApiHelper {
    interface RoutingLoadCallback<T> {
        fun onSuccess(data: T)

        fun onError(throwable: Throwable)
    }

    fun getMapsDirection(queries: Map<String, String>, store: Store, callback: RoutingLoadCallback<MapsDirection>)
}
