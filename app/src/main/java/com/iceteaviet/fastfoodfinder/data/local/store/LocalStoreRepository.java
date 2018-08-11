package com.iceteaviet.fastfoodfinder.data.local.store;

import com.iceteaviet.fastfoodfinder.data.domain.store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.data.local.store.model.StoreEntity;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.utils.Constant;
import com.iceteaviet.fastfoodfinder.utils.DataUtils;
import com.iceteaviet.fastfoodfinder.utils.exception.EmptyParamsException;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Genius Doan on 11/20/2016.
 */
public class LocalStoreRepository implements StoreDataSource {

    private static final String PARAM_ID = "id";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_LAT = "latitude";
    private static final String PARAM_LNG = "longitude";
    private static final String PARAM_TITLE = "title";
    private static final String PARAM_ADDRESS = "address";

    private List<Store> cachedStores;

    public LocalStoreRepository() {
        cachedStores = new ArrayList<>();
    }

    @Override
    public void setStores(final List<Store> storeList) {
        if (storeList != null && !storeList.isEmpty()) {
            // Cache
            cachedStores = storeList;

            // Write to persistence
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(@NonNull Realm realm) {
                    realm.where(StoreEntity.class)
                            .findAll()
                            .deleteAllFromRealm();

                    for (int i = 0; i < storeList.size(); i++) {
                        StoreEntity storeEntity = realm.createObject(StoreEntity.class);
                        storeEntity.map(storeList.get(i));
                    }
                }
            });

