package co.tpcreative.data.voicemails
import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.domain.models.SearchUsersResult
import co.tpcreative.domain.models.User
import co.tpcreative.domain.models.request.UserRequest
import co.tpcreative.domain.models.response.UserResponse
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitVoiceMailsDataSource(url : String) : VoiceMailsDataSource {

    private val githubService = Retrofit.Builder()
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
        val response = githubService.searchUsers(query).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun getUser(username: String): User {
        val response = githubService.getUser(username).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun signIn(request: UserRequest): UserResponse {
        val response = githubService.signIn(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }

    override fun signUp(request: UserRequest): UserResponse {
        val response = githubService.signUp(request).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception(response.message())
        }
    }
}