package com.iceteaviet.fastfoodfinder.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Nullable
import com.iceteaviet.fastfoodfinder.utils.d

/**
 * Created by tom on 8/7/18.
 */
class SyncService : Service() {

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        d(TAG, "SyncService started")
        return Service.START_STICKY
    }

    override fun onDestroy() {
        d(TAG, "SyncService stopped")
        super.onDestroy()
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {

        private val TAG = "SyncService"

        //private DataManager dataManager;

        fun getStartIntent(context: Context): Intent {
            return Intent(context, SyncService::class.java)
        }

        fun start(context: Context) {
            val starter = Intent(context, SyncService::class.java)
            context.startService(starter)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, SyncService::class.java))
        }
    }
}