            realm.close();
        }
    }

    @Override
    public Single<List<Store>> getAllStores() {
        return Single.create(new SingleOnSubscribe<List<Store>>() {
            @Override
            public void subscribe(SingleEmitter<List<Store>> emitter) {
                if (cachedStores != null && cachedStores.size() > 0) {
                    // Use cached stores
                    emitter.onSuccess(new ArrayList<>(cachedStores));
                    return;
                }

                Realm realm = Realm.getDefaultInstance();
                RealmResults<StoreEntity> results = realm
                        .where(StoreEntity.class)
                        .findAll();

                cachedStores = new ArrayList<>();
                for (int i = 0; i < results.size(); i++) {
                    StoreEntity storeEntity = results.get(i);
                    if (storeEntity != null)
                        cachedStores.add(new Store(storeEntity));
                }

                realm.close();
                emitter.onSuccess(new ArrayList<>(cachedStores));
            }
        });
    }

    @Override
    public Single<List<Store>> getStoreInBounds(final double minLat, final double minLng, final double maxLat, final double maxLng) {
        return Single.create(new SingleOnSubscribe<List<Store>>() {
            @Override
            public void subscribe(SingleEmitter<List<Store>> emitter) {
                Realm realm = Realm.getDefaultInstance();
                List<Store> storeList = new ArrayList<>();
                // Build the query looking at all users:
                RealmQuery<StoreEntity> query = realm.where(StoreEntity.class);

                // Add query conditions:
                query.between(PARAM_LAT, minLat, maxLat);
                query.between(PARAM_LNG, minLng, maxLng);

                // Execute the query:
                RealmResults<StoreEntity> results = query.findAll();

                for (int i = 0; i < results.size(); i++) {
                    StoreEntity storeEntity = results.get(i);
                    if (storeEntity != null) {
                        Store store = new Store(storeEntity);
                        storeList.add(store);
                    }
                }

                realm.close();
                emitter.onSuccess(storeList);
            }
        });
    }

    @Override
    public Single<List<Store>> findStores(String queryString) {
        String trimmedQuery = queryString.toLowerCase().trim();
        if (isCircleKQuery(trimmedQuery)) {
            return findStoresByType(Constant.TYPE_CIRCLE_K);
        } else if (isMinisStopQuery(trimmedQuery)) {
            return findStoresByType(Constant.TYPE_MINI_STOP);
        } else if (isFamilyMartQuery(trimmedQuery)) {
            return findStoresByType(Constant.TYPE_FAMILY_MART);
        } else if (isShopnGoQuery(trimmedQuery)) {
            return findStoresByType(Constant.TYPE_SHOP_N_GO);
        } else if (isBsMartQuery(trimmedQuery)) {
            return findStoresByType(Constant.TYPE_BSMART);
        } else {
            // Cant determine
            // Quite hard to implement
            return findStoresByCustomAddress(DataUtils.normalizeDistrictQuery(queryString));
        }
    }

    @Override
    public Single<List<Store>> findStoresByCustomAddress(final List<String> customQuerySearch) {
        return Single.create(new SingleOnSubscribe<List<Store>>() {
            @Override
            public void subscribe(SingleEmitter<List<Store>> emitter) {
                Realm realm = Realm.getDefaultInstance();
                List<Store> storeList = new ArrayList<>();

                RealmQuery<StoreEntity> query = realm.where(StoreEntity.class);

                if (!customQuerySearch.isEmpty()) {
                    query.contains(PARAM_TITLE, customQuerySearch.get(0), Case.INSENSITIVE);
                    query.or().contains(PARAM_ADDRESS, customQuerySearch.get(0), Case.INSENSITIVE);

                    for (int i = 1; i < customQuerySearch.size(); i++) {
                        query.or().contains(PARAM_TITLE, customQuerySearch.get(i), Case.INSENSITIVE);
                        query.or().contains(PARAM_ADDRESS, customQuerySearch.get(i), Case.INSENSITIVE);
                    }

                    RealmResults<StoreEntity> results = query.findAll();

                    int size = results.size();
                    for (int i = 0; i < size; i++) {
                        StoreEntity storeEntity = results.get(i);
                        if (storeEntity != null) {
                            Store store = new Store(storeEntity);
                            storeList.add(store);
                        }
                    }
                }

                realm.close();

                emitter.onSuccess(storeList);
            }
        });
    }

    @Override
    public Single<List<Store>> findStoresBy(final String key, final int value) {
        return Single.create(new SingleOnSubscribe<List<Store>>() {
            @Override
            public void subscribe(SingleEmitter<List<Store>> emitter) {
                Realm realm = Realm.getDefaultInstance();
                List<Store> storeList = new ArrayList<>();

                RealmQuery<StoreEntity> query = realm.where(StoreEntity.class);

                query.equalTo(key, value);

                RealmResults<StoreEntity> results = query.findAll();

                int size = results.size();
                for (int i = 0; i < size; i++) {
                    StoreEntity storeEntity = results.get(i);
                    if (storeEntity != null) {
                        Store store = new Store(storeEntity);
                        storeList.add(store);
                    }
                }

                realm.close();

                emitter.onSuccess(storeList);
            }
        });
    }

    @Override
    public Single<List<Store>> findStoresBy(final String key, final List<Integer> values) {
        if (values.isEmpty())
            return Single.error(new EmptyParamsException());

        return Single.create(new SingleOnSubscribe<List<Store>>() {
            @Override
            public void subscribe(SingleEmitter<List<Store>> emitter) {
                Realm realm = Realm.getDefaultInstance();
                List<Store> storeList = new ArrayList<>();

                RealmQuery<StoreEntity> query = realm.where(StoreEntity.class);

                query.equalTo(key, values.get(0));
                for (int i = 1; i < values.size(); i++) {
                    query.or().equalTo(key, values.get(i));
                }

                RealmResults<StoreEntity> results = query.findAll();

                int size = results.size();
                for (int i = 0; i < size; i++) {
                    StoreEntity storeEntity = results.get(i);
                    if (storeEntity != null) {
                        Store store = new Store(storeEntity);
                        storeList.add(store);
                    }
                }

                realm.close();
                emitter.onSuccess(storeList);
            }
        });
    }

    @Override
    public Single<List<Store>> findStoresByType(int type) {
        return findStoresBy(PARAM_TYPE, type);
    }


    @Override
    public Single<List<Store>> findStoresById(int id) {
        return findStoresBy(PARAM_ID, id);
    }

    @Override
    public Single<List<Store>> findStoresByIds(List<Integer> ids) {
        return findStoresBy(PARAM_ID, ids);
    }

    @Override
    public void deleteAllStores() {
        cachedStores.clear();
        Realm realm = Realm.getDefaultInstance();
        realm.where(StoreEntity.class)
                .findAll()
                .deleteAllFromRealm();
    }

    public void clearCache() {
        cachedStores.clear();
    }

    private boolean isCircleKQuery(String query) {
        return query.equals("circle k") || query.equals("circlek");
    }

    private boolean isMinisStopQuery(String query) {
        return query.equals("mini stop") || query.equals("ministop");
    }

    private boolean isFamilyMartQuery(String queryString) {
        return queryString.equals("family mart") || queryString.equals("familymart");
    }

    private boolean isShopnGoQuery(String queryString) {
        return queryString.equals("shop and go") || queryString.equals("shopandgo") || queryString.equals("shop n go");
    }

    private boolean isBsMartQuery(String queryString) {
        return queryString.equals("bsmart") || queryString.equals("b smart") || queryString.equals("bs mart") || queryString.equals("bmart") || queryString.equals("b'smart") || queryString.equals("b's mart");
    }
}