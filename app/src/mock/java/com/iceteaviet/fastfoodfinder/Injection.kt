package com.iceteaviet.fastfoodfinder

import android.content.Context
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.FakeDataManager
import com.iceteaviet.fastfoodfinder.data.auth.ClientAuth
import com.iceteaviet.fastfoodfinder.data.auth.FakeFirebaseClientAuth
import com.iceteaviet.fastfoodfinder.data.domain.prefs.AppPreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.prefs.PreferencesRepository
import com.iceteaviet.fastfoodfinder.data.domain.routing.AppMapsRoutingRepository
import com.iceteaviet.fastfoodfinder.data.domain.routing.MapsRoutingRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.AppStoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.store.StoreRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.AppUserRepository
import com.iceteaviet.fastfoodfinder.data.domain.user.UserRepository
import com.iceteaviet.fastfoodfinder.data.local.db.store.FakeStoreDAO
import com.iceteaviet.fastfoodfinder.data.local.db.store.StoreDataSource
import com.iceteaviet.fastfoodfinder.data.local.db.user.FakeUserDAO
import com.iceteaviet.fastfoodfinder.data.local.db.user.UserDataSource
import com.iceteaviet.fastfoodfinder.data.local.prefs.AppPreferencesHelper
import com.iceteaviet.fastfoodfinder.data.local.prefs.FakePreferencesHelper
import com.iceteaviet.fastfoodfinder.data.local.prefs.PreferencesHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.FakeGoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.MapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.FakeFirebaseStoreApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.StoreApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.user.FakeFirebaseUserApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.user.UserApiHelper
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider

/**
 * Enables injection of mock implementations at compile time. This is useful for testing, since it allows us to use
 * a fake instance of the class to isolate the dependencies and run a test hermetically.
 */
object Injection {
    fun provideDataManager(): DataManager {
        return FakeDataManager(provideContext(), provideStoreRepository(), provideUserRepository(),
                provideAuthClient(),
                provideRoutingRepository(), providePreferenceRepository())
    }

    fun provideSchedulerProvider(): SchedulerProvider {
        return TrampolineSchedulerProvider()
    }

    fun provideContext(): Context {
        return App.getContext()
    }

    fun provideStoreRepository(): StoreRepository {
        return AppStoreRepository(provideRemoteStoreDataSource(), provideLocalStoreDataSource())
    }

    fun provideUserRepository(): UserRepository {
        return AppUserRepository(provideRemoteUserDataSource(), provideLocalUserDataSource())
    }

    fun provideLocalStoreDataSource(): StoreDataSource {
        return FakeStoreDAO()
    }

    fun provideLocalUserDataSource(): UserDataSource {
        return FakeUserDAO()
    }

    fun provideRemoteStoreDataSource(): StoreApiHelper {
        return FakeFirebaseStoreApiHelper()
    }

    fun provideRemoteUserDataSource(): UserApiHelper {
        return FakeFirebaseUserApiHelper()
    }

    fun provideRoutingRepository(): MapsRoutingRepository {
        return AppMapsRoutingRepository(provideRoutingApiHelper())
    }

    fun provideRoutingApiHelper(): MapsRoutingApiHelper {
        return FakeGoogleMapsRoutingApiHelper()
    }

    fun providePreferenceRepository(): PreferencesRepository {
        return AppPreferencesRepository(providePreferenceHelper())
    }

    fun providePreferenceHelper(): PreferencesHelper {
        return AppPreferencesHelper(FakePreferencesHelper())
    }

    fun provideAuthClient(): ClientAuth {
        return FakeFirebaseClientAuth()
    }
}