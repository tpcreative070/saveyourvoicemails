package co.tpcreative.domain.usecases
import co.tpcreative.domain.interfaces.VoiceMailsDataSource

class DownloadFileVoiceMailsUseCase(private val dataSource: VoiceMailsDataSource) {
    operator fun invoke(id : String) = dataSource.downloadFileFormData(id)
}