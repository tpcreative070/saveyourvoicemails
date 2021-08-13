package co.tpcreative.saveyourvoicemails.common.services
import android.Manifest
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
import co.tpcreative.saveyourvoicemails.helper.log
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SaveYourVoiceMailsService : Service() {
    private val mBinder = LocalBinder() // Binder given to clients
    override fun onBind(intent: Intent?): IBinder {
       return  mBinder
    }
    private var mTimer = ""
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Constant.ACTION.START_RECORDING -> {
                log("recording")
                Dexter.withContext(this)
                    .withPermissions(
                        Manifest.permission.RECORD_AUDIO
                    ).withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            RecordHelper.instance().startRecording()
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
                        Manifest.permission.RECORD_AUDIO
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

    init {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: String) {
        if (event == mTimer){
            return
        }
        mTimer = event
        val notification = NotificationBarHelper.getInstance().updatedNotificationBar(event)
        startForeground(Constant.ID_NOTIFICATION_FOREGROUND_SERVICE, notification)
        log(event)
    }
}

fun SaveYourVoiceMailsService.log(message: Any){
    Utils.log(this::class.java,message)
}