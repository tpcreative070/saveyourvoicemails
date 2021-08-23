package co.tpcreative.domain.usecases
import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.domain.models.request.UserRequest
import co.tpcreative.domain.models.response.UserResponse

class SignInUsersUseCase(private val userDataSource: VoiceMailsDataSource) {
    operator fun invoke(request: UserRequest): UserResponse {
        return userDataSource.signIn(request)
    }
}