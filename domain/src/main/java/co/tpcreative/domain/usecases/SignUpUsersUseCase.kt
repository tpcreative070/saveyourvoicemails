package co.tpcreative.domain.usecases
import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.domain.models.request.UserRequest

class SignUpUsersUseCase(private val dataSource: VoiceMailsDataSource) {
    operator fun invoke(request: UserRequest) = dataSource.signUp(request)
}