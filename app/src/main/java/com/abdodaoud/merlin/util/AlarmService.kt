package com.abdodaoud.merlin.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.abdodaoud.merlin.R
import java.util.*

class AlarmService(val context: Context): Service() {
    val mAlarmSender: PendingIntent

    val sharedPref = context.getSharedPreferences(Constants.PREF_FILE, Context.MODE_PRIVATE)

    val hourOfDay = sharedPref.getInt(context.getString(R.string.pref_hour), 9)
    val minute = sharedPref.getInt(context.getString(R.string.pref_minute), 0)

    init {
        mAlarmSender = PendingIntent.getBroadcast(context, 0,
                Intent(context, AlarmReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun startAlarm() {
        // Set the alarm to the desired time
        val calendar = Calendar.getInstance()
        val now = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        calendar.clear(Calendar.SECOND)
        calendar.clear(Calendar.MILLISECOND)

        // Check whether the time is earlier than current time. If so, set it to tomorrow.
        // Otherwise, all alarms for earlier time will fire
        if(calendar.before(now)) calendar.add(Calendar.DATE, 1)

        // Schedule the alarm!
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY,
                mAlarmSender)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}