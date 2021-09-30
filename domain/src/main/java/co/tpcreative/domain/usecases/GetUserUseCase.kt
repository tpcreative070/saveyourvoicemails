package co.tpcreative.domain.usecases

import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.domain.models.request.UserRequest

class GetUserUseCase(private val githubDataSource: VoiceMailsDataSource) {
    operator fun invoke(request: UserRequest) = githubDataSource.getUser(request)
}