package com.carles.lallorigueraviews.common

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.*
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.carles.lallorigueraviews.MainActivity
import com.carles.lallorigueraviews.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationHelper @Inject constructor(@ApplicationContext private val context: Context) {

    fun createDelayedTasksChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                /* id = */ DELAYED_NOTIFICATION_CHANNEL_ID,
                /* name = */ context.getString(R.string.notification_channel_delayed_tasks),
                /* importance = */ IMPORTANCE_DEFAULT
            )
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun createDelayedTasksNotification(tasksNames: List<String>) {
        val notificationManager = NotificationManagerCompat.from(context)

        if (notificationManager.areNotificationsEnabled()) {
            Log.i("NotificationHelper", "creating delayed tasks notification")

            val intent = Intent(context, MainActivity::class.java)
            val contentIntent = PendingIntent.getActivity(
                /* context = */ context,
                /* requestCode = */ MAIN_ACTIVITY_REQUEST_CODE,
                /* intent = */ intent,
                /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, DELAYED_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.conill)
                .setContentTitle(context.getString(R.string.notification_delayed_tasks_title))
                .setContentText(tasksNames.joinToString(", ").uppercase())
                .setContentIntent(contentIntent)
                .setAutoCancel(true)

            notificationManager.notify(DELAYED_NOTIFICATION_ID, builder.build())
        }
    }

    companion object {

        private const val DELAYED_NOTIFICATION_CHANNEL_ID = "notification_channel_delayed"
        private const val DELAYED_NOTIFICATION_ID = 1
        private const val MAIN_ACTIVITY_REQUEST_CODE = 1
    }
}