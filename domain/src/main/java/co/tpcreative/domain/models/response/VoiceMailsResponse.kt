package co.tpcreative.domain.models.response

import co.tpcreative.domain.models.BaseResponse

class VoiceMailsResponse : BaseResponse(){
    var data: MutableList<VoiceMail>? = null
}

class VoiceMail {
    var id : Int? = null
    var user_id: String? = null
    var title: String? = null
    var status : Int? = null
    var voice: String? = null
    var dateTime: String? = null
}