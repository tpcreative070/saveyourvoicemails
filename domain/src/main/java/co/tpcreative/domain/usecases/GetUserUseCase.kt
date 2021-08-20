package co.tpcreative.domain.usecases

import co.tpcreative.domain.interfaces.GithubDataSource

class GetUserUseCase(private val githubDataSource: GithubDataSource) {
    operator fun invoke(username: String) = githubDataSource.getUser(username)
}