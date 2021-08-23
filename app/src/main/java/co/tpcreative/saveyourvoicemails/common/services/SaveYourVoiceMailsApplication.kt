package co.tpcreative.saveyourvoicemails.common.services
import android.annotation.SuppressLint
import android.provider.Settings
import androidx.multidex.MultiDexApplication
import co.tpcreative.saveyourvoicemails.BuildConfig
import co.tpcreative.saveyourvoicemails.common.encrypt.SecurityUtil
import co.tpcreative.saveyourvoicemails.common.helper.AppPrefs

class SaveYourVoiceMailsApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        AppPrefs.initEncryptedPrefs(this)
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

    fun getUrl(): String {
        url = if (!BuildConfig.DEBUG) {
            SecurityUtil.url_live
        } else {
            SecurityUtil.url_developer
        }
        return url
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId() : String{
        return Settings.Secure.getString(applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID)
    }

}