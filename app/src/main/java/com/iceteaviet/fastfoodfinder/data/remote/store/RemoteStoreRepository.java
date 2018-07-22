package com.iceteaviet.fastfoodfinder.data.remote.store;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.iceteaviet.fastfoodfinder.data.base.store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.utils.Constant;
import com.iceteaviet.fastfoodfinder.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * Created by tom on 7/17/18.
 */
public class RemoteStoreRepository implements StoreDataSource {
    private static final String TAG = RemoteStoreRepository.class.getSimpleName();

    private DatabaseReference databaseRef;

    public RemoteStoreRepository(DatabaseReference reference) {
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
                databaseRef.child(Constant.CHILD_STORES_LOCATION).addListenerForSingleValueEvent(new ValueEventListener() {
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

    private List<Store> parseDataFromFirebase(DataSnapshot dataSnapshot) {
        List<Store> storeList = new ArrayList<>();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            for (DataSnapshot storeLocation : child.child(Constant.CHILD_MARKERS_ADD).getChildren()) {
                Store store = storeLocation.getValue(Store.class);
                store.setType(DataUtils.getStoreType(child.getKey()));
                storeList.add(store);
            }
        }

        return storeList;
    }
}
