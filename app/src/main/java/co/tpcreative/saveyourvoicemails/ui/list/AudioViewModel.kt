package co.tpcreative.saveyourvoicemails.ui.list
import co.tpcreative.domain.models.response.VoiceMail
import co.tpcreative.saveyourvoicemails.common.services.SaveYourVoiceMailsApplication

class AudioViewModel {
     val title : String by lazy {
         item.title ?: ""
     }
     val url : String by lazy {
         SaveYourVoiceMailsApplication.getInstance().getUrl() + "saveyourvoicemails/voiceApp/uploads/" + item.voice
     }
     private var  item : VoiceMail
     constructor(item : VoiceMail){
         this.item = item
     }
}