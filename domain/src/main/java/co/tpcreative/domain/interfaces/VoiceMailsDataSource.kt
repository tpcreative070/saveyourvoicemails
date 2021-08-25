package co.tpcreative.domain.interfaces

import co.tpcreative.domain.models.BaseResponse
import co.tpcreative.domain.models.SearchUsersResult
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.domain.models.UploadBody
import co.tpcreative.domain.models.request.UserRequest
import co.tpcreative.domain.models.request.VoiceMailsRequest
import co.tpcreative.domain.models.response.UserResponse
import co.tpcreative.domain.models.response.VoiceMailsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Part
import retrofit2.http.Path

interface VoiceMailsDataSource {

    fun searchUsers(query: String): SearchUsersResult

    fun getUser(username: String): GitHubUser

    fun signIn(request: UserRequest) : UserResponse

    fun signUp(request : UserRequest) : UserResponse

    fun changePassword(request: UserRequest) : UserResponse

    fun forgotPassword(request :UserRequest) : UserResponse

    fun deleteVoiceMails(request: VoiceMailsRequest) : VoiceMailsResponse

    fun updateVoiceMails(request: VoiceMailsRequest) : BaseResponse

    fun getVoiceMails(request: VoiceMailsRequest) : VoiceMailsResponse

    fun uploadFile(@Part metaPart: MultipartBody.Part?,
                   @Part dataPart: MultipartBody.Part?) : ResponseBody

    fun uploadFileFormData(@Part("user_id") user_id: RequestBody,
                     @Part("session_token") session_token: RequestBody, @Part("fileTitle") fileTitle: RequestBody,
                     @Part dataPart: MultipartBody.Part?) : BaseResponse

    fun downloadFileFormData(@Path("id") id: String?) : ResponseBody
}