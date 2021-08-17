package co.tpcreative.presentation.common.services
import androidx.multidex.MultiDexApplication

class SaveYourVoiceMailsApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object{
        @Volatile private var INSTANCE: SaveYourVoiceMailsApplication? = null
        fun  getInstance(): SaveYourVoiceMailsApplication {
            return INSTANCE?: synchronized(this){
                SaveYourVoiceMailsApplication().also {
                    INSTANCE = it
                }
            }
        }
    }

}