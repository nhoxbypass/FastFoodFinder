package com.iceteaviet.fastfoodfinder.data.remote.user;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.iceteaviet.fastfoodfinder.data.domain.user.UserDataSource;
import com.iceteaviet.fastfoodfinder.data.local.store.LocalStoreRepository;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreEvent;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.UserStoreList;
import com.iceteaviet.fastfoodfinder.utils.Constant;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

/**
 * Created by tom on 7/15/18.
 */
public class UserRepository implements UserDataSource {
    private static final String TAG = UserRepository.class.getSimpleName();

    private DatabaseReference databaseRef;
    private LocalStoreRepository localStoreRepository;

    public UserRepository(DatabaseReference reference, LocalStoreRepository localStoreRepository) {
        databaseRef = reference;
        this.localStoreRepository = localStoreRepository;
    }

    @Override
    public void insertOrUpdate(String name, String email, String photoUrl, String uid, List<UserStoreList> storeLists) {
        User user = new User(name, email, photoUrl, uid, storeLists);
        insertOrUpdate(user);
    }


    @Override
    public void insertOrUpdate(User user) {
        databaseRef.child(Constant.CHILD_USERS)
                .child(user.getUid())
                .setValue(user);
    }

    @Override
    public void updateStoreListForUser(String uid, List<UserStoreList> storeList) {
        databaseRef.child(Constant.CHILD_USERS)
                .child(uid)
                .child(Constant.CHILD_USERS_STORE_LIST)
                .setValue(storeList);
    }

    @Override
    public Single<User> getUser(final String uid) {
        return Single.create(new SingleOnSubscribe<User>() {
            @Override
            public void subscribe(final SingleEmitter<User> emitter) {
                databaseRef.child(Constant.CHILD_USERS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);
                            emitter.onSuccess(user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Failed to get user data");
                        emitter.onError(databaseError.toException());
                    }
                });
            }
        });
    }

    @Override
    public Single<Boolean> isUserExists(final String uid) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final SingleEmitter<Boolean> emitter) {
                databaseRef.child(Constant.CHILD_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists() || !dataSnapshot.hasChild(uid)) {
                            // Not exists
                            emitter.onSuccess(false);
                        } else {
                            emitter.onSuccess(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(TAG, "Error checking user exists");
                    }
                });
            }
        });
    }

    @Override
    public Observable<UserStoreEvent> subscribeFavouriteStoresOfUser(final String uid) {
        return Observable.create(new ObservableOnSubscribe<UserStoreEvent>() {
            @Override
            public void subscribe(final ObservableEmitter<UserStoreEvent> emitter) {
                databaseRef.child(Constant.CHILD_USERS)
                        .child(uid)
                        .child(Constant.CHILD_USERS_STORE_LIST)
                        .child(String.valueOf(UserStoreList.ID_FAVOURITE))
                        .child("storeIdList").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists())
                            emitter.onNext(new UserStoreEvent(getStoreFrom(dataSnapshot.getValue(Integer.class)), UserStoreEvent.ACTION_ADDED));
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists())
                            emitter.onNext(new UserStoreEvent(getStoreFrom(dataSnapshot.getValue(Integer.class)), UserStoreEvent.ACTION_CHANGED));
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists())
                            emitter.onNext(new UserStoreEvent(getStoreFrom(dataSnapshot.getValue(Integer.class)), UserStoreEvent.ACTION_REMOVED));
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists())
                            emitter.onNext(new UserStoreEvent(getStoreFrom(dataSnapshot.getValue(Integer.class)), UserStoreEvent.ACTION_MOVED));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        emitter.onError(databaseError.toException());
                    }
                });
            }
        });
    }

    private Store getStoreFrom(Integer storeId) {
        Store store = null;

        if (storeId != null) {
            store = localStoreRepository.findStoresById(storeId)
                    .blockingGet()
                    .get(0);
        }

        return store;
    }
}
