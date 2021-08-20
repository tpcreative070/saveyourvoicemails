package co.tpcreative.saveyourvoicemails.common.services

import co.tpcreative.common.Logger
import co.tpcreative.domain.interfaces.GithubDataSource
import co.tpcreative.domain.interfaces.SearchHistoryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.reflect.KClass

interface ServiceLocator {

    val githubDataSource: GithubDataSource

    val searchHistoryDataSource: SearchHistoryDataSource

    val ioDispatcher: CoroutineDispatcher

    val mainDispatcher: CoroutineDispatcher

    fun getLogger(cls: KClass<*>): Logger
}