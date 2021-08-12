package co.tpcreative.saveyourvoicemails.common.services
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import co.tpcreative.saveyourvoicemails.common.Constant
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.helper.NotificationBarHelper

class SaveYourVoiceMailsService : Service() {
    private val mBinder = LocalBinder() // Binder given to clients
    override fun onBind(intent: Intent?): IBinder {
       return  mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    inner class LocalBinder : Binder() {
        // Return this instance of SignalRService so clients can call public methods
        val service: SaveYourVoiceMailsService
            get() = this@SaveYourVoiceMailsService
    }

    override fun onCreate() {
        super.onCreate()
        log("Created service")
        val notification = NotificationBarHelper.getInstance().createNotificationBar()
        startForeground(Constant.ID_NOTIFICATION_FOREGROUND_SERVICE, notification)
        //startForeground()
    }

    private fun startForeground() {
        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                // If earlier version channel ID is not used
                // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                ""
            }

        val notificationBuilder = NotificationCompat.Builder(this, channelId )
        val notification = NotificationBarHelper.getInstance().createNotificationBar(notificationBuilder)
        startForeground(101, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    fun exitApp(){
        stopForeground(true)
        stopSelf()
    }
    fun startRecord(){

    }
    fun stopRecord(){

    }
    fun pauseRecord(){

    }
    fun resumeRecord(){

    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }
}

fun SaveYourVoiceMailsService.log(message: Any){
    Utils.log(this::class.java,message)
}