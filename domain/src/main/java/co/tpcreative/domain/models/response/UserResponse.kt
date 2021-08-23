package co.tpcreative.domain.models.response

class UserResponse {
    var session_token : SessionToken? = null
}

class SessionToken {
    var id = 0
    var user_id: String? = null
    var session_token: String? = null
    var created_at: String? = null
    var device_id: String? = null
}

