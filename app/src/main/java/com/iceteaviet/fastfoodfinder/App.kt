package com.iceteaviet.fastfoodfinder

import androidx.multidex.MultiDexApplication
import com.google.firebase.database.FirebaseDatabase
import com.iceteaviet.fastfoodfinder.data.AppDataManager
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.local.store.LocalStoreRepository
import com.iceteaviet.fastfoodfinder.data.local.user.LocalUserRepository
import com.iceteaviet.fastfoodfinder.data.prefs.AppPreferencesHelper
import com.iceteaviet.fastfoodfinder.data.remote.FirebaseClientAuth
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.FirebaseStoreRepository
import com.iceteaviet.fastfoodfinder.data.remote.user.FirebaseUserRepository
import com.iceteaviet.fastfoodfinder.utils.initLogger

/**
 * Created by tom on 7/15/18.
 */
class App : MultiDexApplication() {

    companion object {
        private lateinit var dataManager: DataManager
        lateinit var PACKAGE_NAME: String

        @JvmStatic
        fun getDataManager(): DataManager {
            return dataManager
        }
    }

    override fun onCreate() {
        super.onCreate()

        PACKAGE_NAME = applicationContext.packageName
        initLogger()

        val localStoreDataSource = LocalStoreRepository()
        val remoteStoreDataSource = FirebaseStoreRepository(FirebaseDatabase.getInstance().reference)

        val localUserDataSource = LocalUserRepository()
        val remoteUserDataSource = FirebaseUserRepository(FirebaseDatabase.getInstance().reference)

        val mapsRoutingApiHelper = GoogleMapsRoutingApiHelper(getString(R.string.google_maps_browser_key))
        val clientAuth = FirebaseClientAuth()
        val preferencesHelper = AppPreferencesHelper(this)

        dataManager = AppDataManager(this, localStoreDataSource, remoteStoreDataSource, clientAuth,
                localUserDataSource, remoteUserDataSource,
                mapsRoutingApiHelper, preferencesHelper)
    }
}
