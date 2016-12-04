package com.example.mypc.fastfoodfinder.model.Store;

import com.example.mypc.fastfoodfinder.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by nhoxb on 11/20/2016.
 */
public class StoreDataSource {
    public static void store(final List<Store> storeList) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
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

    public static List<Store> getAllObjects() {
        Realm realm = Realm.getDefaultInstance();
        List<Store> storeList = new ArrayList<>();
        RealmResults<StoreEntity> results = realm.where(StoreEntity.class).findAll();

        for (int i = 0; i < results.size(); i++) {
            storeList.add(new Store(results.get(i)));
        }

        realm.close();

        return storeList;
    }

    public static List<Store> getStoreInBounds(double minLat, double minLng, double maxLat, double maxLng) {
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

        return storeList;
    }

    public static List<Store> getStore(String queryString) {
        queryString = queryString.toLowerCase().trim();
        if (queryString.equals("circle k") || queryString.equals("circlek")) {
            return getStore(Constant.TYPE_CIRCLE_K);
        } else if (queryString.equals("mini stop") || queryString.equals("logo_red_ministop")) {
            return getStore(Constant.TYPE_MINI_STOP);
        } else if (queryString.equals("family mart") || queryString.equals("logo_red_familymart")) {
            return getStore(Constant.TYPE_FAMILY_MART);
        } else if (queryString.equals("shop and go") || queryString.equals("shopandgo") || queryString.equals("shop n go")) {
            return getStore(Constant.TYPE_SHOP_N_GO);
        } else if (queryString.equals("logo_red_bsmart") || queryString.equals("b smart") || queryString.equals("bs mart") || queryString.equals("bmart") || queryString.equals("b'smart") || queryString.equals("b's mart")) {
            return getStore(Constant.TYPE_BSMART);
        } else {
            //Cant determine
            //Quite hard to implement
            return getCustomStore(queryString);
        }
    }

    public static List<Store> getCustomStore(String customQuerySearch) {
        Realm realm = Realm.getDefaultInstance();

        List<Store> storeList = new ArrayList<>();

        RealmQuery<StoreEntity> query = realm.where(StoreEntity.class);

        query.contains("title", customQuerySearch);
        query.or().contains("address", customQuerySearch);

        RealmResults<StoreEntity> results = query.findAll();

        int size = results.size();
        for (int i = 0; i < size; i++) {
            Store store = new Store(results.get(i));
            storeList.add(store);
        }

        realm.close();

        return storeList;
    }

    public static List<Store> getStore(int type) {
        Realm realm = Realm.getDefaultInstance();

        List<Store> storeList = new ArrayList<>();

        RealmQuery<StoreEntity> query = realm.where(StoreEntity.class);

        query.equalTo("type", type);

        RealmResults<StoreEntity> results = query.findAll();

        int size = results.size();
        for (int i = 0; i < size; i++) {
            Store store = new Store(results.get(i));
            storeList.add(store);
        }

        realm.close();

        return storeList;
    }
}