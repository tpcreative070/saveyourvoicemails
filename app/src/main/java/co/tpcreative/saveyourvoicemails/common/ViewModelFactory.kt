package co.tpcreative.saveyourvoicemails.common
import co.tpcreative.domain.usecases.SearchUsersUseCase
import co.tpcreative.domain.usecases.GetSearchHistoryUseCase
import androidx.lifecycle.ViewModel
import co.tpcreative.saveyourvoicemails.common.services.ServiceLocator
import co.tpcreative.saveyourvoicemails.ui.home.HomeViewModel
import androidx.lifecycle.ViewModelProvider
import co.tpcreative.saveyourvoicemails.ui.user.viewmodel.UserViewModel

class ViewModelFactory(private val serviceLocator: ServiceLocator) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(
                        SearchUsersUseCase(
                            serviceLocator.githubDataSource,
                            serviceLocator.searchHistoryDataSource,
                            serviceLocator.getLogger(SearchUsersUseCase::class)
                        ),
                        GetSearchHistoryUseCase(
                            serviceLocator.searchHistoryDataSource
                        ),
                        serviceLocator.getLogger(HomeViewModel::class),
                        serviceLocator.ioDispatcher,
                        serviceLocator.mainDispatcher
                    )
                isAssignableFrom(UserViewModel::class.java) ->
                    UserViewModel(
                        SearchUsersUseCase(
                            serviceLocator.githubDataSource,
                            serviceLocator.searchHistoryDataSource,
                            serviceLocator.getLogger(SearchUsersUseCase::class)
                        ),
                        GetSearchHistoryUseCase(
                            serviceLocator.searchHistoryDataSource
                        ),
                        serviceLocator.getLogger(UserViewModel::class),
                        serviceLocator.ioDispatcher,
                        serviceLocator.mainDispatcher
                    )
                else -> throw IllegalArgumentException("unknown model class $modelClass")
            }
        } as T
}