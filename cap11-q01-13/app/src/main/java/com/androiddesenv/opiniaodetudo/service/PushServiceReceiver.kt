package com.androiddesenv.opiniaodetudo.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.androiddesenv.opiniaodetudo.MainActivity
import com.androiddesenv.opiniaodetudo.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushServiceReceiver : FirebaseMessagingService() {

    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MainActivity.PUSH_NOTIFICATION_CHANNEL,
                "Canal padrão",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Principal canal de entrega de mensagem"
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val messageId = System.currentTimeMillis().toInt()
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(
                this,
                MainActivity.PUSH_NOTIFICATION_MESSAGE_REQUEST,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        val notification = NotificationCompat.Builder(
            this, MainActivity.PUSH_NOTIFICATION_CHANNEL
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground) //escolha um drawable ou crie um
            .setContentTitle(remoteMessage.notification?.title ?: "Opini")
            .setContentText(remoteMessage.notification?.body ?: "Mensagem no Opini")
            .setContentIntent(pendingIntent)
            .build()
        NotificationManagerCompat.from(this).notify(messageId, notification)
    }
}