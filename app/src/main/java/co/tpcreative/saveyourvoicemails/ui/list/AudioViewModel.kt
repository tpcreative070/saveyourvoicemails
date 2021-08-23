package co.tpcreative.saveyourvoicemails.ui.list
import co.tpcreative.common.Logger
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.domain.usecases.GetSearchHistoryUseCase
import co.tpcreative.domain.usecases.SearchUsersUseCase
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher

class AudioViewModel(
    private val searchUsersUseCase: SearchUsersUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val logger: Logger,
    private val ioDispatcher: CoroutineDispatcher,
    private val mainDispatcher: CoroutineDispatcher
) : BaseViewModel<GitHubUser>() {

    fun doSearch() {
    }
}