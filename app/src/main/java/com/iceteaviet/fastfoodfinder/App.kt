package com.iceteaviet.fastfoodfinder

import android.annotation.SuppressLint
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.location.SystemLocationManager
import com.iceteaviet.fastfoodfinder.utils.initLogger
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider

/**
 * Created by tom on 7/15/18.
 */
class App : MultiDexApplication() {

    companion object {
        private lateinit var dataManager: DataManager
        private lateinit var schedulerProvider: SchedulerProvider

        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
        lateinit var PACKAGE_NAME: String

        fun getDataManager(): DataManager {
            return dataManager
        }

        fun getSchedulerProvider(): SchedulerProvider {
            return schedulerProvider
        }

        fun getContext(): Context {
            return context
        }
    }

    override fun onCreate() {
        super.onCreate()

        PACKAGE_NAME = applicationContext.packageName
        context = applicationContext

        initLogger()

        dataManager = Injection.provideDataManager()
        schedulerProvider = Injection.provideSchedulerProvider()
        GoogleLocationManager.init(getContext())
        SystemLocationManager.init(getContext())
    }

    override fun onTerminate() {
        GoogleLocationManager.getInstance().terminate()
        SystemLocationManager.getInstance().terminate()
        super.onTerminate()
    }
}
