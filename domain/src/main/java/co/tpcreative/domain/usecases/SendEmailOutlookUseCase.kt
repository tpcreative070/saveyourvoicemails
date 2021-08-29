package co.tpcreative.domain.usecases

import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.domain.models.EmailToken
import co.tpcreative.domain.models.request.Mail365Request

class SendEmailOutlookUseCase(private val dataSource: VoiceMailsDataSource) {
    operator fun invoke(url : String?,token: String?,body: EmailToken) = dataSource.sendEmailOutlook(url,token,body)
}