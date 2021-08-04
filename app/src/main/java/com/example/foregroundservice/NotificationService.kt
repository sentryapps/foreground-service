package com.example.foregroundservice

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log

class NotificationService : Service() {
    private lateinit var handler: Handler
    private var isForeground = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand() - starting service")

        // Ensure the notification channel is created for the foreground notification
        AlarmService.createNotificationChannel(this, NOTIFICATION_SERVICE_CHANNEL_ID, "Notification Service")

        val notification = AlarmService.createForegroundNotification(
            this,
            NOTIFICATION_SERVICE_CHANNEL_ID,
            "Alarm Snoozed (NotificationService)",
            "zzz zzz zzz..."
        )

        startForeground(NOTIFICATION_SERVICE_NOTIFICATION_ID, notification)
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
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    companion object {
        private const val NOTIFICATION_SERVICE_CHANNEL_ID = "notificationServiceChannelId"
        private const val NOTIFICATION_SERVICE_NOTIFICATION_ID = 200
        private const val TAG = "NotificationService"
    }
}