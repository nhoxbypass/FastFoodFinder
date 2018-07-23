package com.iceteaviet.fastfoodfinder.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.iceteaviet.fastfoodfinder.App;
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
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        dataManager = App.getDataManager();

        if (dataManager.getPreferencesHelper().getAppLaunchFirstTime()
                || dataManager.getPreferencesHelper().getNumberOfStores() == 0) {
            // Download data from Firebase and store in Realm
            dataManager.readDataFromFirebase(this)
                    .subscribe(new SingleObserver<List<Store>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(List<Store> storeList) {
                            dataManager.signOut();

                            dataManager.getPreferencesHelper().setAppLaunchFirstTime(false);
                            dataManager.getPreferencesHelper().setNumberOfStores(storeList.size());
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
                // User still signed in
                if (NetworkUtils.isNetworkReachable(this)) {
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
                    Toast.makeText(this, "Network is unreachable!", Toast.LENGTH_SHORT).show();
                    startMyActivity(MainActivity.class);
                }
            } else {
                startMyActivity(MainActivity.class);
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