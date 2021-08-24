package co.tpcreative.saveyourvoicemails.common
import androidx.lifecycle.ViewModel
import co.tpcreative.saveyourvoicemails.common.services.ServiceLocator
import co.tpcreative.saveyourvoicemails.ui.list.AudioViewModel
import androidx.lifecycle.ViewModelProvider
import co.tpcreative.domain.usecases.*
import co.tpcreative.saveyourvoicemails.common.services.UploadDownloadService
import co.tpcreative.saveyourvoicemails.ui.main.MainActViewModel
import co.tpcreative.saveyourvoicemails.ui.share.ShareViewModel
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

                isAssignableFrom(ShareViewModel::class.java) ->
                    ShareViewModel(
                        InsertVoiceMailsUseCase(serviceLocator.voiceMailsDataSource),
                        serviceLocator.ioDispatcher,
                        serviceLocator.mainDispatcher,
                        serviceLocator.getLogger(ShareViewModel::class),
                        UploadDownloadService(UploadFileVoiceMailsUseCase(serviceLocator.voiceMailsDataSource),UploadFileFormDataVoiceMailsUseCase(serviceLocator.voiceMailsDataSource))
                    )
                isAssignableFrom(MainActViewModel::class.java) ->
                    MainActViewModel()
                else -> throw IllegalArgumentException("unknown model class $modelClass")
            }
        } as T
}