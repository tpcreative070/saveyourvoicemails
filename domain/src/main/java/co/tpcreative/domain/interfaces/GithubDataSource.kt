package co.tpcreative.domain.interfaces

import co.tpcreative.domain.models.SearchUsersResult
import co.tpcreative.domain.models.User

interface GithubDataSource {

    fun searchUsers(query: String): SearchUsersResult

    fun getUser(username: String): User
}