package co.tpcreative.domain.usecases

import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.domain.models.request.VoiceMailsRequest

class DownloadFilePostVoiceMailsUseCase(private val dataSource: VoiceMailsDataSource) {
    operator fun invoke(request: VoiceMailsRequest) = dataSource.downloadFileFormDataPost(request)
}