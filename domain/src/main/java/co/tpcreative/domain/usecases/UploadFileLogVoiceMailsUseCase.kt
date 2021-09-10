package co.tpcreative.domain.usecases

import co.tpcreative.domain.interfaces.VoiceMailsDataSource
import okhttp3.MultipartBody
import retrofit2.http.Part

class UploadFileLogVoiceMailsUseCase(private val dataSource: VoiceMailsDataSource) {
    operator fun invoke(@Part file: MultipartBody.Part?) = dataSource.uploadFileLog(file)
}