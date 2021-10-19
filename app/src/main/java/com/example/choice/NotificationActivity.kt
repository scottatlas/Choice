package com.example.choice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {

    private val CHANNEL_ID = "channel_id_example_01"
    private val notificationID = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        back_image11.setOnClickListener {
            finish()
        }

        createNotificationChannel()

        send_not_button.setOnClickListener {
            sendNotification()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Notification Title"
            val descriptionText = "Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID,name,importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(){

        val intent = Intent(this, BottomNav::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,intent,0)

        val bitmap = BitmapFactory.decodeResource(applicationContext.resources,
            R.drawable.profile_image
        )
        val bitmapLargeIcon = BitmapFactory.decodeResource(applicationContext.resources,
            R.drawable.choicepfp
        )





        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.placeholderlogo)
            .setContentTitle("Example Title")
            .setContentText("Example Descirption")
            .setLargeIcon(bitmapLargeIcon)
            .setStyle(NotificationCompat.BigTextStyle().bigText("You got Match!!!"))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(notificationID,builder.build())
        }
    }
}