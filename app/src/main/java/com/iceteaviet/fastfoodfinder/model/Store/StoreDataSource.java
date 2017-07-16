package com.iceteaviet.fastfoodfinder.model.Store;

import com.iceteaviet.fastfoodfinder.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import io.realm.Case;
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
            return getStoresByType(Constant.TYPE_CIRCLE_K);
        } else if (queryString.equals("mini stop") || queryString.equals("logo_ministop_red")) {
            return getStoresByType(Constant.TYPE_MINI_STOP);
        } else if (queryString.equals("family mart") || queryString.equals("logo_familymart_red")) {
            return getStoresByType(Constant.TYPE_FAMILY_MART);
        } else if (queryString.equals("shop and go") || queryString.equals("shopandgo") || queryString.equals("shop n go")) {
            return getStoresByType(Constant.TYPE_SHOP_N_GO);
        } else if (queryString.equals("logo_bsmart_red") || queryString.equals("b smart") || queryString.equals("bs mart") || queryString.equals("bmart") || queryString.equals("b'smart") || queryString.equals("b's mart")) {
            return getStoresByType(Constant.TYPE_BSMART);
        } else {
            //Cant determine
            //Quite hard to implement
            return getStoresByCustomAddress(normalizeDistrictQuery(queryString));
        }
    }

    public static List<Store> getStoresByCustomAddress(List<String> customQuerySearch) {
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
        return storeList;
    }

    private static List<String> normalizeDistrictQuery(String queryString) {
        List<String> result = new ArrayList<>();
        queryString = queryString.toLowerCase().trim();

        if (queryString.equals("gò vấp") || queryString.equals("go vap") || queryString.equals("govap")) {
            result.add("Gò Vấp");
            result.add("Go Vap");
        } else if (queryString.equals("tân bình") || queryString.equals("tan binh") || queryString.equals("tanbinh")) {
            result.add("Tân Bình");
            result.add("Tan Binh");
        } else if (queryString.equals("tân phú") || queryString.equals("tan phu") || queryString.equals("tanphu")) {
            result.add("Tân Phú");
            result.add("Tan Phu");
        } else if (queryString.equals("bình thạnh") || queryString.equals("binh thanh") || queryString.equals("binhthanh")) {
            result.add("Bình Thạnh");
            result.add("Binh Thanh");
        } else if (queryString.equals("phú nhuận") || queryString.equals("phu nhuan") || queryString.equals("phunhuan")) {
            result.add("Phú Nhuận");
            result.add("Phu Nhuan");
        } else if (queryString.equals("quận 9") || queryString.equals("quan 9")) {
            result.add("Quận 9");
            result.add("Quan 9");
            result.add("District 9");
        } else if (queryString.equals("quận 1") || queryString.equals("quan 1")) {
            result.add("Quận 1");
            result.add("Quan 1");
            result.add("District 1");
        } else if (queryString.equals("quận 2") || queryString.equals("quan 2")) {
            result.add("Quận 2");
            result.add("Quan 2");
            result.add("District 2");
        } else if (queryString.equals("quận 3") || queryString.equals("quan 3")) {
            result.add("Quận 3");
            result.add("Quan 3");
            result.add("District 3");
        } else if (queryString.equals("quận 4") || queryString.equals("quan 4")) {
            result.add("Quận 4");
            result.add("Quan 4");
            result.add("District 4");
        } else if (queryString.equals("quận 5") || queryString.equals("quan 5")) {
            result.add("Quận 5");
            result.add("Quan 5");
            result.add("District 5");
        } else if (queryString.equals("quận 6") || queryString.equals("quan 6")) {
            result.add("Quận 6");
            result.add("Quan 6");
            result.add("District 6");
        } else if (queryString.equals("quận 7") || queryString.equals("quan 7")) {
            result.add("Quận 7");
            result.add("Quan 7");
            result.add("District 7");
        } else if (queryString.equals("quận 8") || queryString.equals("quan 8")) {
            result.add("Quận 8");
            result.add("Quan 8");
            result.add("District 8");
        } else if (queryString.equals("quận 10") || queryString.equals("quan 10")) {
            result.add("Quận 10");
            result.add("Quan 10");
            result.add("District 10");
        } else if (queryString.equals("quận 11") || queryString.equals("quan 11")) {
            result.add("Quận 11");
            result.add("Quan 11");
            result.add("District 11");
        } else if (queryString.equals("quận 12") || queryString.equals("quan 12")) {
            result.add("Quận 12");
            result.add("Quan 12");
            result.add("District 12");
        }

        return result;
    }

    private static List<Store> getStoresBy(String key, int value) {
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

        return storeList;
    }

    private static List<Store> getStoresBy(String key, List<Integer> values) {
        if (values.isEmpty())
            return new ArrayList<>();

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

        return storeList;
    }

    public static List<Store> getStoresByType(int type) {
        return getStoresBy("type", type);
    }


    public static List<Store> getStoresById(int id) {
        return getStoresBy("id", id);
    }

    public static List<Store> getStoresById(List<Integer> ids) {
        return getStoresBy("id", ids);
    }
}