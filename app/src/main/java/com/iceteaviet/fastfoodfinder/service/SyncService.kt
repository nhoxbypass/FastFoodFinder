package com.iceteaviet.fastfoodfinder.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

/**
 * Created by tom on 8/7/18.
 */
class SyncService : Service() {
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, SyncService::class.java)
        }

        fun start(context: Context) {
            context.startService(getStartIntent(context))
        }

        fun stop(context: Context) {
            context.stopService(getStartIntent(context))
        }
    }
}