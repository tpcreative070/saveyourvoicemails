package co.tpcreative.presentation.common.services
import android.Manifest
import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import co.tpcreative.presentation.common.Constant
import co.tpcreative.presentation.common.Utils
import co.tpcreative.presentation.common.helper.NotificationBarHelper
import co.tpcreative.presentation.common.helper.ServiceHelper
import co.tpcreative.saveyourvoicemails.helper.RecordHelper
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class SaveYourVoiceMailsService : Service() {
    private val mBinder = LocalBinder() // Binder given to clients
    override fun onBind(intent: Intent?): IBinder {
       return  mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Constant.ACTION.START_RECORDING -> {
                log("START_RECORDING")
                Dexter.withContext(this)
                    .withPermissions(
                        Manifest.permission.BIND_ACCESSIBILITY_SERVICE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS
                    ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            RecordHelper.instance().startRecording()
                            val notification = NotificationBarHelper.getInstance().updatedNotificationBar()
                            startForeground(Constant.ID_NOTIFICATION_FOREGROUND_SERVICE, notification)
                        }
                        override fun onPermissionRationaleShouldBeShown(
                            permissions: List<PermissionRequest?>?,
                            token: PermissionToken?
                        ) { /* ... */
                        }
                    }).check()
            }
            Constant.ACTION.START_RECORDING_PHONE_CALL -> {
                log("START_RECORDING_PHONE_CALL")
                Dexter.withContext(this)
                    .withPermissions(
                        Manifest.permission.BIND_ACCESSIBILITY_SERVICE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS
                    ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            RecordHelper.instance().startRecordPhoneCall()
                            val notification = NotificationBarHelper.getInstance().updatedNotificationBar()
                            startForeground(Constant.ID_NOTIFICATION_FOREGROUND_SERVICE, notification)
                        }
                        override fun onPermissionRationaleShouldBeShown(
                            permissions: List<PermissionRequest?>?,
                            token: PermissionToken?
                        ) { /* ... */
                        }
                    }).check()
            }
            Constant.ACTION.STOP_RECORDING -> {
                log("stopping record")
                Dexter.withContext(this)
                    .withPermissions(
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.PROCESS_OUTGOING_CALLS,
                        Manifest.permission.MODIFY_AUDIO_SETTINGS
                    ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            RecordHelper.instance().stopRecording()
                            val notification = NotificationBarHelper.getInstance().createNotificationBar()
                            startForeground(Constant.ID_NOTIFICATION_FOREGROUND_SERVICE, notification)
                        }
                        override fun onPermissionRationaleShouldBeShown(
                            permissions: List<PermissionRequest?>?,
                            token: PermissionToken?
                        ) { /* ... */
                        }
                    }).check()
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