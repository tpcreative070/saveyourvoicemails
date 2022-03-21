package co.tpcreative.saveyourvoicemails.common.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import co.tpcreative.saveyourvoicemails.common.Constant
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsService
import co.tpcreative.saveyourvoicemails.R


class NotificationBarHelper {
    private var remoteViews : RemoteViews? = null
    companion object {
        @Volatile private var INSTANCE: NotificationBarHelper? = null
        fun  getInstance(): NotificationBarHelper {
            return INSTANCE ?: synchronized(this){
                NotificationBarHelper().also {
                    INSTANCE = it
                }
            }
        }
    }

    init {
    }

    private val notifyManager: NotificationManager by lazy {
        appContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun initNotifyChanel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notifyManager.getNotificationChannel(
                Constant.FOREGROUND_CHANNEL_ID
            ) == null
        ) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                Constant.FOREGROUND_CHANNEL_ID,
                Constant.FOREGROUND_CHANNEL_NAME,
                importance
            )
            channel.enableVibration(true)
            notifyManager.createNotificationChannel(channel)
        }
    }

    private val appContext by lazy {
        SaveYourVoiceMailsApplication.getInstance().applicationContext
    }

    private val homeIntent = Intent(appContext, SaveYourVoiceMailsService::class.java)
        .apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = Constant.ACTION.START_HOME
        }

    private val recordPhoneCallIntent = Intent(appContext, SaveYourVoiceMailsService::class.java)
        .apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = Constant.ACTION.START_RECORDING_PHONE_CALL
        }

    private val stopPhoneCallRecordIntent = Intent(appContext, SaveYourVoiceMailsService::class.java)
            .apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                action = Constant.ACTION.STOP_RECORDING_PHONE_CALL
            }

    private val notifyCompatBuilder by lazy {
        initNotifyChanel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return@lazy NotificationCompat.Builder(appContext, Constant.FOREGROUND_CHANNEL_ID)
        } else {
            return@lazy  NotificationCompat.Builder(appContext, Constant.FOREGROUND_CHANNEL_ID)
        }
    }

    fun createNotificationBar() : Notification {
        val pendingHome = PendingIntent.getService(
            appContext,
            0,
            homeIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews = RemoteViews(appContext.packageName, R.layout.notification_bar)
        remoteViews?.setOnClickPendingIntent(R.id.rlHome, pendingHome)
        notifyCompatBuilder
            .setContent(remoteViews)
            .setSmallIcon(R.drawable.ic_record)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .priority = NotificationManagerCompat.IMPORTANCE_HIGH
        return notifyCompatBuilder.build()
    }

    fun updatedNotificationBar() : Notification {
        val pendingStop = PendingIntent.getService(
            appContext,
            0,
            stopPhoneCallRecordIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val pendingHome = PendingIntent.getService(
            appContext,
            0,
            homeIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        remoteViews = RemoteViews(appContext.packageName, R.layout.notification_bar_recording)
        remoteViews?.setOnClickPendingIntent(R.id.rlStopRecord, pendingStop)
        remoteViews?.setOnClickPendingIntent(R.id.rlHome, pendingHome)
        notifyCompatBuilder
            .setContent(remoteViews)
            .setSmallIcon(R.drawable.ic_record)
                .setColor(ContextCompat.getColor(appContext,R.color.colorAccent))
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .priority = NotificationManagerCompat.IMPORTANCE_HIGH
        return notifyCompatBuilder.build()
    }
}

fun NotificationBarHelper.log(message: Any){
    Utils.log(this::class.java, message)
}