package com.example.foregroundservice

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val checkBoxCloseActivity = findViewById<CheckBox>(R.id.checkBoxCloseActivity)
        val buttonStartAlarm = findViewById<Button>(R.id.buttonStartAlarm)

        buttonStartAlarm.setOnClickListener {
            buttonStartAlarm.isEnabled = false
            checkBoxCloseActivity.isEnabled = false

            setAlarm()

            Toast.makeText(this, "Alarm starting in 10 seconds", Toast.LENGTH_LONG).show()
            if (checkBoxCloseActivity.isChecked) finishAndRemoveTask()
        }
    }

    private fun setAlarm() {
        Log.i("MainActivity", "setAlarm() - scheduling alarm for 10s from now")

        val intent = Intent(this, AlarmReceiver::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            1000, // test id
            intent,
            flags
        )

        val alarmClockInfo = AlarmClockInfo(System.currentTimeMillis() + 10000, pendingIntent)
        (getSystemService(ALARM_SERVICE) as AlarmManager).setAlarmClock(alarmClockInfo, pendingIntent)
    }
}