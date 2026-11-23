package com.healthconnect.app.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object MedicineScheduler {
    const val ACTION_ALARM_TRIGGER = "com.healthconnect.app.ALARM_TRIGGER"
    const val ACTION_STOP_ALARM = "com.healthconnect.app.STOP_ALARM"
    const val EXTRA_REMINDER_ID = "extra_reminder_id"
    const val EXTRA_MEDICINE_NAME = "extra_medicine_name"
    const val EXTRA_DOSAGE = "extra_dosage"
    const val EXTRA_RINGTONE_URI = "extra_ringtone_uri"

    fun scheduleDailyReminder(context: Context, id: Int, medicineName: String, dosage: String, hour: Int, minute: Int, ringtoneUri: String = "") {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM_TRIGGER
            putExtra(EXTRA_REMINDER_ID, id)
            putExtra(EXTRA_MEDICINE_NAME, medicineName)
            putExtra(EXTRA_DOSAGE, dosage)
            putExtra(EXTRA_RINGTONE_URI, ringtoneUri)
        }
        val pending = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute); set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.timeInMillis, AlarmManager.INTERVAL_DAY, pending)
    }

    fun cancelReminder(context: Context, id: Int) {
        val pending = PendingIntent.getBroadcast(context, id, Intent(context, AlarmReceiver::class.java).setAction(ACTION_ALARM_TRIGGER), PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        if (pending != null) context.getSystemService(AlarmManager::class.java).cancel(pending)
    }

    fun stopAlarmRingtone(context: Context) {
        context.sendBroadcast(Intent(context, AlarmReceiver::class.java).setAction(ACTION_STOP_ALARM))
    }
}
