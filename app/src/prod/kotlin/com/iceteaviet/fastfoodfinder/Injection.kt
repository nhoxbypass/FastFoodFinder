package com.iceteaviet.fastfoodfinder

import com.google.firebase.database.FirebaseDatabase
import com.iceteaviet.fastfoodfinder.data.AppDataManager
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.auth.FirebaseClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreDataSource
import com.iceteaviet.fastfoodfinder.data.domain.user.UserDataSource
import com.iceteaviet.fastfoodfinder.data.local.store.LocalStoreRepository
import com.iceteaviet.fastfoodfinder.data.local.user.LocalUserRepository
import com.iceteaviet.fastfoodfinder.data.prefs.AppPreferencesHelper
import com.iceteaviet.fastfoodfinder.data.prefs.PreferencesHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.GoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.FirebaseStoreRepository
import com.iceteaviet.fastfoodfinder.data.remote.user.FirebaseUserRepository

/**
 * Enables injection of mock implementations at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
object Injection {
    fun provideDataManager(): DataManager {
        return AppDataManager(provideLocalStoreDataSource(), provideRemoteStoreDataSource(),
                provideAuthClient(),
                provideLocalUserDataSource(), provideRemoteUserDataSource(),
                provideRoutingApiHelper(), providePreferenceHelper())
    }

    fun provideLocalStoreDataSource(): StoreDataSource {
        return LocalStoreRepository()
    }

    fun provideLocalUserDataSource(): UserDataSource {
        return LocalUserRepository()
    }

    fun provideRemoteStoreDataSource(): StoreDataSource {
        return FirebaseStoreRepository(FirebaseDatabase.getInstance().reference)
    }

    fun provideRemoteUserDataSource(): UserDataSource {
        return FirebaseUserRepository(FirebaseDatabase.getInstance().reference)
    }

    fun provideRoutingApiHelper(): MapsRoutingApiHelper {
        return GoogleMapsRoutingApiHelper(App.getContext().getString(R.string.google_maps_browser_key))
    }

    fun providePreferenceHelper(): PreferencesHelper {
        return AppPreferencesHelper(App.getContext())
    }

    fun provideAuthClient(): ClientAuth {
        return FirebaseClientAuth()
    }
}