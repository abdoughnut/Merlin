package com.abdodaoud.merlin.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat

import com.abdodaoud.merlin.R
import com.abdodaoud.merlin.ui.activities.MainActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = 1

        val message = intent.getStringExtra("message")
        val source = intent.getStringExtra("source")

        val contentIntent = PendingIntent.getActivity(context, 0,
                Intent(context, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), 0)

        val sourceIntent = PendingIntent.getActivity(context, 0,
                Intent(Intent.ACTION_VIEW, Uri.parse(source)), PendingIntent.FLAG_CANCEL_CURRENT)

        val sendIntent = Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, message)
                .setType("text/plain")

        val shareIntent = PendingIntent.getActivity(context, 0, sendIntent,
                PendingIntent.FLAG_CANCEL_CURRENT)

        // Create a WearableExtender to add functionality for wearables
        val wearableExtender = NotificationCompat.WearableExtender()
                .setBackground(BitmapFactory.decodeResource(context.resources,
                        R.drawable.wearable_background))
                .addAction(NotificationCompat.Action(R.mipmap.ic_notification_share_wear,
                        context.getString(R.string.action_share), shareIntent))
                .addAction(NotificationCompat.Action(R.mipmap.ic_notification_source_wear,
                        context.getString(R.string.action_source), sourceIntent))

        val notificationBuilder = NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setTicker(message)
                .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(contentIntent)
                .addAction(R.mipmap.ic_notification_share,
                        context.getString(R.string.action_share), shareIntent)
                .addAction(R.mipmap.ic_notification_source,
                        context.getString(R.string.action_source), sourceIntent)
                .extend(wearableExtender)

        // Get an instance of the NotificationManager service
        val notificationManager = NotificationManagerCompat.from(context)
        // Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}