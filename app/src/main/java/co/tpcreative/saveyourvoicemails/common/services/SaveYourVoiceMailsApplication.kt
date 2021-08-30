package co.tpcreative.saveyourvoicemails.common.services
import android.annotation.SuppressLint
import android.os.Environment
import android.provider.Settings
import androidx.multidex.MultiDexApplication
import co.tpcreative.saveyourvoicemails.BuildConfig
import co.tpcreative.saveyourvoicemails.common.encrypt.SecurityUtil
import co.tpcreative.saveyourvoicemails.common.extension.createDirectory
import co.tpcreative.saveyourvoicemails.common.helper.AppPrefs
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

class SaveYourVoiceMailsApplication : MultiDexApplication() {
    private lateinit var saveYourVoiceMailsTemp: String
    private lateinit var saveYourVoiceMailsPrivate : String
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

        saveYourVoiceMailsDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/SaveYourVoiceMails/"
        saveYourVoiceMailsDownload.createDirectory()
        isLive = true
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

    fun getTemporary() : String {
        return saveYourVoiceMailsTemp
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
        return Settings.Secure.getString(applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID)
    }

}