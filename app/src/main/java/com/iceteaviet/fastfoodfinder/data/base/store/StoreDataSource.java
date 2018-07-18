package com.iceteaviet.fastfoodfinder.data.base.store;

import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by tom on 7/15/18.
 */
public interface StoreDataSource {
    void setStores(List<Store> storeList);

    Single<List<Store>> getAllStores();

    Single<List<Store>> getStoreInBounds(double minLat, double minLng, double maxLat, double maxLng);

    Single<List<Store>> findStores(String queryString);

    Single<List<Store>> findStoresByCustomAddress(List<String> customQuerySearch);

    Single<List<Store>> findStoresBy(String key, int value);

    Single<List<Store>> findStoresBy(String key, List<Integer> values);

    Single<List<Store>> findStoresByType(int type);

    Single<List<Store>> findStoresById(int id);

    Single<List<Store>> findStoresByIds(List<Integer> ids);
}
