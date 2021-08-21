package co.tpcreative.saveyourvoicemails.common.services
import androidx.multidex.MultiDexApplication
import co.tpcreative.saveyourvoicemails.common.helper.AppPrefs

class SaveYourVoiceMailsApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        AppPrefs.initEncryptedPrefs(this)
    }

    companion object{
        @Volatile private var INSTANCE: SaveYourVoiceMailsApplication? = null
        fun  getInstance(): SaveYourVoiceMailsApplication {
            return INSTANCE ?: synchronized(this){
                SaveYourVoiceMailsApplication().also {
                    INSTANCE = it
                }
            }
        }
    }

}