package co.tpcreative.domain.usecases

import co.tpcreative.domain.interfaces.VoiceMailsDataSource

class GetUserUseCase(private val githubDataSource: VoiceMailsDataSource) {
    operator fun invoke(username: String) = githubDataSource.getUser(username)
}