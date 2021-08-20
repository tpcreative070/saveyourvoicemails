package co.tpcreative.saveyourvoicemails.common.helper
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsService
import co.tpcreative.saveyourvoicemails.common.services.StateRecorderListener
import java.lang.Exception

class ServiceHelper : StateRecorderListener {

    companion object{
        @Volatile private var INSTANCE: ServiceHelper? = null
        fun  getInstance(): ServiceHelper {
            return INSTANCE ?: synchronized(this){
                ServiceHelper().also {
                    INSTANCE = it
                }
            }
        }
    }

    var stateService:String? = null
    var stateRecorderListener: StateRecorderListener? = this
    var myService: SaveYourVoiceMailsService? = null

    private var myConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, binder: IBinder) {
            myService = (binder as SaveYourVoiceMailsService.LocalBinder).service
            log("Connected")
            if (myService == null){
                log( "myService is null")
            }else{
                log( "myService not null")
            }
        }
        //binder comes from server to communicate with method's of
        override fun onServiceDisconnected(className: ComponentName) {
            log("Disconnected")
            myService = null
        }
    }

    fun getService(): SaveYourVoiceMailsService? {
        return myService
    }

    fun clearSubscription(){
        if (myService == null){
            return
        }
    }

    private fun doBindService() {
        if (myService != null) {
            return
        }
        log( "doBindService")
        val intent: Intent?
        intent = Intent(SaveYourVoiceMailsApplication.getInstance(), SaveYourVoiceMailsService::class.java)
        SaveYourVoiceMailsApplication.getInstance().bindService(intent, myConnection, Context.BIND_AUTO_CREATE)
    }

    fun startRecord(){
        if (myService != null){
            myService?.startRecord()
        }
    }

    fun stopRecord(){
        myService?.stopRecord()
    }

    fun exitApp(){
        myService?.exitApp()
    }

    fun onStartService() {
        if (myService == null) {
            doBindService()
        }
    }

    fun pauseRecord(){

    }

    fun resumeRecord(){
        myService?.resumeRecord()
    }

    fun onStopService() {
        if (myService != null) {
            try {
                SaveYourVoiceMailsApplication.getInstance().unbindService(myConnection)
            }catch (e: Exception){

            }
            myService = null
        }
        if (stateRecorderListener != null){
            stateRecorderListener = null
        }
        stateService = null
    }

    override fun onChangeState(state: String) {
        TODO("Not yet implemented")
    }

    override fun onNetWorkChange(isNetWorks: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onSyncChange(state: String) {
        TODO("Not yet implemented")
    }

    override fun onSyncImageChange(state: String) {
        TODO("Not yet implemented")
    }
}

fun ServiceHelper.log(message : Any){
    Utils.log(this::class.java,message)
}