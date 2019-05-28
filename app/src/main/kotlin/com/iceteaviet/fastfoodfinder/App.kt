package com.iceteaviet.fastfoodfinder

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.location.GoogleLocationManager
import com.iceteaviet.fastfoodfinder.location.SystemLocationManager
import com.iceteaviet.fastfoodfinder.utils.initLogger

/**
 * Created by tom on 7/15/18.
 */
class App : MultiDexApplication() {

    companion object {
        private lateinit var dataManager: DataManager
        private lateinit var context: Context
        lateinit var PACKAGE_NAME: String

        @JvmStatic
        fun getDataManager(): DataManager {
            return dataManager
        }

        @JvmStatic
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
    }

    override fun onTerminate() {
        GoogleLocationManager.getInstance().terminate()
        SystemLocationManager.getInstance().terminate()
        super.onTerminate()
    }
}
