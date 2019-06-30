package com.iceteaviet.fastfoodfinder.data.domain.routing

import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import io.reactivex.Single

/**
 * Created by tom on 7/18/18.
 */
class AppMapsRoutingRepository(private val mapsRoutingApiHelper: MapsRoutingApiHelper) : MapsRoutingRepository {

    override fun getMapsDirection(queries: Map<String, String>, store: Store): Single<MapsDirection> {
        return Single.create { emitter ->
            mapsRoutingApiHelper.getMapsDirection(queries, store, object : MapsRoutingApiHelper.RoutingLoadCallback<MapsDirection> {
                override fun onSuccess(data: MapsDirection) {
                    emitter.onSuccess(data)
                }

                override fun onError(throwable: Throwable) {
                    emitter.onError(throwable)
                }
            })
        }
    }

    companion object {
        private val TAG = AppMapsRoutingRepository::class.java.simpleName
    }
}
