package co.tpcreative.domain.models.response

class UserResponse {
    var message: String? = null
    var error = false
    var session_token : SessionToken? = null
    var user: User? = null
    var mail365: Mail365? = null
}

class SessionToken {
    var id = 0
    var user_id: String? = null
    var session_token: String? = null
    var created_at: String? = null
    var device_id: String? = null
}

class Mail365 {
    var id = 0
    var access_token: String? = null
    var refresh_token: String? = null
    var client_id: String? = null
    var redirect_uri: String? = null
    var grant_type: String? = null
    var created_date: String? = null
    var user_id: String? = null
}

class User {
    var id = 0
    var contactNumber: String? = null
    var email: String? = null
    var status = 0
    var create_date: String? = null
    var name: String? = null
    var isSignIn : Boolean = false
}

