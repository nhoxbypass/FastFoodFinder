package com.iceteaviet.fastfoodfinder.di

import android.content.Context
import com.iceteaviet.fastfoodfinder.utils.ui.AppNotiManager
import com.iceteaviet.fastfoodfinder.utils.ui.NotiManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotiManager(@ApplicationContext context: Context): NotiManager {
        return AppNotiManager(context)
    }
}
