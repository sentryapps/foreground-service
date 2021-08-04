package com.example.foregroundservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmService : Service() {
    private lateinit var handler: Handler
    private var isForeground = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand() - starting service")

        // Ensure the notification channel is created for the foreground notification
        createNotificationChannel(this, ALARM_CHANNEL_ID, "Alarm Service")

        val notification = createForegroundNotification(
            this,
            ALARM_CHANNEL_ID,
            "Active Alarm (AlarmService)",
            "Alarm is running...")

        startForeground(ALARM_NOTIFICATION_ID, notification)
        isForeground = true

        // Stop the service after 10s
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            Log.i(TAG, "onStartCommand() - stopping service after 10s")
            stopForeground(true)
            isForeground = false
            stopSelf()
        }, 10000)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy()")

        handler.removeCallbacksAndMessages(null)

        // Ensure the foreground notification is stopped (may have been stopped via stopService() call from activity, etc)
        if (isForeground) {
            stopForeground(true)
            isForeground = false
        }

        // Start the NotificationService in foreground (ex. for snooze countdown timer, or an active stopwatch/timer)
        // (won't catch ForegroundServiceStartNotAllowedException so we can observe the crash)
        startForegroundService(Intent(this, NotificationService::class.java))
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    companion object {
        private const val ALARM_CHANNEL_ID = "alarmServiceChannelId"
        private const val ALARM_NOTIFICATION_ID = 100
        private const val TAG = "AlarmService"

        fun createForegroundNotification(context: Context, channelId: String, title: String, contentText: String): Notification {
            val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
                .setContentTitle(title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_alarm)
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLocalOnly(true)

            return builder.build()
        }

        fun createNotificationChannel(context: Context, channelId: String, channelName: String) {
            // Create the notification channel (make it high importance so shows heads-up and easy to see for this sample project)
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH)
            channel.setSound(null, null)
            NotificationManagerCompat.from(context).createNotificationChannel(channel)
        }
    }
}