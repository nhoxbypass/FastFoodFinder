package com.iceteaviet.fastfoodfinder.data.local.store;

import android.support.annotation.NonNull;

import com.iceteaviet.fastfoodfinder.data.base.store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.data.local.store.model.StoreEntity;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.utils.Constant;
import com.iceteaviet.fastfoodfinder.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

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

    public LocalStoreRepository() {

    }

    @Override
    public void setStores(final List<Store> storeList) {
        if (storeList != null) {
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
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
                Realm realm = Realm.getDefaultInstance();
                List<Store> storeList = new ArrayList<>();
                RealmResults<StoreEntity> results = realm
                        .where(StoreEntity.class)
                        .findAll();

                for (int i = 0; i < results.size(); i++) {
                    storeList.add(new Store(results.get(i)));
                }

                realm.close();
                emitter.onSuccess(storeList);
            }
        });
    }

    @Override
    public Single<List<Store>> getStoreInBounds(final double minLat, final double minLng, final double maxLat, final double maxLng) {
        return Single.create(new SingleOnSubscribe<List<Store>>() {
            @Override
            public void subscribe(SingleEmitter<List<Store>> emitter) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                List<Store> storeList = new ArrayList<>();
                // Build the query looking at all users:
                RealmQuery<StoreEntity> query = realm.where(StoreEntity.class);

                // Add query conditions:
                query.between("latitude", minLat, maxLat);
                query.between("longitude", minLng, maxLng);

                // Execute the query:
                RealmResults<StoreEntity> result = query.findAll();

                for (int i = 0; i < result.size(); i++) {
                    Store store = new Store(result.get(i));
                    storeList.add(store);
                }

                realm.close();
                emitter.onSuccess(storeList);
            }
        });
    }

    @Override
    public Single<List<Store>> findStores(String queryString) {
        queryString = queryString.toLowerCase().trim();
        if (queryString.equals("circle k") || queryString.equals("circlek")) {
            return findStoresByType(Constant.TYPE_CIRCLE_K);
        } else if (queryString.equals("mini stop") || queryString.equals("logo_ministop_red")) {
            return findStoresByType(Constant.TYPE_MINI_STOP);
        } else if (queryString.equals("family mart") || queryString.equals("logo_familymart_red")) {
            return findStoresByType(Constant.TYPE_FAMILY_MART);
        } else if (queryString.equals("shop and go") || queryString.equals("shopandgo") || queryString.equals("shop n go")) {
            return findStoresByType(Constant.TYPE_SHOP_N_GO);
        } else if (queryString.equals("logo_bsmart_red") || queryString.equals("b smart") || queryString.equals("bs mart") || queryString.equals("bmart") || queryString.equals("b'smart") || queryString.equals("b's mart")) {
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
            public void subscribe(SingleEmitter<List<Store>> emitter) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                List<Store> storeList = new ArrayList<>();

                RealmQuery<StoreEntity> query = realm.where(StoreEntity.class);

                if (!customQuerySearch.isEmpty()) {
                    query.contains("title", customQuerySearch.get(0), Case.INSENSITIVE);
                    query.or().contains("address", customQuerySearch.get(0), Case.INSENSITIVE);

                    for (int i = 1; i < customQuerySearch.size(); i++) {
                        query.or().contains("title", customQuerySearch.get(i), Case.INSENSITIVE);
                        query.or().contains("address", customQuerySearch.get(i), Case.INSENSITIVE);
                    }

                    RealmResults<StoreEntity> results = query.findAll();

                    int size = results.size();
                    for (int i = 0; i < size; i++) {
                        Store store = new Store(results.get(i));
                        storeList.add(store);
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
            public void subscribe(SingleEmitter<List<Store>> emitter) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                List<Store> storeList = new ArrayList<>();

                RealmQuery<StoreEntity> query = realm.where(StoreEntity.class);

                query.equalTo(key, value);

                RealmResults<StoreEntity> results = query.findAll();

                int size = results.size();
                for (int i = 0; i < size; i++) {
                    Store store = new Store(results.get(i));
                    storeList.add(store);
                }

                realm.close();

                emitter.onSuccess(storeList);
            }
        });
    }

    @Override
    public Single<List<Store>> findStoresBy(final String key, final List<Integer> values) {
        if (values.isEmpty())
            return Single.error(new NullPointerException("Empty values!"));

        return Single.create(new SingleOnSubscribe<List<Store>>() {
            @Override
            public void subscribe(SingleEmitter<List<Store>> emitter) throws Exception {
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
                    Store store = new Store(results.get(i));
                    storeList.add(store);
                }

                realm.close();
                emitter.onSuccess(storeList);
            }
        });
    }

    @Override
    public Single<List<Store>> findStoresByType(int type) {
        return findStoresBy("type", type);
    }


    @Override
    public Single<List<Store>> findStoresById(int id) {
        return findStoresBy("id", id);
    }

    @Override
    public Single<List<Store>> findStoresByIds(List<Integer> ids) {
        return findStoresBy("id", ids);
    }
}