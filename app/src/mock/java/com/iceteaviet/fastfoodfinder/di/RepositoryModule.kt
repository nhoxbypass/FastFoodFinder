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
import com.iceteaviet.fastfoodfinder.data.local.db.store.StoreDao
import com.iceteaviet.fastfoodfinder.data.local.db.user.FakeUserDAO
import com.iceteaviet.fastfoodfinder.data.local.db.user.UserDao
import android.content.Context
import com.iceteaviet.fastfoodfinder.data.local.prefs.AppPreferencesHelper
import com.iceteaviet.fastfoodfinder.data.local.prefs.AppPreferencesWrapper
import com.iceteaviet.fastfoodfinder.data.remote.routing.FakeGoogleMapsRoutingApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.FakeFirebaseStoreApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.store.StoreApiHelper
import com.iceteaviet.fastfoodfinder.data.remote.user.FakeFirebaseUserApiHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideStoreApiHelper(@ApplicationContext context: Context): StoreApiHelper {
        return FakeFirebaseStoreApiHelper(context)
    }

    @Provides
    @Singleton
    fun provideStoreRepository(storeApiHelper: StoreApiHelper, storeDao: StoreDao): StoreRepository {
        return AppStoreRepository(storeApiHelper, storeDao)
    }

    @Provides
    @Singleton
    fun provideUserRepository(@ApplicationContext context: Context): UserRepository {
        val remote = FakeFirebaseUserApiHelper(context)
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
    fun providePreferenceRepository(@ApplicationContext context: Context): PreferencesRepository {
        val wrapper = AppPreferencesWrapper(context.getSharedPreferences(AppPreferencesWrapper.PREFS_NAME, Context.MODE_PRIVATE))
        val helper = AppPreferencesHelper(wrapper)
        return AppPreferencesRepository(helper)
    }

    @Provides
    @Singleton
    fun provideAuthClient(@ApplicationContext context: Context): ClientAuth {
        return FakeFirebaseClientAuth(context)
    }
}
