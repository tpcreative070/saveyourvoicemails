package co.tpcreative.saveyourvoicemails.ui.list
import co.tpcreative.domain.models.response.VoiceMail
import co.tpcreative.saveyourvoicemails.common.Utils
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication
import java.text.SimpleDateFormat
import java.util.*

class AudioViewModel(private var item: VoiceMail) {
     val title : String by lazy {
         item.title ?: ""
     }

     val id : String by lazy {
         "${item.id}"
     }

     val voice : String by lazy {
         item.voice ?: ""
     }

     val url : String by lazy {
         SaveYourVoiceMailsApplication.getInstance().getUrl() + "saveyourvoicemails/voiceApp/uploads/" + item.voice
     }

     val outputFolder : String by lazy {
         SaveYourVoiceMailsApplication.getInstance().getPrivate()
     }

     val fullLocalPath : String by lazy {
         outputFolder + voice
     }

     val createdDateTime : String by lazy {
         convertUTCToLocalTime(item.dateTime ?: "")
     }

    private fun convertUTCToLocalTime(timestamp: String) : String{
        val df = SimpleDateFormat(Utils.FORMAT_TIME, Locale.ENGLISH)
        df.timeZone = TimeZone.getTimeZone("MST")
        val date = df.parse(timestamp)
        df.timeZone = TimeZone.getDefault()
        return df.format(date)
    }
}