package co.tpcreative.domain.usecases

import co.tpcreative.domain.interfaces.SearchHistoryDataSource

class GetSearchHistoryUseCase(private val searchHistoryDataSource: SearchHistoryDataSource) {
    operator fun invoke() = searchHistoryDataSource.getAll()
}