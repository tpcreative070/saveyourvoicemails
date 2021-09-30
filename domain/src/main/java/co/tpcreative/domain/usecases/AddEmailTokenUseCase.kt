package co.tpcreative.domain.usecases

import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.domain.models.request.Mail365Request

class AddEmailTokenUseCase(private val dataSource: VoiceMailsDataSource) {
    operator fun invoke(request: Mail365Request) = dataSource.addEmailToken(request)
}