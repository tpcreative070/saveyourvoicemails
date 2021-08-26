package co.tpcreative.domain.models

class UploadBody {
    lateinit var session_token : String
    lateinit var user_id : String
    lateinit var fileTitle : String
    lateinit var mimeType : String
    lateinit var fileName: String
    lateinit var extension : String
}