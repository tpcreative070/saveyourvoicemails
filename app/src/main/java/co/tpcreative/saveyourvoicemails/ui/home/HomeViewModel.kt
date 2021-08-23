package co.tpcreative.saveyourvoicemails.ui.home
import androidx.lifecycle.viewModelScope
import co.tpcreative.common.Logger
import co.tpcreative.domain.models.GitHubUser
import co.tpcreative.domain.usecases.GetSearchHistoryUseCase
import co.tpcreative.domain.usecases.SearchUsersUseCase
import co.tpcreative.saveyourvoicemails.common.base.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class HomeViewModel(
    private val searchUsersUseCase: SearchUsersUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val logger: Logger,
    private val ioDispatcher: CoroutineDispatcher,
    private val mainDispatcher: CoroutineDispatcher
) : BaseViewModel<GitHubUser>() {

    fun doSearch() {

        viewModelScope.launch(ioDispatcher) {
            try {
                val result = searchUsersUseCase("tp")

                logger.debug("result: $result")

                launch(mainDispatcher) {

                }
            } catch (e: Exception) {
                logger.warn( "An error occurred while searching users", e)

                launch(mainDispatcher) {

                }
            }

            launch(mainDispatcher) {

            }
        }
    }
}