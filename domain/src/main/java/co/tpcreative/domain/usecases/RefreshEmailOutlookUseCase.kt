package co.tpcreative.domain.usecases

import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import co.tpcreative.domain.models.request.Mail365Request

class RefreshEmailOutlookUseCase(private val dataSource: VoiceMailsDataSource) {
    operator fun invoke(url: String?,request: MutableMap<String?, Any?>) = dataSource.refreshEmailOutlook(url,request)
}