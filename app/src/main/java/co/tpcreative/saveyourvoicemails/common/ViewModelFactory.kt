package co.tpcreative.saveyourvoicemails.common
import co.tpcreative.domain.usecases.SearchUsersUseCase
import co.tpcreative.domain.usecases.GetSearchHistoryUseCase
import androidx.lifecycle.ViewModel
import co.tpcreative.saveyourvoicemails.common.services.ServiceLocator
import co.tpcreative.saveyourvoicemails.ui.list.AudioViewModel
import androidx.lifecycle.ViewModelProvider
import co.tpcreative.domain.usecases.SignInUsersUseCase
import co.tpcreative.domain.usecases.SignUpUsersUseCase
import co.tpcreative.saveyourvoicemails.ui.main.MainActViewModel
import co.tpcreative.saveyourvoicemails.ui.user.viewmodel.UserViewModel

class ViewModelFactory(private val serviceLocator: ServiceLocator) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(AudioViewModel::class.java) ->
                    AudioViewModel(
                        SearchUsersUseCase(
                            serviceLocator.voiceMailsDataSource,
                            serviceLocator.searchHistoryDataSource,
                            serviceLocator.getLogger(SearchUsersUseCase::class)
                        ),
                        GetSearchHistoryUseCase(
                            serviceLocator.searchHistoryDataSource
                        ),
                        serviceLocator.getLogger(AudioViewModel::class),
                        serviceLocator.ioDispatcher,
                        serviceLocator.mainDispatcher
                    )
                isAssignableFrom(UserViewModel::class.java) ->
                    UserViewModel(
                            SignUpUsersUseCase(serviceLocator.voiceMailsDataSource),
                            SignInUsersUseCase(serviceLocator.voiceMailsDataSource),
                            SearchUsersUseCase(
                            serviceLocator.voiceMailsDataSource,
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
                isAssignableFrom(MainActViewModel::class.java) ->
                    MainActViewModel()
                else -> throw IllegalArgumentException("unknown model class $modelClass")
            }
        } as T
}