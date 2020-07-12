package com.iceteaviet.fastfoodfinder.utils.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.iceteaviet.fastfoodfinder.R
import com.iceteaviet.fastfoodfinder.androidext.getNotificationManager
import com.iceteaviet.fastfoodfinder.utils.getSplashScreenIntent

/**
 * Created by tom on 2019-07-07.
 */

class AppNotiManager constructor(private val context: Context) : NotiManager {
    override fun showStoreSyncStatusNotification(message: String, title: String) {
        val intent = getSplashScreenIntent(context).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        val notification = makeStatusNotification(message, title, DB_SYNC_NOTIFICATION_CHANNEL_ID,
                DB_SYNC_NOTIFICATION_CHANNEL_NAME, DB_SYNC_NOTIFICATION_CHANNEL_DESCRIPTION, pendingIntent, R.drawable.ic_cloud_update_done)

        NotificationManagerCompat.from(context).notify(STORE_DB_SYNC_NOTIFICATION_ID, notification)
    }

    override fun showStoreSyncProgressStatusNotification(message: String, title: String) {
        val notification = makeProgressStatusNotification(message, title, DB_SYNC_NOTIFICATION_CHANNEL_ID,
                DB_SYNC_NOTIFICATION_CHANNEL_NAME, DB_SYNC_NOTIFICATION_CHANNEL_DESCRIPTION, R.drawable.ic_cloud_update)

        NotificationManagerCompat.from(context).notify(STORE_DB_SYNC_NOTIFICATION_ID, notification)
    }


    /**
     * Create a Notification that is shown as a heads-up notification if possible.
     *
     * @param message Message shown on the notification
     * @param context Context needed to create Toast
     */
    private fun makeStatusNotification(message: String, title: String,
                                       notificationChannelId: String, notificationChannelName: String,
                                       notificationChannelDes: String, pendingIntent: PendingIntent?,
                                       iconId: Int = R.drawable.ic_all_store24h_red): Notification {

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(notificationChannelId, notificationChannelName, importance)
            channel.description = notificationChannelDes

            // Add the channel
            val notificationManager = context.getNotificationManager()
            notificationManager?.createNotificationChannel(channel)
        }

        // Create the notification
        val builder = NotificationCompat.Builder(context, notificationChannelId)
                .setSmallIcon(iconId)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(LongArray(0))
                .setAutoCancel(true)

        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent)

        return builder.build()
    }

    /**
     * Create a Notification that is shown with a progress bar
     *
     * @param message Message shown on the notification
     * @param context Context needed to create Toast
     */
    private fun makeProgressStatusNotification(message: String, title: String,
                                               notificationChannelId: String, notificationChannelName: String,
                                               notificationChannelDes: String, iconId: Int = R.drawable.ic_all_store24h_red): Notification {

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(notificationChannelId, notificationChannelName, importance)
            channel.description = notificationChannelDes

            // Add the channel
            val notificationManager = context.getNotificationManager()
            notificationManager?.createNotificationChannel(channel)
        }

        // Create the notification
        val builder = NotificationCompat.Builder(context, notificationChannelId)
                .setSmallIcon(iconId)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setProgress(0, 0, true) // display an indeterminate progress bar (a bar that does not indicate percentage complete)

        return builder.build()
    }

    companion object {
        private const val DB_SYNC_NOTIFICATION_CHANNEL_NAME = "DB_SYNC_NOTIFICATION_CHANNEL_NAME"
        private const val DB_SYNC_NOTIFICATION_CHANNEL_DESCRIPTION = "DB_SYNC_NOTIFICATION_CHANNEL_DESCRIPTION"
        private const val DB_SYNC_NOTIFICATION_CHANNEL_ID = "DB_SYNC_NOTIFICATION_CHANNEL_ID"

        private const val STORE_DB_SYNC_NOTIFICATION_ID = 1
    }
}