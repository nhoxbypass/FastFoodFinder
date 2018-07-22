package com.iceteaviet.fastfoodfinder.ui.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.iceteaviet.fastfoodfinder.App;
import com.iceteaviet.fastfoodfinder.BuildConfig;
import com.iceteaviet.fastfoodfinder.R;
import com.iceteaviet.fastfoodfinder.data.DataManager;
import com.iceteaviet.fastfoodfinder.data.remote.store.model.Store;
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User;
import com.iceteaviet.fastfoodfinder.ui.login.LoginActivity;
import com.iceteaviet.fastfoodfinder.ui.main.MainActivity;
import com.iceteaviet.fastfoodfinder.utils.NetworkUtils;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class SplashActivity extends AppCompatActivity {

    public static final String KEY_FIRST_RUN = "firstRun";
    private final int SPLASH_DISPLAY_LENGTH = 1000; //Duration of wait
    public boolean isFirstRun = false;
    private SharedPreferences mSharedPreferences;
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        dataManager = App.getDataManager();
        mSharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        isFirstRun = mSharedPreferences.getBoolean(KEY_FIRST_RUN, true);

        //Check if is first run
        if (isFirstRun) {
            //First run
            //Download data from Firebase and ic_store in Realm
            dataManager.readDataFromFirebase(this)
                    .subscribe(new SingleObserver<List<Store>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(List<Store> storeList) {
                            dataManager.signOut();
                            mSharedPreferences.edit().putBoolean(KEY_FIRST_RUN, false).apply();
                            dataManager.getLocalStoreDataSource().setStores(storeList);
                            Toast.makeText(SplashActivity.this, R.string.update_database_successfull, Toast.LENGTH_SHORT).show();
                            startMyActivity(LoginActivity.class);
                        }

                        @Override
                        public void onError(Throwable e) {
                            dataManager.signOut();
                            Toast.makeText(SplashActivity.this, R.string.update_database_failed + e.getMessage(), Toast.LENGTH_SHORT).show();
                            restartActivity();
                        }
                    });
        } else {
            if (dataManager.isSignedIn()) {
                //User still signed in
                if (NetworkUtils.isNetworkReachable(this)) {
                    if (NetworkUtils.isInternetConnected()) { // TODO: 4 seconds
                        dataManager.getUserDataSource().getUser(dataManager.getCurrentUserUid())
                                .subscribe(new SingleObserver<User>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(User user) {
                                        dataManager.setCurrentUser(user);
                                        startMyActivity(MainActivity.class);
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        e.printStackTrace();
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
