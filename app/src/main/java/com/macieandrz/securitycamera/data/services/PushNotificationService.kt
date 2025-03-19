package com.macieandrz.securitycamera.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.macieandrz.securitycamera.MainActivity
import com.macieandrz.securitycamera.R

class PushNotificationService : FirebaseMessagingService() {
    companion object {
        private const val CHANNEL_ID = "security_camera_channel"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onNewToken(token: String) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            Firebase.firestore.collection("users").document(it.uid)
                .update("fcmToken", token)
                .addOnSuccessListener {
                    Log.d("DEBUG", "Token updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("DEBUG", "Error updating token: ${e.message}")
                }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("DEBUG", "Received FCM message: ${remoteMessage.data}")

        // Check if the message contains notification data
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "Notification"
            val body = notification.body ?: "Activity detected"

            // Display the notification
            showNotification(title, body)
        }

        // Handle data message type
        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"] ?: "Notification"
            val message = remoteMessage.data["message"] ?: "Activity detected"

            showNotification(title, message)
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Security Camera Notifications",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for activity detection notifications"
            enableLights(true)
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)

        // Create an intent to open the app when the notification is clicked
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build the notification
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Display the notification
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

}
