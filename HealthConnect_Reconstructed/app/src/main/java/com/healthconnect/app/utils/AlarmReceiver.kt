package com.healthconnect.app.utils

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.healthconnect.app.R

class AlarmReceiver : BroadcastReceiver() {
    companion object { private var activePlayer: MediaPlayer? = null; private const val CHANNEL_ID = "medicine_reminder_alarm_channel" }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == MedicineScheduler.ACTION_STOP_ALARM) {
            activePlayer?.stop(); activePlayer?.release(); activePlayer = null
            context.getSystemService(NotificationManager::class.java).cancel(1001)
            return
        }
        val id = intent.getIntExtra(MedicineScheduler.EXTRA_REMINDER_ID, 0)
        val medicine = intent.getStringExtra(MedicineScheduler.EXTRA_MEDICINE_NAME) ?: "Medicine Reminder"
        val dosage = intent.getStringExtra(MedicineScheduler.EXTRA_DOSAGE) ?: "Time to take your scheduled dose."
        playSound(context, intent.getStringExtra(MedicineScheduler.EXTRA_RINGTONE_URI).orEmpty())
        showNotification(context, id, medicine, dosage)
    }

    private fun playSound(context: Context, custom: String) {
        runCatching {
            activePlayer?.release()
            val uri = custom.takeIf { it.isNotBlank() }?.let(Uri::parse) ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            activePlayer = MediaPlayer.create(context, uri)?.apply { isLooping = true; start() }
        }
    }

    private fun showNotification(context: Context, id: Int, medicine: String, dosage: String) {
        val nm = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= 26) nm.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Medicine Daily Reminders", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Urgent alerts for daily scheduled medications"
        })
        val dismiss = PendingIntent.getBroadcast(context, id + 10_000, Intent(context, AlarmReceiver::class.java).setAction(MedicineScheduler.ACTION_STOP_ALARM), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val n = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(medicine)
            .setContentText(dosage)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .addAction(0, "Dismiss Alarm", dismiss)
            .build()
        nm.notify(1001, n)
    }
}
