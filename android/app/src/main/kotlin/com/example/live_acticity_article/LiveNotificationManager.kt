package com.example.live_acticity_article


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi

/// Для версии андроида >= 8.0
@RequiresApi(Build.VERSION_CODES.O)
class LiveActivityManager(private val context: Context) {
    private val remoteViews = RemoteViews("com.example.live_acticity_article", R.layout.live_notification)

    private val notificationId = 100
    private val channelWithHighPriority = "channelWithHighPriority"
    private val channelWithDefaultPriority = "channelWithDefaultPriority"
    private val pendingIntent = PendingIntent.getActivity(
    context, 
    200,
    Intent(context, MainActivity::class.java).apply { 
        flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT 
    },
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel(channelWithDefaultPriority)
        createNotificationChannel(channelWithHighPriority, true)
    }


    // Функция для создания каналов уведомлений
    private fun createNotificationChannel(channelName : String, importanceHigh : Boolean = false) {
        val importance = if (importanceHigh) IMPORTANCE_HIGH else IMPORTANCE_DEFAULT
        val existingChannel =  (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).getNotificationChannel(channelName)
        if (existingChannel == null) { 
            val channel =
                NotificationChannel(channelName, "Delivery Notification", importance).apply {
                    setSound(null, null)
                    vibrationPattern = longArrayOf(0L)
                }

            notificationManager.createNotificationChannel(channel)
        }
    }

    // 1 стадия -  Заказ оформлен
    private fun onFirstNotification(): Notification {
        return Notification.Builder(context, channelWithHighPriority)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Live Notification - Your order has been processed")
            .setContentIntent(pendingIntent)
            .setWhen(3000)
            .setOngoing(true)
            .setCustomBigContentView(remoteViews)
            .build()
    }
    
    // 2 стадия - Заказ начал собираться
    private fun onGoingNotification(): Notification {
        return Notification.Builder(context, channelWithDefaultPriority)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Live Notification - Your order is being collected")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCustomBigContentView(remoteViews)
            .build()
    }
    
    // 3 стадия - Заказ в пути
    private fun onOrderOnTheWayNotification(minutesToDelivery: Int): Notification {
        val minuteString = if (minutesToDelivery > 1) "minutes" else "minute"

        return Notification.Builder(context, channelWithHighPriority)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setContentTitle("Live Notification - Your order is on its way and will be delivered in $minutesToDelivery $minuteString")
            .setCustomBigContentView(remoteViews)
            .build()
    }
    
    // 4 стадия - Заказ доставлен
    private fun onFinishNotification(): Notification {
        return Notification.Builder(context, channelWithHighPriority)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Live Notification - Your order is delivered")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCustomBigContentView(remoteViews) 
            .build()
    }

    


    // Функция для отображения уведомления первой стадии
    fun showNotification()  {
        val notification = onFirstNotification()
        
        remoteViews.setTextViewText(R.id.order_status, "Your order has been processed and will be collected soon")

        remoteViews.setImageViewResource(R.id.image_stage, R.drawable.stage1)


        notificationManager.notify(notificationId, notification)
    }

    // Функция для обновления и отображения уведомления второй и третьей стадий
    fun updateNotification(minutesToDelivery: Int, stage: Int) {
        val minuteString = if (minutesToDelivery > 1) "minutes" else "minute"
        
        when (stage) {
            2 -> {
                remoteViews.setTextViewText(R.id.order_status, "Your order is being assembled and will be shipped to you soon")
                remoteViews.setImageViewResource(R.id.image_stage, R.drawable.stage2)
            }
            3 -> {
                remoteViews.setTextViewText(R.id.order_status, "Your order is on its way and will be delivered in $minutesToDelivery $minuteString")
                remoteViews.setImageViewResource(R.id.image_stage, R.drawable.stage3)
            }
            
        }
    
        val notification: Notification? = when (stage) {
            2 -> {
                onGoingNotification()
            }
            3 -> {
                onOrderOnTheWayNotification(minutesToDelivery)
            }
            else -> null
        }
        
        if (notification != null) {
            notificationManager.notify(notificationId, notification)
        } else {
            println("Error: Notification is null.")
        }
    
        notificationManager.notify(notificationId, notification)
    }

    // Функция для отображения уведомления четвертой стадии
    fun finishDeliveryNotification() {
        val notification = onFinishNotification()
        remoteViews.setTextViewText(R.id.order_status, "Your order is delivered. Enjoy your purchase!")
        remoteViews.setImageViewResource(R.id.image_stage,R.drawable.stage4)
        notificationManager.notify(notificationId, notification)
    }

    // Функция для удаление каналов при окончании ЖЦ уведа(просто скрыли)
    fun endNotification() {
      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.deleteNotificationChannel(channelWithHighPriority)
        notificationManager.deleteNotificationChannel(channelWithDefaultPriority)
        remoteViews.setViewVisibility(R.id.order_status, View.GONE)
    }

}