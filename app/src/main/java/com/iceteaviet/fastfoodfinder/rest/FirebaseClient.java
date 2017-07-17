package com.iceteaviet.fastfoodfinder.rest;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iceteaviet.fastfoodfinder.model.Store.Store;
import com.iceteaviet.fastfoodfinder.model.Store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.model.Store.UserStoreList;
import com.iceteaviet.fastfoodfinder.model.User.User;
import com.iceteaviet.fastfoodfinder.utils.Constant;
import com.iceteaviet.fastfoodfinder.utils.DataUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tamdoan on 14/07/2017.
 */

public class FirebaseClient {
    private static FirebaseClient mInstance = null;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;
    private FirebaseDatabase mDatabase;

    private FirebaseClient() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public static FirebaseClient getInstance() {
        if (mInstance == null)
            mInstance = new FirebaseClient();
        return mInstance;
    }

    public void refresh() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public boolean isSignedIn() {
        return mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().isAnonymous();
    }

    public void addListenerForSingleUserValueEvent(String uid, final UserValueEventListener listener) {
        mDatabaseRef.child(Constant.CHILD_USERS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    listener.onDataChange(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MAPP", "Failed to get user data");
                listener.onCancelled(databaseError);
            }
        });
    }

    public void saveUserIfNotExists(final User user) {
        mDatabaseRef.child(Constant.CHILD_USERS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !dataSnapshot.hasChild(user.getUid())) {
                    //New account -> save
                    setUserData(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("MAPP", "Error checking user exists");
            }
        });
    }

    public void saveUserStoreListIfNotExists(String uid, List<UserStoreList> storeList) {
        mDatabaseRef.child(Constant.CHILD_USERS)
                .child(uid)
                .child(Constant.CHILD_USERS_STORE_LIST)
                .setValue(storeList);
    }

    public void setUserData(User user) {
        mDatabaseRef.child(Constant.CHILD_USERS).child(user.getUid()).setValue(user);
    }

    public void addListenerForSingleStoreListValueEvent(final StoreListValueEventListener listener) {
        mDatabaseRef.child(Constant.CHILD_STORES_LOCATION).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onDataChange(parseDataFromFirebase(dataSnapshot));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MAPP", "The read failed: " + databaseError.getMessage());
                listener.onCancelled(databaseError);
            }
        });
    }

    private List<Store> parseDataFromFirebase(DataSnapshot dataSnapshot) {
        List<Store> storeList = new ArrayList<Store>();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            for (DataSnapshot storeLocation : child.child(Constant.CHILD_MARKERS_ADD).getChildren()) {
                Store store = storeLocation.getValue(Store.class);
                store.setType(DataUtils.getStoreType(child.getKey()));
                storeList.add(store);
            }
        }

        return storeList;
    }

    public void addFavouriteStoresEventListener(String uid, final ChildStoreEventListener listener) {
        mDatabaseRef.child(Constant.CHILD_USERS)
                .child(uid)
                .child(Constant.CHILD_USERS_STORE_LIST)
                .child(String.valueOf(UserStoreList.ID_FAVOURITE))
                .child("storeIdList").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists())
                    listener.onChildAdded(getStoreFrom(dataSnapshot), s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists())
                    listener.onChildChanged(getStoreFrom(dataSnapshot), s);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    listener.onChildRemoved(getStoreFrom(dataSnapshot));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists())
                    listener.onChildMoved(getStoreFrom(dataSnapshot), s);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onCancelled(databaseError);
            }
        });
    }

    private Store getStoreFrom(DataSnapshot snapshot) {
        Integer storeId = snapshot.getValue(Integer.class);
        if (storeId != null)
            return StoreDataSource.getStoresById(storeId).get(0); //Cannot have multiple stores with same id !!!
        return null;
    }

    public interface ChildStoreEventListener {
        void onChildAdded(Store store, String var2);

        void onChildChanged(Store store, String var2);

        void onChildRemoved(Store store);

        void onChildMoved(Store store, String var2);

        void onCancelled(DatabaseError var1);
    }

    public interface UserValueEventListener {
        void onDataChange(User user);

        void onCancelled(DatabaseError error);
    }

    public interface StoreListValueEventListener {
        void onDataChange(List<Store> storeList);

        void onCancelled(DatabaseError error);
    }
}
