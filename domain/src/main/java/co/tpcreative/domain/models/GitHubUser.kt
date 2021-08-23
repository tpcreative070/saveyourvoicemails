package co.tpcreative.domain.models

data class GitHubUser (
    var isSignIn : Boolean = false,
    var login: String,
    var avatarUrl: String,
    var name: String? = null,
    var company: String? = null,
    var blog: String? = null,
    var location: String? = null
)