package com.iceteaviet.fastfoodfinder.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseError;
import com.iceteaviet.fastfoodfinder.BuildConfig;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.model.Store.Store;
import com.iceteaviet.fastfoodfinder.model.Store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.model.User.User;
import com.iceteaviet.fastfoodfinder.rest.FirebaseClient;
import com.iceteaviet.fastfoodfinder.utils.NetworkUtils;

import java.util.List;

import io.realm.Realm;

public class SplashActivity extends AppCompatActivity {

    public static final String KEY_FIRST_RUN = "firstRun";
    private final int SPLASH_DISPLAY_LENGTH = 1000; //Duration of wait
    public boolean isFirstRun = false;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Realm.init(SplashActivity.this);

        FirebaseClient.getInstance().refresh();
        mSharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        isFirstRun = mSharedPreferences.getBoolean(KEY_FIRST_RUN, true);

        //Check if is first run
        if (isFirstRun) {
            //First run
            //Download data from Firebase and ic_store in Realm
            readDataFromFirebase(new OnGetDataListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(List<Store> data) {
                    mSharedPreferences.edit().putBoolean(KEY_FIRST_RUN, false).apply();
                    if (FirebaseClient.getInstance().getAuth() != null) {
                        FirebaseClient.getInstance().getAuth().signOut();
                        Toast.makeText(SplashActivity.this, R.string.update_database_successfull, Toast.LENGTH_SHORT).show();
                    }
                    startMyActivity(LoginActivity.class);
                }

                @Override
                public void onFailed(DatabaseError databaseError) {
                    Toast.makeText(SplashActivity.this, R.string.update_database_failed + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    FirebaseClient.getInstance().getAuth().signOut();
                    restartActivity();
                }
            });
        } else {
            if (FirebaseClient.getInstance().isSignedIn()) {
                //User still signed in

                if (NetworkUtils.isNetworkReachable(this)) {
                    if (NetworkUtils.isInternetConnected()) {
                        FirebaseClient.getInstance().addListenerForSingleUserValueEvent(
                                FirebaseClient.getInstance().getAuth().getCurrentUser().getUid(),
                                new FirebaseClient.UserValueEventListener() {
                                    @Override
                                    public void onDataChange(User user) {
                                        User.currentUser = user;
                                        startMyActivity(MainActivity.class);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {

                                    }
                                });
                    } else {
                        Toast.makeText(this, "Network reachable but cannot access to the Internet!", Toast.LENGTH_SHORT).show();
                        startMyActivity(MainActivity.class);
                    }
                } else {
                    Toast.makeText(this, "Network is unreachable!", Toast.LENGTH_SHORT).show();
                    startMyActivity(MainActivity.class);
                }
            } else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startMyActivity(MainActivity.class);
                    }
                }, SPLASH_DISPLAY_LENGTH);
            }
        }
    }

    private void startMyActivity(Class<?> activity) {
        Intent intent = new Intent(SplashActivity.this, activity);
        startActivity(intent);
        finish();
    }

    public void readDataFromFirebase(final OnGetDataListener listener) {
        listener.onStart();
        // Do first run stuff here then set 'firstrun' as false
        // using the following line to edit/commit prefs

        if (FirebaseClient.getInstance().isSignedIn()) {
            // Not signed in
            FirebaseClient.getInstance().getAuth().signInWithEmailAndPassword("store_downloader@fastfoodfinder.com", "123456789")
                    .addOnCompleteListener(SplashActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseClient.getInstance().addListenerForSingleStoreListValueEvent(new FirebaseClient.StoreListValueEventListener() {
                                    @Override
                                    public void onDataChange(List<Store> storeList) {
                                        saveStoresLocation(storeList);
                                        listener.onSuccess(storeList);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        listener.onFailed(error);
                                    }
                                });
                            } else {
                                Log.w("MAPP", "Sign In to Get data ", task.getException());
                                Toast.makeText(SplashActivity.this, R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                                restartActivity();
                            }
                        }
                    });

        } else {
            Log.d("MAPP", "Already sign in but didn't get data");
            Toast.makeText(SplashActivity.this, R.string.update_database_failed, Toast.LENGTH_SHORT).show();
            FirebaseClient.getInstance().getAuth().signOut();
            restartActivity();
        }
    }

    private void restartActivity() {
        startActivity(getIntent());
        finish();
    }

    private void saveStoresLocation(List<Store> storeList) {
        StoreDataSource.store(storeList);
    }

    public interface OnGetDataListener {
        public void onStart();

        public void onSuccess(List<Store> data);

        public void onFailed(DatabaseError databaseError);
    }
}
