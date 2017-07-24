package com.iceteaviet.fastfoodfinder.activity.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.iceteaviet.fastfoodfinder.BuildConfig;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.model.store.Store;
import com.iceteaviet.fastfoodfinder.model.store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.model.user.User;
import com.iceteaviet.fastfoodfinder.network.FirebaseClient;
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
            FirebaseClient.getInstance().readDataFromFirebase(this, new FirebaseClient.OnGetDataListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onSuccess(List<Store> data) {
                    mSharedPreferences.edit().putBoolean(KEY_FIRST_RUN, false).apply();
                    StoreDataSource.setData(data);
                    Toast.makeText(SplashActivity.this, R.string.update_database_successfull, Toast.LENGTH_SHORT).show();
                    startMyActivity(LoginActivity.class);
                }

                @Override
                public void onFailed(String errorMessage) {
                    Toast.makeText(SplashActivity.this, R.string.update_database_failed + errorMessage, Toast.LENGTH_SHORT).show();
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

    private void restartActivity() {
        startActivity(getIntent());
        finish();
    }
}
