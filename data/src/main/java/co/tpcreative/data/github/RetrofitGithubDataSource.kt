package co.tpcreative.data.github
import co.tpcreative.domain.interfaces.GithubDataSource
import co.tpcreative.domain.models.SearchUsersResult
import co.tpcreative.domain.models.User
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
class RetrofitGithubDataSource : GithubDataSource {

    companion object {
        private const val BASE_URL = "https://api.github.com/"
    }

    private val githubService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create()
            )
        )
        .build()
        .create(GithubService::class.java)

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
}