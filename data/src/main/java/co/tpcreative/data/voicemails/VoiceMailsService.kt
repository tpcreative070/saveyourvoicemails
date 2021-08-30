package co.tpcreative.data.voicemails

import co.tpcreative.domain.models.BaseResponse
import co.tpcreative.domain.models.EmailToken
import co.tpcreative.domain.models.SearchUsersResult
import co.tpcreative.domain.models.request.Mail365Request
import co.tpcreative.domain.models.request.UserRequest
import co.tpcreative.domain.models.request.VoiceMailsRequest
import co.tpcreative.domain.models.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

internal interface VoiceMailsService {


    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/mail365/getLatestOutlook")
    fun getLatestOutlook(@Body request: Mail365Request): Call<Mail365Response>

    @Headers("Accept: application/json")
    @POST()
    fun sendEmailOutlook(@Url url : String?, @Header("Authorization") token: String?, @Body body: EmailToken): Call<ResponseBody>

    @FormUrlEncoded
    @POST
    fun refreshEmailOutlook(@Url url: String?, @FieldMap request: MutableMap<String?, Any?>): Call<Mail365>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/mail365/addOutlook")
    fun onAddEmailToken(@Body request: Mail365Request?): Call<BaseResponse>

    @GET("search/users")
    fun searchUsers(@Query("q") query: String): Call<SearchUsersResult>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/user/getUser")
    fun getUser(@Body request: UserRequest): Call<BaseResponse>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/user/signIn")
    fun signIn(@Body request: UserRequest): Call<UserResponse>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/user/signUp")
    fun signUp(@Body request: UserRequest): Call<UserResponse>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/user/changePassword")
    fun changePassword(@Body request: UserRequest): Call<UserResponse>

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
    ): Call<ResponseUpload>

    @GET("saveyourvoicemails/voiceApp/uploads/{id}")
    @Streaming
    fun downloadFile(@Path("id") id: String?) : Call<ResponseBody>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/mail/downloadFile")
    @Streaming
    fun downloadFilePost(@Body request: VoiceMailsRequest) : Call<ResponseBody>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/mail/getListVoiceMail")
    fun getVoiceMails(@Body request: VoiceMailsRequest): Call<VoiceMailsResponse>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/mail/deleteMail")
    fun deleteVoiceMails(@Body request: VoiceMailsRequest): Call<BaseResponse>

    @POST("saveyourvoicemails/voiceApp/vmsv2/v1/mail/updatedTitle")
    fun updateVoiceMails(@Body request: VoiceMailsRequest): Call<BaseResponse>
}