package com.example.mypc.fastfoodfinder.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.model.Store.Store;
import com.example.mypc.fastfoodfinder.model.Store.StoreDataSource;
import com.example.mypc.fastfoodfinder.utils.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class SplashActivity extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1500;
    SharedPreferences mSharedPreferences;

    public static final String KEY_FIRST_RUN = "firstRun";
    public boolean isFirstRun = false;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Realm.init(SplashActivity.this);
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mSharedPreferences = getSharedPreferences("com.example.mypc.fastfoodfinder", MODE_PRIVATE);
        isFirstRun = mSharedPreferences.getBoolean(KEY_FIRST_RUN, true);

        //Else
        if (isFirstRun) {
            readDataFromFirebase(new OnGetDataListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(DataSnapshot data) {
                    mSharedPreferences.edit().putBoolean(KEY_FIRST_RUN, false).apply();
                    if (mFirebaseAuth != null) {
                        mFirebaseAuth.signOut();
                        Toast.makeText(SplashActivity.this, R.string.update_database_successfull, Toast.LENGTH_SHORT).show();
                    }
                    startMyActivity(LoginActivity.class);
                }

                @Override
                public void onFailed(DatabaseError databaseError) {
                    Toast.makeText(SplashActivity.this, R.string.update_database_failed + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        else
        {
            startMyActivity(MainActivity.class);
        }
    }

    private void startMyActivity(Class<?> activity)
    {
        Intent intent = new Intent(SplashActivity.this, activity);
        startActivity(intent);
        finish();
    }

    public interface OnGetDataListener {
        public void onStart();
        public void onSuccess(DataSnapshot data);
        public void onFailed(DatabaseError databaseError);
    }

    public void readDataFromFirebase(final OnGetDataListener listener) {
        listener.onStart();
        // Do first run stuff here then set 'firstrun' as false
        // using the following line to edit/commit prefs

        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in
            mFirebaseAuth.signInWithEmailAndPassword("store_downloader@fastfoodfinder.com","123456789")
                    .addOnCompleteListener(SplashActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Firebase instance variables
                                // Get a reference to our posts
                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference ref = database.getReference(Constant.CHILD_STORES_LOCATION);

                                // Attach a listener to read the data at our posts reference
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        List<Store> storeList = new ArrayList<Store>();

                                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                                            for (DataSnapshot storeLocation : child.child(Constant.CHILD_MARKERS_ADD).getChildren()) {
                                                Store store = storeLocation.getValue(Store.class);
                                                store.setType(getStoreType(child.getKey()));
                                                storeList.add(store);
                                            }
                                        }

                                        saveStoresLocation(storeList);
                                        listener.onSuccess(dataSnapshot);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w("MAPP", "The read failed: " + databaseError.getCode());
                                        listener.onFailed(databaseError);
                                    }
                                });
                            } else {
                                Log.w("MAPP", "Sign In to Get data ", task.getException());
                                Toast.makeText(SplashActivity.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else {
            Log.d("MAPP", "Sign In w Email and Password outside");
        }
    }

    private int getStoreType(String key) {
        switch (key)
        {
            case "circle_k":
                return Constant.TYPE_CIRCLE_K;
            case "mini_stop":
                return Constant.TYPE_MINI_STOP;
            case "family_mart":
                return Constant.TYPE_FAMILY_MART;
            case "bsmart":
                return Constant.TYPE_BSMART;
            case "shop_n_go":
                return Constant.TYPE_SHOP_N_GO;

            default:
                return Constant.TYPE_CIRCLE_K;
        }
    }

    private void saveStoresLocation(List<Store> storeList) {
        StoreDataSource.store(storeList);
    }
}
