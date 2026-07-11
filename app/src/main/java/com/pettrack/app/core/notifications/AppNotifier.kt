package com.pettrack.app.core.notifications

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pettrack.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Posts local system notifications (used when a new sighting is reported for your pet). */
@Singleton
class AppNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : Notifier {
    init {
        val channel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName("Avistamientos")
            .setDescription("Avisos cuando reportan un avistamiento de tu mascota")
            .build()
        NotificationManagerCompat.from(context).createNotificationChannel(channel)
    }

    override fun notify(id: Int, title: String, body: String) {
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        try {
            manager.notify(id, notification)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS not granted — ignore.
        }
    }

    private companion object {
        const val CHANNEL_ID = "pettrack_sightings"
    }
}
