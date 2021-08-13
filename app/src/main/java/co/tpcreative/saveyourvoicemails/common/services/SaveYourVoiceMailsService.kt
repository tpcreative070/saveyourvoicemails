package co.tpcreative.saveyourvoicemails.common.services
import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import co.tpcreative.saveyourvoicemails.common.Constant
import co.tpcreative.saveyourvoicemails.common.Navigator
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.helper.NotificationBarHelper
import co.tpcreative.saveyourvoicemails.helper.RecordHelper
import co.tpcreative.saveyourvoicemails.helper.ServiceHelper

class SaveYourVoiceMailsService : Service() {
    private val mBinder = LocalBinder() // Binder given to clients
    override fun onBind(intent: Intent?): IBinder {
       return  mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Constant.ACTION.START_RECORDING -> {
                log("recording")
                RecordHelper.instance().startRecording()
            }
            Constant.ACTION.STOP_RECORDING -> {
                log("stopping record")
                RecordHelper.instance().stopRecording()
                Navigator.movePlayer(this,SaveYourVoiceMailsApplication.getInstance().externalCacheDir?.absolutePath + "/audioFile001.wav")
            }
            Constant.ACTION.EXIT_APP -> {
                log("exit app")
                exitApp()
            }
            Constant.ACTION.START_HOME -> {
                log("Home")
            }
        }
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
    }

    fun exitApp(){
        stopForeground(true)
        stopSelf()
        ServiceHelper.getInstance().onStopService()
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