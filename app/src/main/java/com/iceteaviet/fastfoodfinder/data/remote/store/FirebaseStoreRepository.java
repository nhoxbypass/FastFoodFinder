package com.iceteaviet.fastfoodfinder.data.remote.store;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * Created by tom on 7/17/18.
 */
public class FirebaseStoreRepository implements StoreDataSource {
    private static final String TAG = FirebaseStoreRepository.class.getSimpleName();
    private static final String CHILD_STORES_LOCATION = "stores_location";
    private static final String CHILD_MARKERS_ADD = "markers_add";

    private DatabaseReference databaseRef;

    public FirebaseStoreRepository(DatabaseReference reference) {
        databaseRef = reference;
    }

    @Override
    public void setStores(List<Store> storeList) {

    }

    @Override
    public Single<List<Store>> getAllStores() {
        return Single.create(new SingleOnSubscribe<List<Store>>() {
            @Override
            public void subscribe(final SingleEmitter<List<Store>> emitter) {
                databaseRef.child(CHILD_STORES_LOCATION).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        emitter.onSuccess(parseDataFromFirebase(dataSnapshot));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "The read failed: " + databaseError.getMessage());
                        emitter.onError(databaseError.toException());
                    }
                });
            }
        });
    }

    @Override
    public Single<List<Store>> getStoreInBounds(double minLat, double minLng, double maxLat, double maxLng) {
        return null;
    }

    @Override
    public Single<List<Store>> findStores(String queryString) {
        return null;
    }

    @Override
    public Single<List<Store>> findStoresByCustomAddress(List<String> customQuerySearch) {
        return null;
    }

    @Override
    public Single<List<Store>> findStoresBy(String key, int value) {
        return null;
    }

    @Override
    public Single<List<Store>> findStoresBy(String key, List<Integer> values) {
        return null;
    }

    @Override
    public Single<List<Store>> findStoresByType(int type) {
        return null;
    }

    @Override
    public Single<List<Store>> findStoresById(int id) {
        return null;
    }

    @Override
    public Single<List<Store>> findStoresByIds(List<Integer> ids) {
        return null;
    }

    @Override
    public void deleteAllStores() {
        // TODO: Clear all stores
    }

    private List<Store> parseDataFromFirebase(DataSnapshot dataSnapshot) {
        List<Store> storeList = new ArrayList<>();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            for (DataSnapshot storeLocation : child.child(CHILD_MARKERS_ADD).getChildren()) {
                Store store = storeLocation.getValue(Store.class);
                store.setType(DataUtils.getStoreType(child.getKey()));
                storeList.add(store);
            }
        }

        return storeList;
    }
}
