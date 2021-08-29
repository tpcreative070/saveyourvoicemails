package co.tpcreative.domain.models.response

import co.tpcreative.domain.models.BaseResponse

class Mail365Response : BaseResponse() {
    var data : Mail365? = null
    var code : String? = null
}