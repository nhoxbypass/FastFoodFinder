package com.iceteaviet.fastfoodfinder.di

import android.content.Context
import com.iceteaviet.fastfoodfinder.service.eventbus.core.IBus
import com.iceteaviet.fastfoodfinder.service.eventbus.core.RobotBus
import com.iceteaviet.fastfoodfinder.utils.rx.AppSchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.ui.AppNotiManager
import com.iceteaviet.fastfoodfinder.utils.ui.NotiManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.greenrobot.eventbus.EventBus
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideEventBus(): IBus {
        return RobotBus(EventBus.getDefault())
    }

    @Provides
    @Singleton
    fun provideNotiManager(@ApplicationContext context: Context): NotiManager {
        return AppNotiManager(context)
    }
}
