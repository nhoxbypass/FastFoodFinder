package com.iceteaviet.fastfoodfinder.data.remote.routing

import androidx.annotation.NonNull
import com.iceteaviet.fastfoodfinder.data.remote.ApiEndPoint
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store
import com.iceteaviet.fastfoodfinder.utils.e
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import com.iceteaviet.fastfoodfinder.utils.get
import io.reactivex.Single
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by tom on 7/18/18.
 */
class GoogleMapsRoutingApiHelper(googleMapBrowserKey: String) : MapsRoutingApiHelper {

    private val mMapDirectionApi: MapsRoutingApi

    init {
        mMapDirectionApi = get(googleMapBrowserKey, ApiEndPoint.GOOGLE_MAP_BASE_URL).create(MapsRoutingApi::class.java)
    }

    override fun getMapsDirection(queries: Map<String, String>, store: Store): Single<MapsDirection> {
        return Single.create { emitter ->
            mMapDirectionApi.getDirection(queries).enqueue(object : Callback<MapsDirection> {
                override fun onResponse(@NonNull call: Call<MapsDirection>, @NonNull response: Response<MapsDirection>) {
                    val body = response.body()
                    if (body != null)
                        emitter.onSuccess(body)
                    else
                        emitter.onError(NotFoundException())
                }

                override fun onFailure(@NonNull call: Call<MapsDirection>, @NonNull t: Throwable) {
                    e(TAG, "Get direction failed")
                    emitter.onError(t)
                }
            })
        }
    }

    companion object {
        private val TAG = GoogleMapsRoutingApiHelper::class.java.simpleName

        const val PARAM_DESTINATION = "destination"
        const val PARAM_ORIGIN = "origin"
    }
}
