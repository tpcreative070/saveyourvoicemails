package co.tpcreative.domain.interfaces

import co.tpcreative.domain.models.SearchHistoryItem

interface SearchHistoryDataSource {

    fun getAll(): List<SearchHistoryItem>

    fun add(searchHistoryItem: SearchHistoryItem)
}