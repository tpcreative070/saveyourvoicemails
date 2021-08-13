package co.tpcreative.saveyourvoicemails.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Constant
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class NotificationBarHelper {
    private var remoteViews : RemoteViews? = null
    companion object {
        @Volatile private var INSTANCE: NotificationBarHelper? = null
        fun  getInstance(): NotificationBarHelper {
            return INSTANCE?: synchronized(this){
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

    private val recordIntent = Intent(appContext, SaveYourVoiceMailsService::class.java)
        .apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = Constant.ACTION.START_RECORDING
        }

    private val stopRecordIntent = Intent(appContext, SaveYourVoiceMailsService::class.java)
        .apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = Constant.ACTION.STOP_RECORDING
        }

    private val exitIntent = Intent(appContext, SaveYourVoiceMailsService::class.java)
        .apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = Constant.ACTION.EXIT_APP
        }

    private val requestPermissionIntent = Intent(appContext, SaveYourVoiceMailsService::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        action = Constant.ACTION.START_RECORDING
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
        val pendingRecord = PendingIntent.getService(
            appContext,
            0,
            recordIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingStop = PendingIntent.getService(
            appContext,
            0,
            stopRecordIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingHome = PendingIntent.getService(
            appContext,
            0,
            homeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingExit = PendingIntent.getService(
            appContext,
            0,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        remoteViews = RemoteViews(appContext.packageName, R.layout.notification_bar)
        remoteViews?.setOnClickPendingIntent(R.id.rlRecord, pendingRecord)
        remoteViews?.setOnClickPendingIntent(R.id.rlStopRecord, pendingStop)
        remoteViews?.setOnClickPendingIntent(R.id.rlHome, pendingHome)
        remoteViews?.setOnClickPendingIntent(R.id.rlExit, pendingExit)
        notifyCompatBuilder
            .setContent(remoteViews)
            .setSmallIcon(R.drawable.ic_record)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .priority = NotificationManagerCompat.IMPORTANCE_HIGH
        return notifyCompatBuilder.build()
    }

    fun updatedNotificationBar(value : String) : Notification {
        val pendingStop = PendingIntent.getService(
            appContext,
            0,
            stopRecordIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingHome = PendingIntent.getService(
            appContext,
            0,
            homeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val pendingExit = PendingIntent.getService(
            appContext,
            0,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        remoteViews = RemoteViews(appContext.packageName, R.layout.notification_bar_recording)
        remoteViews?.setOnClickPendingIntent(R.id.rlStopRecord, pendingStop)
        remoteViews?.setOnClickPendingIntent(R.id.rlHome, pendingHome)
        remoteViews?.setOnClickPendingIntent(R.id.rlExit, pendingExit)
        remoteViews?.setTextViewText(R.id.tvTimer,value)
        notifyCompatBuilder
            .setContent(remoteViews)
            .setSmallIcon(R.drawable.ic_record)
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