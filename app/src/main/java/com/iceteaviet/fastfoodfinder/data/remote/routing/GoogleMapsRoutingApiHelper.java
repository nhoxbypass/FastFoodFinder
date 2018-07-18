package com.iceteaviet.fastfoodfinder.data.remote.routing;

import android.support.annotation.NonNull;
import android.util.Log;

import com.iceteaviet.fastfoodfinder.data.remote.ApiEndPoint;
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.utils.RetrofitUtils;

import java.util.Map;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by tom on 7/18/18.
 */
public class GoogleMapsRoutingApiHelper implements MapsRoutingApiHelper {
    public static final String TAG = GoogleMapsRoutingApiHelper.class.getSimpleName();
    public String googleMapBrowserKey = "";
    private MapsRoutingApi mMapDirectionApi;

    public GoogleMapsRoutingApiHelper(String key) {
        if (key != null)
            this.googleMapBrowserKey = key;

        mMapDirectionApi = RetrofitUtils.get(googleMapBrowserKey, ApiEndPoint.GOOGLE_MAP_BASE_URL).create(MapsRoutingApi.class);
    }

    @Override
    public Single<MapsDirection> getMapsDirection(final Map<String, String> queries, Store store) {
        return Single.create(new SingleOnSubscribe<MapsDirection>() {
            @Override
            public void subscribe(final SingleEmitter<MapsDirection> emitter) throws Exception {
                mMapDirectionApi.getDirection(queries).enqueue(new Callback<MapsDirection>() {
                    @Override
                    public void onResponse(@NonNull Call<MapsDirection> call, @NonNull Response<MapsDirection> response) {
                        emitter.onSuccess(response.body());
                    }

                    @Override
                    public void onFailure(@NonNull Call<MapsDirection> call, @NonNull Throwable t) {
                        Log.e(TAG, "Get direction failed");
                        emitter.onError(t);
                    }
                });
            }
        });
    }
}
