package com.iceteaviet.fastfoodfinder.data.remote.routing;

import com.iceteaviet.fastfoodfinder.data.remote.routing.model.MapsDirection;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;

import java.util.Map;

import io.reactivex.Single;

/**
 * Created by tom on 7/18/18.
 */
public interface MapsRoutingApiHelper {
    Single<MapsDirection> getMapsDirection(Map<String, String> queries, final Store store);
}
