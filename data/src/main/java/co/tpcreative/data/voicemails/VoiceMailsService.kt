package co.tpcreative.data.voicemails

import co.tpcreative.domain.models.BaseResponse
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.domain.models.SearchUsersResult
import co.tpcreative.domain.models.request.UserRequest
import co.tpcreative.domain.models.request.VoiceMailsRequest
import co.tpcreative.domain.models.response.UserResponse
import co.tpcreative.domain.models.response.VoiceMailsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

internal interface VoiceMailsService {

    @GET("search/users")
    fun searchUsers(@Query("q") query: String): Call<SearchUsersResult>

    @GET("users/{username}")
    fun getUser(@Path("username") username: String): Call<GitHubUser>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/user/signIn")
    fun signIn(@Body request: UserRequest): Call<UserResponse>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/user/signUp")
    fun signUp(@Body request: UserRequest): Call<UserResponse>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/user/signUp")
    fun signUp(@Body request: VoiceMailsRequest): Call<VoiceMailsResponse>

    @POST("saveyourvoicemails/voiceApp/vmsv2/include/fileUpload.php")
    @Multipart
    fun uploadFile(
        @Part metaPart: MultipartBody.Part?, @Part dataPart: MultipartBody.Part?,
    ): Call<ResponseBody>

    @POST("saveyourvoicemails/voiceApp/vmsv2/include/fileUpload.php")
    @Multipart
    fun uploadFileFormData(
        @Part("user_id") user_id: RequestBody,
        @Part("session_token") session_token: RequestBody,
        @Part("fileTitle") fileTitle: RequestBody,
        @Part dataPart: MultipartBody.Part?,
    ): Call<BaseResponse>

    @GET("saveyourvoicemails/voiceApp/uploads/{id}")
    @Streaming
    fun downloadFile(@Path("id") id: String?) : Call<ResponseBody>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/mail/getListVoiceMail")
    fun getVoiceMails(@Body request: VoiceMailsRequest): Call<VoiceMailsResponse>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/mail/deleteMail")
    fun deleteVoiceMails(@Body request: VoiceMailsRequest): Call<VoiceMailsResponse>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/mail/updatedTitle")
    fun updateVoiceMails(@Body request: VoiceMailsRequest): Call<VoiceMailsResponse>
}