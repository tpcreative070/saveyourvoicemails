package co.tpcreative.data.voicemails
import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.domain.models.SearchUsersResult
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.domain.models.request.UserRequest
import co.tpcreative.domain.models.request.VoiceMailsRequest
import co.tpcreative.domain.models.response.UserResponse
import co.tpcreative.domain.models.response.VoiceMailsResponse
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitVoiceMailsDataSource(url : String) : VoiceMailsDataSource {

    private val voiceMailsService = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
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

    override fun getUser(username: String): GitHubUser {
        val response = voiceMailsService.getUser(username).execute()
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
        val response = voiceMailsService.signUp(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun insertVoiceMails(request: VoiceMailsRequest): VoiceMailsResponse {
        val response = voiceMailsService.signUp(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun deleteVoiceMails(request: VoiceMailsRequest): VoiceMailsResponse {
        val response = voiceMailsService.signUp(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun updateVoiceMails(request: VoiceMailsRequest): VoiceMailsResponse {
        val response = voiceMailsService.signUp(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }
}