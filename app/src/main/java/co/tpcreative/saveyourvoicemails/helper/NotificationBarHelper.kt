package co.tpcreative.saveyourvoicemails.helper

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import co.tpcreative.saveyourvoicemails.R
import co.tpcreative.saveyourvoicemails.common.Constant
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsService

class NotificationBarHelper {

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
        initNotifyChanel()
    }

    private val notifyManager: NotificationManager by lazy {
        SaveYourVoiceMailsApplication.getInstance()
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun initNotifyChanel(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && notifyManager.getNotificationChannel(
                Constant.FOREGROUND_CHANNEL_ID
            ) == null
        ) {
            val name = Constant.NAME_NOTIFICATION
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constant.FOREGROUND_CHANNEL_ID, name, importance)
            channel.enableVibration(false)
            notifyManager.createNotificationChannel(channel)
        }
    }

    private val appContext by lazy {
        SaveYourVoiceMailsApplication.getInstance()
    }

    private val homeIntent = Intent(appContext, SaveYourVoiceMailsService::class.java)
        .apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = Constant.ACTION.START_HOME
        }

    private val requestPermissionIntent = Intent(appContext, SaveYourVoiceMailsService::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        action = Constant.ACTION.START_RECORDING
    }

    private val notifyCompatBuilder by lazy {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            return@lazy NotificationCompat.Builder(appContext, Constant.FOREGROUND_CHANNEL_ID)
        } else {
            return@lazy  NotificationCompat.Builder(appContext, Constant.FOREGROUND_CHANNEL_ID)
        }
    }

    @SuppressLint("WrongConstant")
    fun createNotificationBar() : Notification{
        val pendingHome = PendingIntent.getService(appContext, 0, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val remoteViews = RemoteViews(appContext.packageName, R.layout.notification_bar)
        remoteViews.setOnClickPendingIntent(R.id.imgHome, pendingHome)
        notifyCompatBuilder
            .setContent(remoteViews)
            .setSmallIcon(R.drawable.ic_record)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOnlyAlertOnce(true)
            //.setOngoing(true)
            .setAutoCancel(true)
            .priority = NotificationManagerCompat.IMPORTANCE_HIGH
        notifyCompatBuilder.setVisibility(Notification.VISIBILITY_PUBLIC)
        return notifyCompatBuilder.build()
    }

}