package com.example.foregroundservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.i("AlarmReceiver", "onReceive() - alarm broadcast received")

        // Not going to validate the alarm intent, just start the alarm...
        context.startForegroundService(Intent(context, AlarmService::class.java))
    }
}