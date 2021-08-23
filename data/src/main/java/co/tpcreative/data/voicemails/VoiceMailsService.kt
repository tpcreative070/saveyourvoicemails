package co.tpcreative.data.voicemails

import co.tpcreative.domain.models.SearchUsersResult
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.domain.models.request.UserRequest
import co.tpcreative.domain.models.response.UserResponse
import retrofit2.Call
import retrofit2.http.*

internal interface VoiceMailsService {

    @GET("search/users")
    fun searchUsers(@Query("q") query: String): Call<SearchUsersResult>

    @GET("users/{username}")
    fun getUser(@Path("username") username: String): Call<GitHubUser>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/user/signIn")
    fun signIn(@Body request : UserRequest): Call<UserResponse>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/user/signUp")
    fun signUp(@Body request : UserRequest): Call<UserResponse>
}