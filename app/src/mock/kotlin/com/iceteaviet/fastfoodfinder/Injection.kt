package com.iceteaviet.fastfoodfinder

import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.FakeDataManager
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.auth.FakeFirebaseClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreDataSource
import com.iceteaviet.fastfoodfinder.data.domain.user.UserDataSource
import com.iceteaviet.fastfoodfinder.data.local.store.FakeLocalStoreRepository
import com.iceteaviet.fastfoodfinder.data.local.user.FakeLocalUserRepository
import com.iceteaviet.fastfoodfinder.data.prefs.FakePreferencesHelper
import com.iceteaviet.fastfoodfinder.data.prefs.PreferencesHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.FakeGoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.FakeFirebaseStoreRepository
import com.iceteaviet.fastfoodfinder.data.remote.user.FakeFirebaseUserRepository

/**
 * Enables injection of mock implementations at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
object Injection {
    fun provideDataManager(): DataManager {
        return FakeDataManager(provideLocalStoreDataSource(), provideRemoteStoreDataSource(),
                provideAuthClient(),
                provideLocalUserDataSource(), provideRemoteUserDataSource(),
                provideRoutingApiHelper(), providePreferenceHelper())
    }

    fun provideLocalStoreDataSource(): StoreDataSource {
        return FakeLocalStoreRepository()
    }

    fun provideLocalUserDataSource(): UserDataSource {
        return FakeLocalUserRepository()
    }

    fun provideRemoteStoreDataSource(): StoreDataSource {
        return FakeFirebaseStoreRepository()
    }

    fun provideRemoteUserDataSource(): UserDataSource {
        return FakeFirebaseUserRepository()
    }

    fun provideRoutingApiHelper(): MapsRoutingApiHelper {
        return FakeGoogleMapsRoutingApiHelper()
    }

    fun providePreferenceHelper(): PreferencesHelper {
        return FakePreferencesHelper()
    }

    fun provideAuthClient(): ClientAuth {
        return FakeFirebaseClientAuth()
    }
}