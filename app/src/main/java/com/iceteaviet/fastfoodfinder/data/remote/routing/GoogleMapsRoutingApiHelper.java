package com.iceteaviet.fastfoodfinder.data.remote.routing;

import com.iceteaviet.fastfoodfinder.data.remote.ApiEndPoint;
import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.utils.AppLogger;
import com.iceteaviet.fastfoodfinder.utils.RetrofitUtils;

import java.util.Map;

import androidx.annotation.NonNull;
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
    private static final String TAG = GoogleMapsRoutingApiHelper.class.getSimpleName();

    public static final String PARAM_DESTINATION = "destination";
    public static final String PARAM_ORIGIN = "origin";

    private MapsRoutingApi mMapDirectionApi;

    public GoogleMapsRoutingApiHelper(String googleMapBrowserKey) {
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
                        AppLogger.e(TAG, "Get direction failed");
                        emitter.onError(t);
                    }
                });
            }
        });
    }
}
