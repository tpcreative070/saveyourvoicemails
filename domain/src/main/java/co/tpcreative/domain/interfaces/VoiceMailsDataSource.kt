package co.tpcreative.domain.interfaces

import co.tpcreative.domain.models.SearchUsersResult
import co.tpcreative.domain.models.User
import co.tpcreative.domain.models.request.UserRequest
import co.tpcreative.domain.models.response.UserResponse

interface VoiceMailsDataSource {

    fun searchUsers(query: String): SearchUsersResult

    fun getUser(username: String): User

    fun signIn(request: UserRequest) : UserResponse

    fun signUp(request : UserRequest) : UserResponse
}