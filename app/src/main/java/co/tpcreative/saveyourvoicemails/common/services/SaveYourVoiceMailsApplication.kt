package co.tpcreative.saveyourvoicemails.common.services

import android.annotation.SuppressLint
import android.os.Environment
import android.provider.Settings
import androidx.multidex.MultiDexApplication
import co.tpcreative.saveyourvoicemails.BuildConfig
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.encrypt.SecurityUtil
import co.tpcreative.saveyourvoicemails.common.extension.createDirectory
import co.tpcreative.saveyourvoicemails.common.extension.isSignedIn
import co.tpcreative.saveyourvoicemails.common.helper.AppPrefs
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.elvishew.xlog.interceptor.BlacklistTagsFilterInterceptor
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy
import com.elvishew.xlog.printer.file.naming.ChangelessFileNameGenerator
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import org.solovyev.android.checkout.Billing
import org.solovyev.android.checkout.Billing.DefaultConfiguration


class SaveYourVoiceMailsApplication : MultiDexApplication() {
    private lateinit var saveYourVoiceMailsTemp: String
    private lateinit var saveYourVoiceMailsPrivate : String
    private lateinit var saveYourVoiceMailsLog : String
    private lateinit var saveYourVoiceMails : String
    private lateinit var saveYourVoiceMailsRecorder : String
    private lateinit var saveYourVoiceMailsDownload: String
    private var isLive : Boolean = false
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(this);
        INSTANCE = this
        AppPrefs.initEncryptedPrefs(this)

        saveYourVoiceMails = getExternalFilesDir(null)?.absolutePath + "/SaveYourVoiceMails/"

        saveYourVoiceMailsTemp = saveYourVoiceMails + "temporary/"
        saveYourVoiceMailsTemp.createDirectory()
        saveYourVoiceMailsPrivate = saveYourVoiceMails + "private/"
        saveYourVoiceMailsPrivate.createDirectory()

        saveYourVoiceMailsRecorder = saveYourVoiceMails + "recorder/"
        saveYourVoiceMailsRecorder.createDirectory()

        saveYourVoiceMailsLog = saveYourVoiceMails + "log/"
        saveYourVoiceMailsLog.createDirectory()

        saveYourVoiceMailsDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/SaveYourVoiceMails/"
        saveYourVoiceMailsDownload.createDirectory()
        isLive = true
    }


    fun initXLog(){
        if (!Utils.isSignedIn()){
            return
        }
        val config = LogConfiguration.Builder()
            .logLevel(
                LogLevel.ALL
            )
            .tag("MY_TAG") // Specify TAG, default: "X-LOG"
            .enableStackTrace(1) // Enable stack trace info with depth 2, disabled by default
            .build()

        val filePrinter: Printer =
            FilePrinter.Builder(saveYourVoiceMailsLog) // Specify the directory path of log file(s)
                .fileNameGenerator(ChangelessFileNameGenerator(Utils.getUserId()+"_log.txt")) // Default: ChangelessFileNameGenerator("log")
                .backupStrategy(NeverBackupStrategy()) // Default: FileSizeBackupStrategy(1024 * 1024)
                .build()
        XLog.init(config, filePrinter);
        XLog.d(Utils.getUUId())
    }
    companion object{
        @Volatile private var INSTANCE: SaveYourVoiceMailsApplication? = null
        lateinit var url : String
        fun  getInstance(): SaveYourVoiceMailsApplication {
            return INSTANCE ?: synchronized(this){
                SaveYourVoiceMailsApplication().also {
                    INSTANCE = it
                }
            }
        }
    }

    private val mBilling = Billing(this, object : DefaultConfiguration() {
        override fun getPublicKey(): String {
            return SecurityUtil.API
        }
    })

    fun getBilling(): Billing {
        return mBilling
    }

    fun getTemporary() : String {
        return saveYourVoiceMailsTemp
    }

    fun getFileLog() : String {
        return saveYourVoiceMailsLog + Utils.getUserId()+"_log.txt"
    }

    fun getDownload() : String {
        return saveYourVoiceMailsDownload
    }

    fun getRecorder(): String {
        return saveYourVoiceMailsRecorder
    }

    fun getPrivate() : String {
        return  saveYourVoiceMailsPrivate
    }

    fun getUrl(): String {
        url = if (!BuildConfig.DEBUG) {
            SecurityUtil.url_live
        } else {
            if (isLive){
                SecurityUtil.url_live
            }else{
                SecurityUtil.url_developer
            }
        }
        return url
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId() : String{
        return Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

}