package com.iceteaviet.fastfoodfinder;

import android.support.multidex.MultiDexApplication;

import com.google.firebase.database.FirebaseDatabase;
import com.iceteaviet.fastfoodfinder.data.AppDataManager;
import com.iceteaviet.fastfoodfinder.data.DataManager;
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreDataSource;
import com.iceteaviet.fastfoodfinder.data.domain.user.UserDataSource;
import com.iceteaviet.fastfoodfinder.data.local.store.LocalStoreRepository;
import com.iceteaviet.fastfoodfinder.data.local.user.LocalUserRepository;
import com.iceteaviet.fastfoodfinder.data.prefs.AppPreferencesHelper;
import com.iceteaviet.fastfoodfinder.data.prefs.PreferencesHelper;
import com.iceteaviet.fastfoodfinder.data.remote.ClientAuth;
import com.iceteaviet.fastfoodfinder.data.remote.FirebaseClientAuth;
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper;
import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper;
import com.iceteaviet.fastfoodfinder.data.remote.store.FirebaseStoreRepository;
import com.iceteaviet.fastfoodfinder.data.remote.user.FirebaseUserRepository;

import io.realm.Realm;

/**
 * Created by tom on 7/15/18.
 */
public class App extends MultiDexApplication {

    private static DataManager dataManager;
    public static String PACKAGE_NAME;

    public static DataManager getDataManager() {
        return dataManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        PACKAGE_NAME = getApplicationContext().getPackageName();

        LocalStoreRepository localStoreDataSource = new LocalStoreRepository();
        StoreDataSource remoteStoreDataSource = new FirebaseStoreRepository(FirebaseDatabase.getInstance().getReference());

        UserDataSource localUserDataSource = new LocalUserRepository();
        UserDataSource remoteUserDataSource = new FirebaseUserRepository(FirebaseDatabase.getInstance().getReference());

        MapsRoutingApiHelper mapsRoutingApiHelper = new GoogleMapsRoutingApiHelper(getString(R.string.google_maps_browser_key));
        ClientAuth clientAuth = new FirebaseClientAuth();
        PreferencesHelper preferencesHelper = new AppPreferencesHelper(this);

        dataManager = new AppDataManager(localStoreDataSource, remoteStoreDataSource, clientAuth,
                localUserDataSource, remoteUserDataSource,
                mapsRoutingApiHelper, preferencesHelper);
    }
}
