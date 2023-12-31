package co.tpcreative.saveyourvoicemails.common
import androidx.lifecycle.ViewModel
import co.tpcreative.saveyourvoicemails.common.services.ServiceLocator
import co.tpcreative.saveyourvoicemails.ui.list.AudioFragmentViewModel
import androidx.lifecycle.ViewModelProvider
import co.tpcreative.domain.usecases.*
import co.tpcreative.saveyourvoicemails.common.services.EmailOutlookService
import co.tpcreative.saveyourvoicemails.common.services.UploadDownloadService
import co.tpcreative.saveyourvoicemails.common.services.download.ProgressResponseBody
import co.tpcreative.saveyourvoicemails.ui.changepassword.ChangePasswordViewModel
import co.tpcreative.saveyourvoicemails.ui.main.MainActViewModel
import co.tpcreative.saveyourvoicemails.ui.me.MeViewModel
import co.tpcreative.saveyourvoicemails.ui.player.PlayerViewModel
import co.tpcreative.saveyourvoicemails.ui.share.ShareViewModel
import co.tpcreative.saveyourvoicemails.ui.trim.TrimViewModel
import co.tpcreative.saveyourvoicemails.ui.user.viewmodel.UserViewModel

class ViewModelFactory(private val serviceLocator: ServiceLocator,private val listener: ProgressResponseBody.ProgressResponseBodyListener?=null) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        with(modelClass) {
            when {
                isAssignableFrom(AudioFragmentViewModel::class.java) ->
                    AudioFragmentViewModel(
                        GetVoiceMailsUseCase(serviceLocator.voiceMailsDataSource),
                        UploadDownloadService(DownloadFilePostVoiceMailsUseCase(serviceLocator.voiceMailsDownloadDataSource(listener)),DownloadFileVoiceMailsUseCase(serviceLocator.voiceMailsDownloadDataSource(listener)),UploadFileFormDataVoiceMailsUseCase(serviceLocator.voiceMailsDataSource),UploadFileLogVoiceMailsUseCase(serviceLocator.voiceMailsDataSource)),
                        UpdateVoiceMailsUseCase(serviceLocator.voiceMailsDataSource),
                        DeleteVoiceMailUseCase(serviceLocator.voiceMailsDataSource),
                        serviceLocator.getLogger(AudioFragmentViewModel::class),
                        serviceLocator.ioDispatcher,
                        serviceLocator.mainDispatcher
                    )
                isAssignableFrom(UserViewModel::class.java) ->
                    UserViewModel(
                            GetLatestOutlookUseCase(serviceLocator.voiceMailsDataSource),
                            EmailOutlookViewModel(EmailOutlookService(SendEmailOutlookUseCase(serviceLocator.voiceMailsDataSource), RefreshEmailOutlookUseCase(serviceLocator.voiceMailsDataSource),AddEmailTokenUseCase(serviceLocator.voiceMailsDataSource))),
                            GetUserUseCase(serviceLocator.voiceMailsDataSource),
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
                        serviceLocator.ioDispatcher,
                        serviceLocator.mainDispatcher,
                        serviceLocator.getLogger(ShareViewModel::class),
                        UploadDownloadService(DownloadFilePostVoiceMailsUseCase(serviceLocator.voiceMailsDownloadDataSource(listener)),DownloadFileVoiceMailsUseCase(serviceLocator.voiceMailsDownloadDataSource(listener)),UploadFileFormDataVoiceMailsUseCase(serviceLocator.voiceMailsDataSource),UploadFileLogVoiceMailsUseCase(serviceLocator.voiceMailsDataSource))
                    )

                isAssignableFrom(PlayerViewModel::class.java) ->
                    PlayerViewModel(
                        serviceLocator.ioDispatcher,
                        serviceLocator.mainDispatcher,
                        serviceLocator.getLogger(PlayerViewModel::class),
                        UploadDownloadService(DownloadFilePostVoiceMailsUseCase(serviceLocator.voiceMailsDownloadDataSource(listener)),DownloadFileVoiceMailsUseCase(serviceLocator.voiceMailsDownloadDataSource(listener)),UploadFileFormDataVoiceMailsUseCase(serviceLocator.voiceMailsDataSource),UploadFileLogVoiceMailsUseCase(serviceLocator.voiceMailsDataSource))
                    )
                isAssignableFrom(ChangePasswordViewModel::class.java) ->
                    ChangePasswordViewModel(
                        ChangePasswordUserUseCase(serviceLocator.voiceMailsDataSource)
                    )
                isAssignableFrom(MeViewModel::class.java) ->
                    MeViewModel()
                isAssignableFrom(MainActViewModel::class.java) ->
                    MainActViewModel( EmailOutlookViewModel(EmailOutlookService(SendEmailOutlookUseCase(serviceLocator.voiceMailsDataSource), RefreshEmailOutlookUseCase(serviceLocator.voiceMailsDataSource),AddEmailTokenUseCase(serviceLocator.voiceMailsDataSource))), UploadDownloadService(DownloadFilePostVoiceMailsUseCase(serviceLocator.voiceMailsDownloadDataSource(listener)),DownloadFileVoiceMailsUseCase(serviceLocator.voiceMailsDownloadDataSource(listener)),UploadFileFormDataVoiceMailsUseCase(serviceLocator.voiceMailsDataSource),UploadFileLogVoiceMailsUseCase(serviceLocator.voiceMailsDataSource)))
                isAssignableFrom(TrimViewModel::class.java) ->
                    TrimViewModel( UploadDownloadService(DownloadFilePostVoiceMailsUseCase(serviceLocator.voiceMailsDownloadDataSource(listener)),DownloadFileVoiceMailsUseCase(serviceLocator.voiceMailsDownloadDataSource(listener)),UploadFileFormDataVoiceMailsUseCase(serviceLocator.voiceMailsDataSource),UploadFileLogVoiceMailsUseCase(serviceLocator.voiceMailsDataSource)))
                else -> throw IllegalArgumentException("unknown model class $modelClass")
            }
        } as T
}