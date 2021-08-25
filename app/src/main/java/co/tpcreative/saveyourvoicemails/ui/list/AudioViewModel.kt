package co.tpcreative.saveyourvoicemails.ui.list
import co.tpcreative.domain.models.response.VoiceMail
import co.tpcreative.saveyourvoicemails.common.extension.isFileExist
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication

class AudioViewModel {
     val title : String by lazy {
         item.title ?: ""
     }

     val id : String by lazy {
         item.voice ?: ""
     }

     val url : String by lazy {
         SaveYourVoiceMailsApplication.getInstance().getUrl() + "saveyourvoicemails/voiceApp/uploads/" + item.voice
     }

     val outputFolder : String by lazy {
         SaveYourVoiceMailsApplication.getInstance().getPrivate()
     }

     val fullLocalPath : String by lazy {
         outputFolder + id
     }

     val createdDateTime : String by lazy {
         item.dateTime ?: ""
     }

     private var  item : VoiceMail
     constructor(item : VoiceMail){
         this.item = item
     }
}