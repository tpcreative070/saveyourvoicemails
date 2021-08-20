package co.tpcreative.domain.usecases
import co.tpcreative.common.Logger
import co.tpcreative.domain.interfaces.GithubDataSource
import co.tpcreative.domain.interfaces.SearchHistoryDataSource
import co.tpcreative.domain.models.SearchHistoryItem
import co.tpcreative.domain.models.SearchUsersResult

class SearchUsersUseCase(
    private val githubDataSource: GithubDataSource,
    private val searchHistoryDataSource: SearchHistoryDataSource,
    private val logger: Logger
) {
    operator fun invoke(query: String): SearchUsersResult {
        try {
            searchHistoryDataSource.add(SearchHistoryItem(query = query))
            logger.info("Query added to the search history")
        } catch (e: Exception) {
            logger.warn("An error occurred while adding a query to the search history", e)
        }
        return githubDataSource.searchUsers(query)
    }
}