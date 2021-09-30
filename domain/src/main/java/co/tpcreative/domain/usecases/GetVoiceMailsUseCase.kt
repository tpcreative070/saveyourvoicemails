package co.tpcreative.domain.usecases
import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.domain.models.request.VoiceMailsRequest

class GetVoiceMailsUseCase(private val dataSource: VoiceMailsDataSource) {
    operator fun invoke(request: VoiceMailsRequest) = dataSource.getVoiceMails(request)
}