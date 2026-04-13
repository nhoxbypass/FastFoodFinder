package com.iceteaviet.fastfoodfinder.di

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
import com.iceteaviet.fastfoodfinder.data.local.db.user.FakeUserDAO
import com.iceteaviet.fastfoodfinder.data.local.prefs.AppPreferencesHelper
import com.iceteaviet.fastfoodfinder.data.local.prefs.FakePreferencesHelper
import com.iceteaviet.fastfoodfinder.data.remote.routing.FakeGoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.FakeFirebaseStoreApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.user.FakeFirebaseUserApiHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideStoreRepository(): StoreRepository {
        val remote = FakeFirebaseStoreApiHelper()
        val local = FakeStoreDAO()
        return AppStoreRepository(remote, local)
    }

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository {
        val remote = FakeFirebaseUserApiHelper()
        val local = FakeUserDAO()
        return AppUserRepository(remote, local)
    }

    @Provides
    @Singleton
    fun provideRoutingRepository(): MapsRoutingRepository {
        val apiHelper = FakeGoogleMapsRoutingApiHelper()
        return AppMapsRoutingRepository(apiHelper)
    }

    @Provides
    @Singleton
    fun providePreferenceRepository(): PreferencesRepository {
        val helper = AppPreferencesHelper(FakePreferencesHelper())
        return AppPreferencesRepository(helper)
    }

    @Provides
    @Singleton
    fun provideAuthClient(): ClientAuth {
        return FakeFirebaseClientAuth()
    }
}
