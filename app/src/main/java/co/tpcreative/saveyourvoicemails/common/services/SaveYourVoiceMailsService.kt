package co.tpcreative.saveyourvoicemails.common.services
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import co.tpcreative.saveyourvoicemails.common.Utils

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