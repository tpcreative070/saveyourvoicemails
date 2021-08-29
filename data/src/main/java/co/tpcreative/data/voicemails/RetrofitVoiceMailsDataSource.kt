package co.tpcreative.data.voicemails
import android.util.Log
import co.tpcreative.data.BuildConfig
import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.domain.models.BaseResponse
import co.tpcreative.domain.models.EmailToken
import co.tpcreative.domain.models.SearchUsersResult
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.domain.models.request.Mail365Request
import co.tpcreative.domain.models.request.UserRequest
import co.tpcreative.domain.models.request.VoiceMailsRequest
import co.tpcreative.domain.models.response.*
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitVoiceMailsDataSource(url : String, client : OkHttpClient) : VoiceMailsDataSource {

    private val voiceMailsService = Retrofit.Builder()
        .baseUrl(url)
        .client(client)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .create()
            )
        )
        .build()
        .create(VoiceMailsService::class.java)

    override fun searchUsers(query: String): SearchUsersResult {
        val response = voiceMailsService.searchUsers(query).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun getUser(request: UserRequest): BaseResponse {
        val response = voiceMailsService.getUser(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun signIn(request: UserRequest): UserResponse {
        val response = voiceMailsService.signIn(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun signUp(request: UserRequest): UserResponse {
        val response = voiceMailsService.signUp(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun forgotPassword(request: UserRequest): UserResponse {
        val response = voiceMailsService.signUp(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun changePassword(request: UserRequest): UserResponse {
        val response = voiceMailsService.signUp(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun getVoiceMails(request: VoiceMailsRequest): VoiceMailsResponse {
        val response = voiceMailsService.getVoiceMails(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun deleteVoiceMails(request: VoiceMailsRequest): BaseResponse {
        val response = voiceMailsService.deleteVoiceMails(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun updateVoiceMails(request: VoiceMailsRequest): BaseResponse {
        val response = voiceMailsService.updateVoiceMails(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun uploadFile(
        metaPart: MultipartBody.Part?,
        dataPart: MultipartBody.Part?
    ): ResponseBody {
        val response = voiceMailsService.uploadFile(metaPart,dataPart).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun uploadFileFormData(
        user_id: RequestBody,
        session_token: RequestBody,
        fileTitle: RequestBody,
        dataPart: MultipartBody.Part?
    ): ResponseUpload {
        val response = voiceMailsService.uploadFileFormData(user_id,session_token,fileTitle,dataPart).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun downloadFileFormData(id: String?): ResponseBody {
        val response = voiceMailsService.downloadFile(id).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun downloadFileFormDataPost(request: VoiceMailsRequest): ResponseBody {
        val response = voiceMailsService.downloadFilePost(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun getLatestMail365(request: Mail365Request): Mail365Response {
        val response = voiceMailsService.getLatestOutlook(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun sendEmailOutlook(url: String?, token: String?, body: EmailToken): Int {
        val response = voiceMailsService.sendEmailOutlook(url,token,body).execute()
        if (response.isSuccessful){
            return response.code()
        }
        return response.code()
    }

    override fun refreshEmailOutlook(url: String?, request: MutableMap<String?, Any?>): Mail365 {
        val response = voiceMailsService.refreshEmailOutlook(url,request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun addEmailToken(request: Mail365Request?): BaseResponse {
        val response = voiceMailsService.onAddEmailToken(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }
}