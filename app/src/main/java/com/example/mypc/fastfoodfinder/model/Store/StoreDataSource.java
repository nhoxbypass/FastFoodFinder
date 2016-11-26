package com.example.mypc.fastfoodfinder.model.Store;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by nhoxb on 11/20/2016.
 */
public class StoreDataSource {
    public void store(final List<Store> storeList)
    {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(StoreEntity.class)
                        .findAll()
                        .deleteAllFromRealm();

                for (int i = 0; i < storeList.size(); i++)
                {
                    StoreEntity storeEntity = realm.createObject(StoreEntity.class);
                    storeEntity.map(storeList.get(i));
                }
            }
        });
        realm.close();
    }

    public List<Store> getAllObjects()
    {
        Realm realm = Realm.getDefaultInstance();
        List<Store> storeList = new ArrayList<>();
        RealmResults<StoreEntity> results = realm.where(StoreEntity.class).findAll();

        for (int i = 0; i < results.size(); i++)
        {
            storeList.add(new Store(results.get(i)));
        }

        realm.close();

        return storeList;
    }

    public static List<Store> getStoreInBounds(double minLat, double minLng, double maxLat, double maxLng)
    {
        Realm realm = Realm.getDefaultInstance();
        List<Store> storeList = new ArrayList<>();
        // Build the query looking at all users:
        RealmQuery<StoreEntity> query = realm.where(StoreEntity.class);

        // Add query conditions:
        query.between("latitude",minLat,maxLat);
        query.between("longitude", minLng, maxLng);

        // Execute the query:
        RealmResults<StoreEntity> result = query.findAll();

        for (int i = 0; i < result.size(); i++)
        {
            Store store = new Store(result.get(i));
            storeList.add(store);
        }

        return storeList;
    }
}
