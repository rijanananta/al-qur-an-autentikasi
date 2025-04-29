package com.example.alquranapp.notif

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.content.pm.PackageManager
import com.example.alquranapp.R

object NotificationHelper {
    const val CHANNEL_ID = "alquran_channel"
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Pengingat Baca Al-Qur'an"
            val descriptionText = "Channel untuk notifikasi harian"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun showNotification(context: Context) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_icon) // Ganti dengan icon kamu
            .setContentTitle("Waktunya Membaca Al-Qur'an")
            .setContentText("Yuk lanjutkan bacaanmu hari ini ☪\uFE0F\n")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(context)) {
                notify(101, builder.build())
            }
        } else {
            Log.w("NotificationHelper", "Izin POST_NOTIFICATIONS belum diberikan.")
        }
    }
}
