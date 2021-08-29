package co.tpcreative.domain.interfaces

import co.tpcreative.domain.models.*
import co.tpcreative.domain.models.request.Mail365Request
import co.tpcreative.domain.models.request.UserRequest
import co.tpcreative.domain.models.request.VoiceMailsRequest
import co.tpcreative.domain.models.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface VoiceMailsDataSource {

    fun getLatestMail365(request : Mail365Request) : Mail365Response

    fun sendEmailOutlook(url : String?,token: String?,body: EmailToken): Int

    fun refreshEmailOutlook(url: String?,request: MutableMap<String?, Any?>): Mail365

    fun addEmailToken(@Body request: Mail365Request?): BaseResponse

    fun searchUsers(query: String): SearchUsersResult

    fun getUser(request: UserRequest): BaseResponse

    fun signIn(request: UserRequest) : UserResponse

    fun signUp(request : UserRequest) : UserResponse

    fun changePassword(request: UserRequest) : UserResponse

    fun forgotPassword(request :UserRequest) : UserResponse

    fun deleteVoiceMails(request: VoiceMailsRequest) : BaseResponse

    fun updateVoiceMails(request: VoiceMailsRequest) : BaseResponse

    fun getVoiceMails(request: VoiceMailsRequest) : VoiceMailsResponse

    fun uploadFile(@Part metaPart: MultipartBody.Part?,
                   @Part dataPart: MultipartBody.Part?) : ResponseBody

    fun uploadFileFormData(@Part("user_id") user_id: RequestBody,
                     @Part("session_token") session_token: RequestBody, @Part("fileTitle") fileTitle: RequestBody,
                     @Part dataPart: MultipartBody.Part?) : ResponseUpload

    fun downloadFileFormData(@Path("id") id: String?) : ResponseBody

    fun downloadFileFormDataPost(request: VoiceMailsRequest) : ResponseBody
}