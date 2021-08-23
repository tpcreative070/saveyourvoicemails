package co.tpcreative.domain.usecases

import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.domain.models.request.VoiceMailsRequest

class UpdateVoiceMailsUseCase(private val dataSource: VoiceMailsDataSource) {
    operator fun invoke(request: VoiceMailsRequest) = dataSource.updateVoiceMails(request)
}